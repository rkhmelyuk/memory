package com.khmelyuk.memory.vm.table;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.metrics.Metrics;
import com.khmelyuk.memory.metrics.MetricsSnapshot;
import com.khmelyuk.memory.metrics.MetricsSnapshotBuilder;
import com.khmelyuk.memory.metrics.TimeContext;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Virtual memory table based on linked lists.
 * Works the best for small number of threads.
 *
 * @author Ruslan Khmelyuk
 */
public class LinkedVirtualMemoryTable implements VirtualMemoryTable {

    private final ReadWriteLock usedLock = new ReentrantReadWriteLock();
    private final ReadWriteLock freeLock = new ReentrantReadWriteLock();

    private final LinkedList<TableBlock> used = new LinkedList<>();
    private final LinkedList<TableBlock> free = new LinkedList<>();

    private final AtomicInteger freeMemorySize;
    private final AtomicInteger usedMemorySize;

    private final Metrics metrics;

    public LinkedVirtualMemoryTable(int size) {
        free.add(new TableBlock(0, size));

        usedMemorySize = new AtomicInteger(0);
        freeMemorySize = new AtomicInteger(size);

        // add metrics
        metrics = new Metrics();
        metrics.addValueMetric("vmtable.totalAllocations");
        metrics.addValueMetric("vmtable.failedAllocations");
        metrics.addValueMetric("vmtable.totalFrees");
        metrics.addValueMetric("vmtable.failedFrees");
        metrics.addValueMetric("vmtable.increases");
        metrics.addValueMetric("vmtable.loopsToFindFitBlock");
        metrics.addValueMetric("vmtable.fragmentation");
        metrics.addValueMetric("vmtable.freeSize");
        metrics.addValueMetric("vmtable.usedSize");
        metrics.addValueMetric("vmtable.freeBlocksCount");
        metrics.addValueMetric("vmtable.usedBlocksCount");
        metrics.addTimerMetric("vmtable.allocationTime");
        metrics.addTimerMetric("vmtable.freeTime");
    }

    @Override
    public Collection<Block> getUsed() {
        return Collections.<Block>unmodifiableCollection(used);
    }

    @Override
    public Collection<Block> getFree() {
        return Collections.<Block>unmodifiableCollection(free);
    }

    @Override
    public MetricsSnapshot getMetrics() {
        return new MetricsSnapshotBuilder().fromMetrics(metrics).build();
    }

    @Override
    public Block allocate(int size) {
        if (size <= 0) {
            throw new OutOfBoundException("Size can't be negative or be zero: " + size);
        }

        metrics.increment("vmtable.totalAllocations");

        TimeContext timer = metrics.getTimer("vmtable.allocationTime");
        timer.start();

        final TableBlock freeBlock = findBlockToAllocateFrom(size);
        if (freeBlock != null) {
            final TableBlock result;
            try {
                result = new TableBlock(freeBlock.getAddress(), size);
                if (freeBlock.getSize() == size) {
                    freeBlock.resize(0, 0);
                    removeBlock(free, freeBlock, freeLock);
                    metrics.decrement("vmtable.fragmentation");
                } else {
                    freeBlock.resize(
                            freeBlock.getAddress() + size,
                            freeBlock.getSize() - size);
                    metrics.increment("vmtable.fragmentation");
                }
                freeMemorySize.addAndGet(-size);
            } finally {
                // unlock asap
                freeBlock.unlock();
            }

            insertBlock(used, result, usedLock);

            usedMemorySize.addAndGet(size);

            timer.stop();
            metrics.mark("vmtable.freeSize", freeMemorySize.longValue());
            metrics.mark("vmtable.usedSize", usedMemorySize.longValue());
            metrics.mark("vmtable.freeBlocksCount", free.size());
            metrics.mark("vmtable.usedBlocksCount", used.size());

            return result;
        }

        timer.stop();


        metrics.increment("vmtable.failedAllocations");

        return null;
    }

    protected TableBlock findBlockToAllocateFrom(int size) {

        boolean repeat;
        do {
            repeat = false;
            try {
                freeLock.readLock().lock();
                for (TableBlock each : free) {
                    if (each.getSize() >= size) {
                        if (each.lock()) {
                            if (each.getSize() >= size) {
                                return each;
                            }
                            each.unlock();
                        } else {
                            // looks like there was a block that was enough to allocate from
                            // but now it's locked, so need to loop the list of blocks again.
                            repeat = true;
                        }
                    }
                }
            } finally {
                freeLock.readLock().unlock();
            }
            metrics.increment("vmtable.loopsToFindFitBlock");
        }
        while (repeat);

        return null;
    }

    @Override
    public boolean free(Block block) {
        if (block == null) {
            return false;
        }

        metrics.increment("vmtable.totalFrees");

        TimeContext timer = metrics.getTimer("vmtable.freeTime");
        timer.start();

        TableBlock tableBlock = getSimilarBlock(used, block, usedLock);
        if (tableBlock != null) {
            if (removeBlock(used, tableBlock, usedLock)) {
                int size = tableBlock.getSize();
                usedMemorySize.addAndGet(-size);

                addFreeBlock(new TableBlock(
                        tableBlock.getAddress(),
                        tableBlock.getSize()));

                freeMemorySize.addAndGet(size);

                tableBlock.resize(0, 0);
                tableBlock.unlock();

                timer.stop();

                metrics.mark("vmtable.freeSize", freeMemorySize.longValue());
                metrics.mark("vmtable.usedSize", usedMemorySize.longValue());
                metrics.mark("vmtable.freeBlocksCount", free.size());
                metrics.mark("vmtable.usedBlocksCount", used.size());

                return true;
            }
        }

        timer.stop();

        metrics.increment("vmtable.failedFrees");

        return false;
    }

    /**
     * Add a block to the free memory list.
     *
     * @param block the block that need to be added to the free memory list.
     */
    protected void addFreeBlock(TableBlock block) {
        if (!extendFreeMemory(block)) {
            insertBlock(free, block, freeLock);
            metrics.increment("vmtable.fragmentation");
        }
    }

    /**
     * Tries to find and extend a free memory with specified block.
     * If memory can't be extended with such block, then returns false.
     *
     * @param block the memory block to extend free memory with.
     * @return true if memory was extended, otherwise false.
     */
    protected boolean extendFreeMemory(TableBlock block) {
        final int blockAddress = block.getAddress();
        final int blockEnd = block.getEnd();

        TableBlock head = null;
        TableBlock tail = null;
        boolean repeat;
        do {
            repeat = false;
            try {
                freeLock.readLock().lock();
                for (TableBlock each : free) {
                    if (head == null && each.getAddress() == blockEnd) {
                        if (each.lock()) {
                            if (each.getAddress() != blockEnd) {
                                each.unlock();
                            } else {
                                head = each;
                            }
                        } else {
                            repeat = true;
                        }
                    } else if (tail == null && each.getEnd() == blockAddress) {
                        if (each.lock()) {
                            if (each.getEnd() != blockAddress) {
                                each.unlock();
                            } else {
                                tail = each;
                            }
                        } else {
                            repeat = true;
                        }
                    }
                    if (head != null && tail != null) {
                        repeat = false;
                        break;
                    }
                }
            } finally {
                freeLock.readLock().unlock();
            }
        }
        while (repeat);


        // if there is a head or tail - then resize and return true
        if (tail != null) {
            if (head == null) {
                // only tail is found
                tail.setSize(block.getSize() + tail.getSize());
            } else {
                // head is found, so we just resize tail and remove head
                tail.setSize(block.getSize() + tail.getSize() + head.getSize());
                removeBlock(free, head, freeLock);

                head.unlock();

                // we want to decrease fragmentation twice for this case, because we merged 3 blocks into 1
                // so this is decrement #1 and the next is at the end of method
                metrics.decrement("vmtable.fragmentation");
            }

            tail.unlock();
        } else if (head != null) {
            head.resize(blockAddress, block.getSize() + head.getSize());
            head.unlock();
        } else {
            // nothing was changed
            return false;
        }

        metrics.decrement("vmtable.fragmentation");
        return true;
    }

    @Override
    public int getFreeMemorySize() {
        return freeMemorySize.get();
    }

    @Override
    public int getUsedMemorySize() {
        return usedMemorySize.get();
    }

    @Override
    public void reset(int size) {
        // reset allocations/frees count first, as it's not used
        // for any functionality but to show information
        metrics.reset();

        try {
            usedLock.writeLock().lock();
            used.clear();
            usedMemorySize.set(0);
        } finally {
            usedLock.writeLock().unlock();
        }

        try {
            freeLock.writeLock().lock();
            free.clear();
            free.add(new TableBlock(0, size));
            freeMemorySize.set(size);
        } finally {
            freeLock.writeLock().unlock();
        }
    }

    @Override
    public boolean canIncreaseSize(int size) {
        final int freeSize = freeMemorySize.get();
        final int usedSize = usedMemorySize.get();
        final int totalSize = freeSize + usedSize;

        return !(size < usedSize || size <= totalSize);
    }

    @Override
    public void increaseSize(int size) {
        final int freeSize = freeMemorySize.get();
        final int usedSize = usedMemorySize.get();
        final int totalSize = freeSize + usedSize;

        // increase memory size
        int incSize = size - totalSize;
        addFreeBlock(new TableBlock(totalSize, incSize));
        freeMemorySize.addAndGet(incSize);
        metrics.increment("vmtable.increases");
    }

    /**
     * Gets the same block or block with such address and size.
     *
     * @param list  the list to find a similar block in.
     * @param block the block to find similar to it.
     * @param lock  the lock to avoid concurrency issues.
     * @return the found similar block or null.
     */
    private static TableBlock getSimilarBlock(List<TableBlock> list, Block block, ReadWriteLock lock) {
        boolean repeat;
        do {
            repeat = false;
            try {
                lock.readLock().lock();
                for (TableBlock each : list) {
                    if (each.equals(block)) {
                        if (each.lock()) {
                            if (each.equals(block)) {
                                return each;
                            }
                            each.unlock();
                        } else {
                            repeat = true;
                        }
                    }
                }
            } finally {
                lock.readLock().unlock();
            }
        }
        while (repeat);

        return null;
    }

    private static void insertBlock(LinkedList<TableBlock> table, TableBlock block, ReadWriteLock lock) {
        try {
            lock.writeLock().lock();
            table.addFirst(block);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static boolean removeBlock(List<TableBlock> table, TableBlock block, ReadWriteLock lock) {
        try {
            lock.writeLock().lock();
            return table.remove(block);
        } finally {
            lock.writeLock().unlock();
        }
    }
}

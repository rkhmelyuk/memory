package com.khmelyuk.memory.vm.table;

import com.khmelyuk.memory.OutOfBoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Virtual memory table based on queue.
 * <p/>
 * Works the best for large number of threads,
 * for small number of threads {@link LinkedVirtualMemoryTable} should be preferred.
 *
 * @author Ruslan Khmelyuk
 */
public class QueueVirtualMemoryTable implements VirtualMemoryTable {

    private final ConcurrentLinkedQueue<TableBlock> used = new ConcurrentLinkedQueue<TableBlock>();
    private final ConcurrentLinkedQueue<TableBlock> free = new ConcurrentLinkedQueue<TableBlock>();

    private AtomicInteger freeMemorySize;
    private AtomicInteger usedMemorySize;

    // point next free block
    private volatile TableBlock freeAnchor;

    public QueueVirtualMemoryTable(int size) {
        freeAnchor = new TableBlock(0, size);
        free.add(freeAnchor);

        usedMemorySize = new AtomicInteger(0);
        freeMemorySize = new AtomicInteger(size);
    }

    public Collection<TableBlock> getUsed() {
        return Collections.unmodifiableCollection(used);
    }

    public Collection<TableBlock> getFree() {
        return Collections.unmodifiableCollection(free);
    }

    public Block allocate(int size) {
        if (size < 0) {
            throw new OutOfBoundException("Size can't be negative: " + size);
        }

        final TableBlock freeBlock = getBlockToAllocate(size);
        if (freeBlock != null) {
            final TableBlock result;
            try {
                result = new TableBlock(freeBlock.getAddress(), size);
                if (freeBlock.getSize() == size) {
                    freeBlock.resize(0, 0);
                    free.remove(freeBlock);
                }
                else {
                    freeBlock.resize(
                            freeBlock.getAddress() + size,
                            freeBlock.getSize() - size);
                }
            }
            finally {
                // unlock asap
                freeBlock.unlock();
                if (freeBlock.getSize() > 0) {
                    freeAnchor = freeBlock;
                }
            }

            used.offer(result);

            usedMemorySize.addAndGet(size);
            freeMemorySize.addAndGet(-size);

            return result;
        }
        return null;
    }

    protected TableBlock getBlockToAllocate(int size) {
        // simple performance optimization
        final TableBlock anchor = freeAnchor;
        if (anchor.getSize() >= size) {
            if (anchor.lock()) {
                if (anchor.getSize() >= size) {
                    return anchor;
                }
                anchor.unlock();
            }
        }

        boolean repeat;
        do {
            repeat = false;
            for (TableBlock each : free) {
                if (each.getSize() >= size) {
                    if (each.lock()) {
                        if (each.getSize() >= size) {
                            return each;
                        }
                        each.unlock();
                    }
                    else {
                        repeat = true;
                    }
                }
            }
        }
        while (repeat);

        return null;
    }

    public boolean free(Block block) {
        TableBlock tableBlock = getSimilarBlock(used, block);
        if (tableBlock != null) {
            if (used.remove(tableBlock)) {
                addFreeBlock(new TableBlock(
                        tableBlock.getAddress(),
                        tableBlock.getSize()));

                int size = tableBlock.getSize();
                freeMemorySize.addAndGet(size);
                usedMemorySize.addAndGet(-size);

                tableBlock.resize(0, 0);
                tableBlock.unlock();

                return true;
            }
        }
        return false;
    }

    /**
     * Add a block to the free memory list.
     *
     * @param block the block that need to be added to the free memory list.
     */
    protected void addFreeBlock(TableBlock block) {
        if (!extendFreeMemory(block)) {
            free.offer(block);
            freeAnchor = block;
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
            for (TableBlock each : free) {
                if (head == null && each.getAddress() == blockEnd) {
                    if (each.lock()) {
                        if (each.getAddress() != blockEnd) {
                            each.unlock();
                        }
                        else {
                            head = each;
                        }
                    }
                    else {
                        repeat = true;
                    }
                }
                else if (tail == null && each.getEnd() == blockAddress) {
                    if (each.lock()) {
                        if (each.getEnd() != blockAddress) {
                            each.unlock();
                        }
                        else {
                            tail = each;
                        }
                    }
                    else {
                        repeat = true;
                    }
                }
                if (head != null && tail != null) {
                    repeat = false;
                    break;
                }
            }
        }
        while (repeat);

        // if there is a head or tail - then resize and return true
        if (tail != null) {
            if (head == null) {
                // only tail is found
                tail.setSize(block.getSize() + tail.getSize());
            }
            else {
                // head is found, so we just resize tail and remove head
                tail.setSize(block.getSize() + tail.getSize() + head.getSize());
                free.remove(head);

                head.unlock();
            }

            tail.unlock();

            return true;
        }
        else if (head != null) {
            head.resize(blockAddress, block.getSize() + head.getSize());
            head.unlock();

            return true;
        }

        return false;
    }

    public int getFreeMemorySize() {
        return freeMemorySize.get();
    }

    public int getUsedMemorySize() {
        return usedMemorySize.get();
    }

    public void reset(int size) {
        used.clear();
        usedMemorySize.set(0);

        free.clear();
        freeMemorySize.set(0);
        freeAnchor = new TableBlock(0, size);
        free.add(freeAnchor);
    }

    public boolean canIncreaseSize(int size) {
        final int freeSize = freeMemorySize.get();
        final int usedSize = usedMemorySize.get();
        final int totalSize = freeSize + usedSize;

        return !(size < usedSize || size <= totalSize);
    }

    public void increaseSize(int size) {
        final int freeSize = freeMemorySize.get();
        final int usedSize = usedMemorySize.get();
        final int totalSize = freeSize + usedSize;

        // increase memory size
        int incSize = size - totalSize;
        addFreeBlock(new TableBlock(totalSize, incSize));
        freeMemorySize.addAndGet(incSize);
    }

    /**
     * Gets the same block or block with such address and size.
     *
     * @param list  the list to find a similar block in.
     * @param block the block to find similar to it.
     * @return the found similar block or null.
     */
    private static TableBlock getSimilarBlock(Queue<TableBlock> list, Block block) {
        boolean repeat;
        do {
            repeat = false;
            for (TableBlock each : list) {
                if (each.equals(block)) {
                    if (each.lock()) {
                        if (each.equals(block)) {
                            return each;
                        }
                        each.unlock();
                    }
                    else {
                        repeat = true;
                    }
                }
            }

        }
        while (repeat);
        return null;
    }
}

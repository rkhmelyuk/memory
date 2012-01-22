package com.khmelyuk.memory.vm.table;

import com.khmelyuk.memory.OutOfBoundException;

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

    private final LinkedList<TableBlock> used = new LinkedList<TableBlock>();
    private final LinkedList<TableBlock> free = new LinkedList<TableBlock>();

    private AtomicInteger freeMemorySize;
    private AtomicInteger usedMemorySize;

    // point next free block
    //private volatile TableBlock freeAnchor;

    public LinkedVirtualMemoryTable(int size) {
        TableBlock freeAnchor = new TableBlock(0, size);
        free.add(freeAnchor);

        usedMemorySize = new AtomicInteger(0);
        freeMemorySize = new AtomicInteger(size);
    }

    public Collection<Block> getUsed() {
        return Collections.<Block>unmodifiableCollection(used);
    }

    public Collection<Block> getFree() {
        return Collections.<Block>unmodifiableCollection(free);
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
                    removeBlock(free, freeBlock, freeLock);
                }
                else {
                    freeBlock.resize(
                            freeBlock.getAddress() + size,
                            freeBlock.getSize() - size);
                }
                freeMemorySize.addAndGet(-size);
            }
            finally {
                // unlock asap
                freeBlock.unlock();
                /*if (freeBlock.getSize() > 0) {
                    freeAnchor = freeBlock;
                }*/
            }

            insertBlock(used, result, usedLock);

            usedMemorySize.addAndGet(size);

            return result;
        }
        return null;
    }

    protected TableBlock getBlockToAllocate(int size) {
        // simple performance optimization
        /*
        TODO - investigate - stats shows it's faster, less defragmented if not used freeAnchor
        final TableBlock anchor = freeAnchor;
        if (anchor.getSize() >= size) {
            if (anchor.lock()) {
                if (anchor.getSize() >= size) {
                    return anchor;
                }
                anchor.unlock();

            }
        }
        */

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
                        }
                        else {
                            repeat = true;
                        }
                    }
                }
            }
            finally {
                freeLock.readLock().unlock();
            }
        }
        while (repeat);

        return null;
    }

    public boolean free(Block block) {
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
            insertBlock(free, block, freeLock);
            //freeAnchor = block;
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
            finally {
                freeLock.readLock().unlock();
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
                removeBlock(free, head, freeLock);

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
        try {
            usedLock.writeLock().lock();
            used.clear();
            usedMemorySize.set(0);
        }
        finally {
            usedLock.writeLock().unlock();
        }

        try {
            freeLock.writeLock().lock();
            free.clear();
            free.add(new TableBlock(0, size));
            freeMemorySize.set(0);
        }
        finally {
            freeLock.writeLock().unlock();
        }
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
                        }
                        else {
                            repeat = true;
                        }
                    }
                }
            }
            finally {
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
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    private static boolean removeBlock(List<TableBlock> table, TableBlock block, ReadWriteLock lock) {
        try {
            lock.writeLock().lock();
            return table.remove(block);
        }
        finally {
            lock.writeLock().unlock();
        }
    }
}

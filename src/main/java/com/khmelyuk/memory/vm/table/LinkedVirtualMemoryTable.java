package com.khmelyuk.memory.vm.table;

import com.khmelyuk.memory.OutOfBoundException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Virtual memory table based on linked lists.
 *
 * @author Ruslan Khmelyuk
 */
public class LinkedVirtualMemoryTable implements VirtualMemoryTable {

    private final ReadWriteLock usedLock = new ReentrantReadWriteLock();
    private final ReadWriteLock freeLock = new ReentrantReadWriteLock();

    private final LinkedList<TableBlock> used = new LinkedList<TableBlock>();
    private final LinkedList<TableBlock> free = new LinkedList<TableBlock>();

    private TableBlock nextBlock;
    private AtomicInteger freeMemorySize;
    private AtomicInteger usedMemorySize;

    public LinkedVirtualMemoryTable(int size) {
        nextBlock = new TableBlock(0, size);
        free.add(nextBlock);

        usedMemorySize = new AtomicInteger(0);
        freeMemorySize = new AtomicInteger(size);
    }

    public Collection<TableBlock> getUsed() {
        return used;
    }

    public Collection<TableBlock> getFree() {
        return free;
    }

    public Block allocate(int size) {
        if (size < 0) {
            throw new OutOfBoundException("Size can't be negative: " + size);
        }

        final TableBlock freeBlock = getBlockToAllocate(size);
        if (freeBlock != null) {
            final TableBlock result;
            if (freeBlock.getSize() == size) {
                result = freeBlock;
                removeBlock(free, freeBlock, freeLock);
            }
            else {
                result = new TableBlock(freeBlock.getAddress(), size);
                freeBlock.resize(
                        freeBlock.getAddress() + size,
                        freeBlock.getSize() - size);

                //nextBlock = freeBlock;
            }

            insertBlock(used, result, usedLock);

            freeBlock.unlock();

            usedMemorySize.addAndGet(size);
            freeMemorySize.addAndGet(-size);

            return result;
        }
        return null;
    }

    protected TableBlock getBlockToAllocate(int size) {
        /*if (nextBlock.lock()) {
            if (nextBlock.getSize() <= size) {
                return nextBlock;
            }
        }
        */

        try {
            freeLock.readLock().lock();

            for (TableBlock each : free) {
                if (each != null && each.getSize() >= size && each.lock()) {
                    if (each.getSize() >= size) {
                        return each;
                    }
                    each.unlock();
                }
            }
            return null;
        }
        finally {
            freeLock.readLock().unlock();
        }
    }

    public boolean free(Block block) {
        TableBlock tableBlock = getSimilarBlock(used, block, usedLock);
        if (tableBlock != null) {
            if (removeBlock(used, tableBlock, usedLock)) {
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
            insertBlock(free, block, freeLock);
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
        try {
            freeLock.readLock().lock();

            for (TableBlock each : free) {
                if (head == null && each.getAddress() == blockEnd && each.lock()) {
                    if (each.getAddress() != blockEnd) {
                        each.unlock();
                    }
                    else {
                        head = each;
                    }
                }
                else if (tail == null && each.getEnd() == blockAddress && each.lock()) {
                    if (each.getEnd() != blockAddress) {
                        each.unlock();
                    }
                    else {
                        tail = each;
                    }
                }
                if (head != null && tail != null) {
                    break;
                }
            }
        }
        finally {
            freeLock.readLock().unlock();
        }

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
            nextBlock.unlock();
            nextBlock = new TableBlock(0, size);
            free.add(nextBlock);
            freeMemorySize.set(0);
        }
        finally {
            freeLock.writeLock().unlock();
        }

    }

    @Override
    public boolean increaseSize(int size) {
        final int freeSize = freeMemorySize.get();
        final int usedSize = usedMemorySize.get();
        final int totalSize = freeSize + usedSize;

        if (size < usedSize || size <= totalSize) {
            return false;
        }

        // increase memory size
        int incSize = size - totalSize;
        addFreeBlock(new TableBlock(totalSize, incSize));
        freeMemorySize.addAndGet(incSize);

        return true;
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
        try {
            lock.readLock().lock();
            for (TableBlock each : list) {
                if (each.equals(block) && each.lock()) {
                    if (!each.equals(block)) {
                        each.unlock();
                    }
                    return each;
                }
            }
            return null;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    // TODO - list of modifications if tryLock() returns false.

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

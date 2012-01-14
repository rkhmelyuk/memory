package com.khmelyuk.memory.vm.table;

import com.khmelyuk.memory.OutOfBoundException;

import java.util.LinkedList;
import java.util.List;

/**
 * Virtual memory table based on linked lists.
 *
 * @author Ruslan Khmelyuk
 */
public class LinkedVirtualMemoryTable implements VirtualMemoryTable {

    private final LinkedList<TableBlock> used = new LinkedList<TableBlock>();
    private final LinkedList<TableBlock> free = new LinkedList<TableBlock>();

    private int freeMemorySize = 0;
    private int usedMemorySize = 0;

    public LinkedVirtualMemoryTable(int size) {
        free.add(new TableBlock(0, size));

        freeMemorySize = size;
        usedMemorySize = 0;
    }

    public List<TableBlock> getUsed() {
        return used;
    }

    public List<TableBlock> getFree() {
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
                free.remove(freeBlock);
            }
            else {
                result = new TableBlock(freeBlock.getAddress(), size);
                freeBlock.resize(
                        freeBlock.getAddress() + size,
                        freeBlock.getSize() - size
                );
            }

            insertBlock(used, result);

            freeMemorySize -= size;
            usedMemorySize += size;

            return result;
        }
        return null;
    }

    protected TableBlock getBlockToAllocate(int size) {
        for (TableBlock each : free) {
            if (each.getSize() >= size) {
                return each;
            }
        }
        return null;
    }

    public boolean free(Block block) {
        TableBlock tableBlock = getSimilarBlock(used, block);
        if (tableBlock != null) {
            if (used.remove(tableBlock)) {
                addFreeBlock(new TableBlock(
                        tableBlock.getAddress(),
                        tableBlock.getSize()));

                int freeBlock = tableBlock.getSize();
                freeMemorySize += freeBlock;
                usedMemorySize -= freeBlock;

                tableBlock.resize(0, 0);

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
            insertBlock(free, block);
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
        final int end = blockAddress + block.getSize();

        TableBlock head = null;
        TableBlock tail = null;
        for (TableBlock each : free) {
            if (head == null && each.getAddress() == end) {
                head = each;
            }
            else if (tail == null && each.getAddress() + each.getSize() == blockAddress) {
                tail = each;
            }
            if (head != null && tail != null) {
                break;
            }
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
                free.remove(head);
            }

            return true;
        }
        else if (head != null) {
            head.resize(blockAddress, block.getSize() + head.getSize());

            return true;
        }

        return false;
    }


    public int getFreeMemorySize() {
        return freeMemorySize;
    }

    public int getUsedMemorySize() {
        return usedMemorySize;
    }

    public void reset(int size) {
        used.clear();
        free.clear();
        free.add(new TableBlock(0, size));
        freeMemorySize = 0;
        usedMemorySize = 0;
    }

    @Override
    public boolean increaseSize(int size) {
        final int totalSize = freeMemorySize + usedMemorySize;

        if (size < usedMemorySize || size <= totalSize) {
            return false;
        }

        // increase memory size
        int incSize = size - totalSize;
        addFreeBlock(new TableBlock(totalSize, incSize));
        freeMemorySize += incSize;

        return true;
    }

    /**
     * Gets the same block or block with such address and size.
     *
     * @param list  the list to find a similar block in.
     * @param block the block to find similar to it.
     * @return the found similar block or null.
     */
    protected static TableBlock getSimilarBlock(List<TableBlock> list, Block block) {
        for (TableBlock each : list) {
            if (each.equals(block)) {
                return each;
            }
        }
        return null;
    }

    private static void insertBlock(List<TableBlock> list, TableBlock block) {
        list.add(0, block);
    }
}

package com.khmelyuk.memory.vm.table;

import java.util.LinkedList;
import java.util.List;

/**
 * Virtual memory table based on linked lists.
 *
 * @author Ruslan Khmelyuk
 */
public class LinkedVirtualMemoryTable implements VirtualMemoryTable {

    /**
     * After how many frees run a memory defragment function.
     */
    public static final int DEFRAGMENT_AFTER_FREES = 15;

    private final List<TableBlock> used = new LinkedList<TableBlock>();
    private final List<TableBlock> free = new LinkedList<TableBlock>();

    protected int freesCount = 0;

    public LinkedVirtualMemoryTable(int size) {
        free.add(new TableBlock(0, size));
    }

    public List<TableBlock> getUsed() {
        return used;
    }

    public List<TableBlock> getFree() {
        return free;
    }

    public Block allocate(int size) {
        TableBlock freeBlock = getBlockToAllocate(size);
        if (freeBlock == null) {
            defragment();
            freeBlock = getBlockToAllocate(size);
        }
        if (freeBlock != null) {
            if (freeBlock.getSize() == size) {
                free.remove(freeBlock);
                insertBlock(used, freeBlock);
                return freeBlock;
            }
            else {
                TableBlock result = new TableBlock(freeBlock.getAddress(), size);
                freeBlock.setAddress(freeBlock.getAddress() + size);
                freeBlock.setSize(freeBlock.getSize() - size);

                insertBlock(used, result);
                return result;
            }
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
                addFreeBlock(tableBlock);
                if (ifNeedDefragmentation()) {
                    defragment();
                }

                tableBlock.setAddress(0);
                tableBlock.setSize(0);

                return true;
            }
        }
        return false;
    }

    protected boolean ifNeedDefragmentation() {
        if (free.size() > 1) {
            if (freesCount++ == DEFRAGMENT_AFTER_FREES) {
                return true;
            }
            if (used.size() == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Glue some free memory blocks into single block.
     * This will remove small parts and will allow allocate large memory
     * and decrease the number of elements in the table, to decrease iteration time.
     */
    public void defragment() {
        if (free.size() > 1) {
            TableBlock prev = free.get(0);
            final List<TableBlock> remove = new LinkedList<TableBlock>();
            for (final TableBlock each : free) {
                if (prev != null) {
                    int end = prev.getAddress() + prev.getSize();
                    if (each.getAddress() == end) {
                        each.setAddress(prev.getAddress());
                        each.setSize(prev.getSize() + each.getSize());

                        prev.setAddress(-1);
                        prev.setSize(0);

                        remove.add(prev);
                    }
                }
                prev = each;
            }

            free.removeAll(remove);
        }
    }

    /**
     * Add a block to the free memory list.
     *
     * @param block the block that need to be added to the free memory list.
     */
    protected void addFreeBlock(TableBlock block) {
        /*int end = block.getAddress() + block.getSize();

        TableBlock main = null;
        for (TableBlock each : free) {
            if (each.getAddress() == end) {
                main = each;
                break;
            }
        }

        if (main != null) {
            // let's glue the blocks
            main.setAddress(block.getAddress());
            main.setSize(block.getSize() + main.getSize());
            return;
        }*/

        insertBlock(free, new TableBlock(block.getAddress(), block.getSize()));
    }

    public int getFreeMemorySize() {
        return getTotalLength(free);
    }

    public int getUsedMemorySize() {
        return getTotalLength(used);
    }

    public void reset(int size) {
        used.clear();
        free.clear();
        free.add(new TableBlock(0, size));
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
        int index = 0;
        final int address = block.getAddress();
        for (TableBlock each : list) {
            if (address <= each.getAddress()) {
                break;
            }
            index++;
        }

        list.add(index, block);
    }

    private static int getTotalLength(List<TableBlock> list) {
        int result = 0;
        for (TableBlock each : list) {
            result += each.getSize();
        }
        return result;
    }
}

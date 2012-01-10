package com.khmelyuk.memory;

import com.khmelyuk.memory.space.FreeSpaceListener;
import com.khmelyuk.memory.space.MemorySpace;
import com.khmelyuk.memory.space.Space;
import com.khmelyuk.memory.vm.VirtualMemory;
import com.khmelyuk.memory.vm.block.VirtualMemoryBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a memory block.
 *
 * @author Ruslan Khmelyuk
 */
public class Memory {

    public static final int KB = 1024;
    public static final int MB = KB * KB;

    private final List<Space> allocated = new ArrayList<Space>();

    private VirtualMemory vm;
    private int freeMemorySize = 0;

    private final FreeSpaceListener freeSpaceListener;

    public Memory(VirtualMemory vm) {
        this.vm = vm;
        this.freeMemorySize = vm.length();

        freeSpaceListener = new FreeSpaceListener() {
            public void onFreeSpace(Space space) {
                freeSpace(space);
            }
        };
    }

    /**
     * Allocates a memory space of specified length.
     *
     * @param length the memory length.
     * @return the new space
     * @throws OutOfMemoryException error to allocate a memory.
     */
    public MemorySpace allocate(int length) throws OutOfMemoryException {
        if (length > freeMemorySize) {
            throw new OutOfMemoryException();
        }
        freeMemorySize -= length;

        int address = getNextFreeBlockIndex(length);
        VirtualMemoryBlock block = vm.getBlock(address, length);
        MemorySpace space = new MemorySpace(block);
        space.setFreeSpaceListener(freeSpaceListener);
        allocated.add(space);

        return space;
    }

    /**
     * Frees the memory.
     */
    public void free() {
        vm = null;
        allocated.clear();
        freeMemorySize = 0;
    }

    /**
     * Free the space.
     * @param space the space to free.
     */
    private void freeSpace(Space space) {
        if (allocated.remove(space)) {
            freeMemorySize += space.size();
        }
    }

    private int getNextFreeBlockIndex(int length) {
        if (allocated.size() == 0) {
            return 0;
        }

        int prevAddress = 0;
        int prevSize = 0;
        for (Space each : allocated) {
            int first = each.getAddress() + each.size();
            int second = prevAddress + prevSize;

            int diff = Math.abs(first - second);
            if (diff > length) {
                int begin = Math.min(first, second);
                if (isNotUsed(begin)) {
                    return begin;
                }
            }

            prevSize = each.size();
            prevAddress = each.getAddress();
        }

        Space last = allocated.get(allocated.size() - 1);
        return last.getAddress() + last.size();
    }

    private boolean isNotUsed(int begin) {
        for (Space each : allocated) {
            int address = each.getAddress();
            if (begin >= address && begin < (address + each.size())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the memory size.
     *
     * @return the memory size.
     */
    public int size() {
        return vm.length();
    }

    /**
     * The size of free memory.
     *
     * @return the free memory size.
     */
    public int getFreeMemorySize() {
        return freeMemorySize;
    }
}

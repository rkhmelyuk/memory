package com.khmelyuk.memory.space;

import com.khmelyuk.memory.vm.block.NoneMemoryBlock;
import com.khmelyuk.memory.vm.block.VirtualMemoryBlock;

/**
 * Represents a space in memory, some part of memory.
 *
 * @author Ruslan Khmelyuk
 */
public class MemorySpace implements Space {

    private VirtualMemoryBlock block;
    private FreeSpaceListener freeSpaceListener;

    public MemorySpace(VirtualMemoryBlock block) {
        this.block = block;
    }

    public void setFreeSpaceListener(FreeSpaceListener freeSpaceListener) {
        this.freeSpaceListener = freeSpaceListener;
    }

    public int getAddress() {
        return block.getAddress();
    }

    public int size() {
        return block.length();
    }

    public void free() {
        if (freeSpaceListener != null) {
            freeSpaceListener.onFreeSpace(this);
            block = NoneMemoryBlock.instance();
        }
    }

    public void write(Object object) {
        block.writeObject(object);
    }

    public void write(String string) {
        block.write(string);
    }

    public Object read() {
        return block.readObject();
    }

    public String readString() {
        return block.readString();
    }

    public Space readOnly() {
        return new ReadOnlySpace(this);
    }
}

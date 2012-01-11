package com.khmelyuk.memory.space;

import com.khmelyuk.memory.vm.VirtualMemoryBlock;

/**
 * The read only wrapper for specified space.
 *
 * @author Ruslan Khmelyuk
 */
public class ReadOnlySpace implements Space {

    private final Space space;

    public ReadOnlySpace(Space space) {
        this.space = space;
    }

    public int getAddress() {
        return space.getAddress();
    }

    public int size() {
        return space.size();
    }

    public void free() {
        space.free();
    }

    public Object read() {
        return space.read();
    }

    public String readString() {
        return space.readString();
    }

    public void write(Object object) {
        throw new WriteNotAllowedException();
    }

    public void write(String string) {
        throw new WriteNotAllowedException();
    }

    public Space readOnly() {
        return this;
    }

    public VirtualMemoryBlock getBlock() {
        return space.getBlock();
    }
}

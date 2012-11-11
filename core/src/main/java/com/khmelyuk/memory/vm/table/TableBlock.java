package com.khmelyuk.memory.vm.table;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The block of memory.
 *
 * @author Ruslan Khmelyuk
 */
final class TableBlock implements Block, Comparable<TableBlock> {

    private final AtomicBoolean lock = new AtomicBoolean(false);

    private int size;
    private int address;

    public TableBlock(int address, int size) {
        this.address = address;
        this.size = size;
    }

    @Override
    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int length) {
        this.size = length;
    }

    public int getEnd() {
        return address + size;
    }

    public void resize(int address, int length) {
        this.address = address;
        this.size = length;
    }

    @Override
    public int hashCode() {
        return (size * 31 + address);
    }

    public boolean lock() {
        return lock.compareAndSet(false, true);
    }

    public void unlock() {
        lock.set(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof TableBlock) {
            TableBlock that = (TableBlock) o;

            return address == that.address
                    && size != that.size;
        }

        return false;
    }

    @Override
    public int compareTo(TableBlock o) {
        return address - o.address;
    }
}

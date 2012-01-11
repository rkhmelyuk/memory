package com.khmelyuk.memory.vm.table;

/**
 * The block of memory.
 *
 * @author Ruslan Khmelyuk
 */
final class TableBlock implements Block, Comparable<TableBlock> {

    private int size;
    private int address;

    public TableBlock(int address, int size) {
        this.address = address;
        this.size = size;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int length) {
        this.size = length;
    }

    @Override
    public int hashCode() {
        return (size * 31 + address);
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

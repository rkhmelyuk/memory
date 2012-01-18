package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.OutOfMemoryException;
import com.khmelyuk.memory.vm.table.Block;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A virtual memory that supports dynamic growth.
 *
 * @author Ruslan Khmelyuk
 */
public class DynamicVirtualMemory implements VirtualMemory {

    private static final int SECTORS_GROW_COUNT = 32;

    private byte[][] data;
    private int count;

    private int size;
    private final int maxSize;
    private final int growth;
    private final VirtualMemoryTable table;

    private final float oneGrowth;

    public DynamicVirtualMemory(int size, int maxSize, int growth, VirtualMemoryTable table) {
        growth = (growth != 0 ? growth : 1);

        int sectorsCount = (maxSize - size) / growth + 1;
        sectorsCount = Math.min(sectorsCount, SECTORS_GROW_COUNT);
        this.data = new byte[sectorsCount][];
        this.data[0] = new byte[size];
        this.count = 1;

        this.size = size;
        this.maxSize = maxSize;
        this.growth = growth;
        this.table = table;
        this.oneGrowth = 1f / growth;
    }

    public int size() {
        return size;
    }

    public int getFreeSize() {
        return table.getFreeMemorySize();
    }

    public int getUsedSize() {
        return table.getUsedMemorySize();
    }

    public VirtualMemoryBlock allocate(int length) throws OutOfMemoryException, OutOfBoundException {
        if (length < 0) {
            throw new OutOfBoundException();
        }

        Block block = table.allocate(length);

        // if failed to allocate a block,
        // then tries to increase a memory size and allocate.
        while (block == null) {
            if (size >= maxSize) {
                break;
            }

            if (!extendMemorySize()) {
                // if failed to increase memory size - exit this loop
                break;
            }
            block = table.allocate(length);
        }

        if (block == null) {
            // throws exception if failed to allocate the block.
            throw new OutOfMemoryException();
        }

        // returns the allocated VM block.
        return new VirtualMemoryBlock(this, block);
    }

    /**
     * Extends the virtual memory size.
     * This method extends the memory by a {@code growth} step to max {@code maxSize} value.
     *
     * @return true if memory was extended, otherwise false.
     */
    private boolean extendMemorySize() {
        int newSize = Math.min(size + growth, maxSize);
        if (table.increaseSize(newSize)) {

            int length = data.length;
            if (length == count) {
                // if there is a need to extend a data array
                // then increase it's size by the appropriate step
                byte[][] newData = new byte[length + SECTORS_GROW_COUNT][];

                // copy current data to the new data array
                System.arraycopy(data, 0, newData, 0, length);

                // swap the data.
                data = newData;
            }

            // allocate the new memory sector
            data[count++] = new byte[newSize - size];
            size = newSize;

            return true;
        }
        return false;
    }

    public void free() {
        data = new byte[0][];
        table.reset(0);
        size = 0;
    }

    public void free(VirtualMemoryBlock block) {
        table.free(block.getBlock());
    }

    // ----------------------------------------------------------

    public InputStream getInputStream() {
        return new VMInputStream(this);
    }

    public InputStream getInputStream(int offset, int length) throws OutOfBoundException {
        if (offset + length > size) {
            throw new OutOfBoundException();
        }

        return new VMInputStream(this, offset, length);
    }

    public OutputStream getOutputStream() {
        return new VMOutputStream(this);
    }

    public OutputStream getOutputStream(int offset, int length) throws OutOfBoundException {
        if (offset + length > size) {
            throw new OutOfBoundException();
        }

        return new VMOutputStream(this, offset, length);
    }

    public void write(byte[] data) throws OutOfBoundException {
        int dataLength = data.length;
        if (dataLength > size) {
            throw new OutOfBoundException();
        }

        int dataOffset = 0;
        final int endIdx = calcEndIndex(0, dataLength);
        for (int i = 0; i <= endIdx; i++) {
            final byte[] each = this.data[i];
            final int eachLength = each.length;
            if (eachLength >= dataLength) {
                System.arraycopy(data, dataOffset, each, 0, dataLength);
                break;
            }
            else {
                System.arraycopy(data, dataOffset, each, 0, eachLength);
                dataLength -= eachLength;
                dataOffset += eachLength;
            }
        }
    }

    public void write(byte[] data, int offset) throws OutOfBoundException {
        write(data, offset, data.length);
    }

    public void write(byte[] data, int offset, int length) throws OutOfBoundException {
        if (offset >= size || length + offset > size) {
            throw new OutOfBoundException();
        }

        int dataOffset = 0;
        boolean started = false;

        final int startIdx = calcStartIndex(offset);
        final int endIdx = calcEndIndex(offset, length);
        int start = calculateStartPosition(startIdx);
        for (int i = startIdx; i <= endIdx; i++) {
            final byte[] each = this.data[i];
            final int eachLength = each.length;
            if (started || (offset >= start && offset < start + eachLength)) {
                final int eachOffset = started ? 0 : offset - start;
                final int realLength = eachLength - eachOffset;
                started = true;

                if (realLength >= length) {
                    System.arraycopy(data, dataOffset, each, eachOffset, length);
                    break;
                }
                else {
                    System.arraycopy(data, dataOffset, each, eachOffset, realLength);
                    length -= realLength;
                    dataOffset += realLength;
                }
            }
            start += eachLength;
        }
    }

    public int read(byte[] data) throws OutOfBoundException {
        int dataLength = data.length;
        if (dataLength > size) {
            dataLength = size;
        }

        int dataOffset = 0;
        final int endIdx = calcEndIndex(0, dataLength);
        for (int i = 0; i <= endIdx; i++) {
            final byte[] each = this.data[i];
            final int eachLength = each.length;

            if (dataLength <= eachLength) {
                System.arraycopy(each, 0, data, dataOffset, dataLength);
                break;
            }
            else {
                System.arraycopy(each, 0, data, dataOffset, eachLength);
                dataLength -= eachLength;
                dataOffset += eachLength;
            }
        }

        int read = dataOffset + dataLength;
        return read != 0 ? read : -1;
    }

    public int read(byte[] data, int offset, int length) {
        int dataLength = length;
        if (data.length < dataLength) {
            dataLength = data.length;
        }
        if (dataLength == 0 || offset + dataLength > size) {
            return -1;
        }

        int dataOffset = 0;
        boolean started = false;

        final int startIdx = calcStartIndex(offset);
        final int endIdx = calcEndIndex(offset, dataLength);
        int start = calculateStartPosition(startIdx);
        for (int i = startIdx; i <= endIdx; i++) {
            final byte[] each = this.data[i];
            final int eachLength = each.length;
            if (started || (offset >= start && offset < start + eachLength)) {
                final int eachOffset = started ? 0 : offset - start;
                final int realLength = eachLength - eachOffset;
                started = true;

                if (dataLength <= realLength) {
                    System.arraycopy(each, eachOffset, data, dataOffset, dataLength);
                    break;
                }
                else {
                    System.arraycopy(each, eachOffset, data, dataOffset, realLength);
                    dataLength -= realLength;
                    dataOffset += realLength;
                }
            }
            start += eachLength;
        }

        int read = dataOffset + dataLength;
        return read != 0 ? read : -1;
    }

    public void write(byte data, int offset) throws OutOfBoundException {
        if (offset >= size) {
            throw new OutOfBoundException();
        }

        final int startIdx = calcStartIndex(offset);
        final byte[] sector = this.data[startIdx];
        if (sector != null) {
            int start = calculateStartPosition(startIdx);
            sector[offset - start] = data;
        }
    }

    public byte read(int offset) {
        if (offset >= size) {
            return -1;
        }

        final int startIdx = calcStartIndex(offset);
        final byte[] sector = this.data[startIdx];

        if (sector != null) {
            int start = calculateStartPosition(startIdx);
            return sector[offset - start];
        }

        return -1;
    }

    /**
     * Calculate the index of the memory sector by offset.
     * For example, if memory contains 5 sectors with next lengths: 50, 25, 25, 25
     * then the index for offset = 65 will be 1, because 50 < 65 < (75 = 50 + 25).
     *
     * @param offset the offset to get index for.
     * @return the found sector index, or -1 if not found.
     */
    private int calcStartIndex(int offset) {
        if (offset == 0) {
            // first sector
            return 0;
        }

        // check if first sector
        final int len1 = data[0].length;
        if (offset <= len1) {
            return 0;
        }

        // not a first sector - find an index by offset
        int index = (int) ((offset - len1) * oneGrowth) + 1;
        return index < count ? index : -1;
    }

    /**
     * Calculate the index of the memory sector by offset and length..
     * For example, if memory contains 5 sectors with next lengths: 50, 25, 25, 25
     * then the index for offset = 65 and length is 20 will be 2, because 75 < (60 + 25 = 85) < (100 = 75 + 25).
     *
     * @param offset the offset to get index for.
     * @param length the length of memory block.
     * @return the found sector index, or -1 if not found.
     */
    private int calcEndIndex(int offset, int length) {
        final int address = offset + length;
        final int len1 = data[0].length;
        if (address < len1) {
            return 0;
        }
        int index = (int) ((address - len1) * oneGrowth) + 1;
        return index < count ? index : -1;
    }

    /**
     * Calculate the start position for specified memory sector.
     * Algorithm is simple - every except first and, maybe, last sectors have fixed size equal to {@code growth}.
     * Method checks what the index, and using it returns an calculated position for sector.
     *
     * @param index the sector index.
     * @return the calculated start position for memory sector.
     */
    private int calculateStartPosition(int index) {
        return (index == 0
                ? 0 : index == 1
                      ? this.data[0].length
                      : this.data[0].length + (index - 1) * growth);
    }
}

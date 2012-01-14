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

    private byte[][] data;
    private int count;

    private int size;
    private final int maxSize;
    private final int growth;
    private final VirtualMemoryTable table;

    private final float oneGrowth;

    public DynamicVirtualMemory(int size, int maxSize, int growth, VirtualMemoryTable table) {
        growth = (growth != 0 ? growth : 1);
        data = new byte[(maxSize - size) / growth + 1][];
        data[0] = new byte[size];
        count = 1;

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
        while (block == null) {
            if (size >= maxSize) {
                break;
            }

            extendMemorySize();
            block = table.allocate(length);
        }

        if (block == null) {
            throw new OutOfMemoryException();
        }

        return new VirtualMemoryBlock(this, block);
    }

    private void extendMemorySize() {
        int newSize = Math.min(size + growth, maxSize);
        if (table.increaseSize(newSize)) {
            data[count++] = new byte[newSize - size];
            size = newSize;
        }
    }

    @Override
    public void free() {
        data = new byte[0][];
        table.reset(0);
        size = 0;
    }

    @Override
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
        int dataLength = length;
        if (data.length < dataLength) {
            dataLength = data.length;
        }

        if (offset >= size || dataLength + offset > size) {
            throw new OutOfBoundException();
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

                if (realLength >= dataLength) {
                    System.arraycopy(data, dataOffset, each, eachOffset, dataLength);
                    break;
                }
                else {
                    System.arraycopy(data, dataOffset, each, eachOffset, realLength);
                    dataLength -= realLength;
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
        if (offset + dataLength > size) {
            dataLength = size - offset;
        }
        if (dataLength == 0) {
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
        if (offset >= this.size) {
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

    private int calcStartIndex(int offset) {
        if (offset == 0) {
            return 0;
        }
        final int len1 = data[0].length;
        if (offset <= len1) {
            return 0;
        }
        return (int) ((offset - len1) * oneGrowth) + 1;
    }

    private int calcEndIndex(int offset, int length) {
        final int address = offset + length;
        final int len1 = data[0].length;
        if (address < len1) {
            return 0;
        }
        return (int) ((address - len1) * oneGrowth) + 1;
    }

    private int calculateStartPosition(int startIdx) {
        return (startIdx == 0
                ? 0 : startIdx == 1
                ? this.data[0].length
                : this.data[0].length + (startIdx - 1) * growth);
    }
}

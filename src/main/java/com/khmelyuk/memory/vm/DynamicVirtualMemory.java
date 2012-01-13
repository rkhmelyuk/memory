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

    long avgAllocation = 0;
    long avgRead = 0;
    long avgWrite = 0;

    public DynamicVirtualMemory(int size, int maxSize, int growth, VirtualMemoryTable table) {
        growth = (growth != 0 ? growth : 1);
        data = new byte[(maxSize - size) / growth + 1][];
        data[0] = new byte[size];
        count = 1;

        this.size = size;
        this.maxSize = maxSize;
        this.growth = growth;
        this.table = table;
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
        long begin = System.nanoTime();

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

        avgAllocation = (avgAllocation + (System.nanoTime() - begin)) / 2;

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
        //write(data, 0, data.length);
        if (data.length > size) {
            throw new OutOfBoundException();
        }

        int dataOffset = 0;
        int dataLength = data.length;
        for (int i = 0; i < count; i++) {
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

        long begin = System.nanoTime();

        int start = 0;
        int dataOffset = 0;
        boolean started = false;
        for (int i = 0; i < count; i++) {
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

        avgWrite = (avgWrite + (System.nanoTime() - begin)) / 2;
    }

    public int read(byte[] data) throws OutOfBoundException {
        int dataLength = data.length;
        if (dataLength > size) {
            dataLength = size;
        }

        int dataOffset = 0;
        for (int i = 0; i < count; i++) {
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

        long begin = System.nanoTime();

        int start = 0;
        int dataOffset = 0;
        boolean started = false;
        for (int i = 0; i < count; i++) {
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

        avgRead = (avgRead + (System.nanoTime() - begin)) / 2;

        int read = dataOffset + dataLength;
        return read != 0 ? read : -1;
    }

    public void write(byte data, int offset) throws OutOfBoundException {
        if (offset >= this.size) {
            throw new OutOfBoundException();
        }

        byte[] sector = null;
        int start = 0;
        for (int i = 0; i < count; i++) {
            byte[] each = this.data[i];
            if (/*offset >= start && */offset < start + each.length) {
                sector = each;
                break;
            }
            start += each.length;
        }
        if (sector != null) {
            sector[offset - start] = data;
        }
    }

    public byte read(int offset) {
        if (offset >= size) {
            return -1;
        }

        byte[] sector = null;
        int start = 0;
        for (int i = 0; i < count; i++) {
            final byte[] each = data[i];
            if (/*offset >= start && */offset < start + each.length) {
                sector = each;
                break;
            }
            start += each.length;
        }
        if (sector != null) {
            return sector[offset - start];
        }

        return -1;
    }
}

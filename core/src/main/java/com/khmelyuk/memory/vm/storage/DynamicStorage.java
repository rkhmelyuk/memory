package com.khmelyuk.memory.vm.storage;

import com.khmelyuk.memory.OutOfBoundException;

/**
 * The storage that can growth.
 *
 * @author Ruslan Khmelyuk
 */
public class DynamicStorage implements Storage {

    private static final int SECTORS_GROW_COUNT = 32;

    private Storage[] data;

    private int count;
    private int size;

    private final int growth;
    private final int maxSize;
    private final float oneGrowth;
    private final StorageFactory storageFactory;

    public DynamicStorage(int size, int maxSize, int growth, StorageFactory storageFactory) {
        growth = (growth != 0 ? growth : 1);

        int sectorsCount = (maxSize - size) / growth + 1;
        sectorsCount = Math.min(sectorsCount, SECTORS_GROW_COUNT);
        this.data = new Storage[sectorsCount];
        this.data[0] = storageFactory.create(0, size);
        this.count = 1;

        this.size = size;
        this.growth = growth;
        this.maxSize = maxSize;
        this.oneGrowth = 1f / growth;

        this.storageFactory = storageFactory;
    }

    public int getGrowth() {
        return growth;
    }

    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void free() {
        data = new Storage[0];
        count = 0;
        size = 0;
    }

    /**
     * Increases the size of this storage.
     *
     * @param newSize the new size of the storage.
     */
    public void increaseSize(int newSize) {
        int length = data.length;
        if (length == count) {
            // if there is a need to extend a data array
            // then increase it's size by the appropriate step
            Storage[] newData = new Storage[length + SECTORS_GROW_COUNT];

            // copy current data to the new data array
            System.arraycopy(data, 0, newData, 0, length);

            // swap the data.
            data = newData;
        }

        // allocate the new memory sector
        data[count++] = storageFactory.create(size, newSize - size);
        size = newSize;
    }

    public void write(byte[] data) throws OutOfBoundException {
        int dataLength = data.length;
        if (dataLength > size) {
            throw new OutOfBoundException();
        }

        int dataOffset = 0;
        final int endIdx = calcEndIndex(0, dataLength);
        for (int i = 0; i <= endIdx; i++) {
            final Storage each = this.data[i];
            final int eachLength = each.size();
            if (eachLength >= dataLength) {
                each.write(data, 0, dataOffset, dataLength);
                break;
            } else {
                each.write(data, 0, dataOffset, eachLength);
                dataLength -= eachLength;
                dataOffset += eachLength;
            }
        }
    }

    public void write(byte[] data, int offset) throws OutOfBoundException {
        write(data, offset, data.length);
    }

    public void write(byte[] data, int offset, int length) throws OutOfBoundException {
        // TODO - what about just calling write(data, offset, 0, length)?

        if (offset >= size || length + offset > size) {
            throw new OutOfBoundException();
        }

        int dataOffset = 0;
        boolean started = false;

        final int startIdx = calcStartIndex(offset);
        final int endIdx = calcEndIndex(offset, length);
        int start = calculateStartPosition(startIdx);
        for (int i = startIdx; i <= endIdx; i++) {
            final Storage each = this.data[i];
            final int eachLength = each.size();
            if (started || (offset >= start && offset < start + eachLength)) {
                final int eachOffset = started ? 0 : offset - start;
                final int realLength = eachLength - eachOffset;
                started = true;

                if (realLength >= length) {
                    each.write(data, eachOffset, dataOffset, length);
                    break;
                } else {
                    each.write(data, eachOffset, dataOffset, realLength);
                    length -= realLength;
                    dataOffset += realLength;
                }
            }
            start += eachLength;
        }
    }

    @Override
    public void write(byte[] data, int offset, int dataOffset, int length) throws OutOfBoundException {
        if (offset >= size || length + offset > size) {
            throw new OutOfBoundException();
        }

        boolean started = false;

        final int startIdx = calcStartIndex(offset);
        final int endIdx = calcEndIndex(offset, length);
        int start = calculateStartPosition(startIdx);
        for (int i = startIdx; i <= endIdx; i++) {
            final Storage each = this.data[i];
            final int eachLength = each.size();
            if (started || (offset >= start && offset < start + eachLength)) {
                final int eachOffset = started ? 0 : offset - start;
                final int realLength = eachLength - eachOffset;
                started = true;

                if (realLength >= length) {
                    each.write(data, eachOffset, dataOffset, length);
                    break;
                } else {
                    each.write(data, eachOffset, dataOffset, realLength);
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
            final Storage each = this.data[i];
            final int eachLength = each.size();

            if (dataLength <= eachLength) {
                each.read(data, 0, dataOffset, dataLength);
                break;
            } else {
                each.read(data, 0, dataOffset, eachLength);
                dataLength -= eachLength;
                dataOffset += eachLength;
            }
        }

        int read = dataOffset + dataLength;
        return read != 0 ? read : -1;
    }

    public int read(byte[] data, int offset, int length) {
        // TODO - what about just calling read(data, offset, 0, length)?
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
            final Storage each = this.data[i];
            final int eachLength = each.size();
            if (started || (offset >= start && offset < start + eachLength)) {
                final int eachOffset = started ? 0 : offset - start;
                final int realLength = eachLength - eachOffset;
                started = true;

                if (dataLength <= realLength) {
                    each.read(data, eachOffset, dataOffset, dataLength);
                    break;
                } else {
                    each.read(data, eachOffset, dataOffset, realLength);
                    dataLength -= realLength;
                    dataOffset += realLength;
                }
            }
            start += eachLength;
        }

        int read = dataOffset + dataLength;
        return read != 0 ? read : -1;
    }

    @Override
    public int read(byte[] data, int offset, int dataOffset, int length) {
        int dataLength = length;
        if (data.length < dataLength) {
            dataLength = data.length;
        }
        if (dataLength == 0 || offset + dataLength > size) {
            return -1;
        }

        boolean started = false;

        final int startIdx = calcStartIndex(offset);
        final int endIdx = calcEndIndex(offset, dataLength);
        int start = calculateStartPosition(startIdx);
        for (int i = startIdx; i <= endIdx; i++) {
            final Storage each = this.data[i];
            final int eachLength = each.size();
            if (started || (offset >= start && offset < start + eachLength)) {
                final int eachOffset = started ? 0 : offset - start;
                final int realLength = eachLength - eachOffset;
                started = true;

                if (dataLength <= realLength) {
                    each.read(data, eachOffset, dataOffset, dataLength);
                    break;
                } else {
                    each.read(data, eachOffset, dataOffset, realLength);
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
        final Storage sector = this.data[startIdx];
        if (sector != null) {
            int start = calculateStartPosition(startIdx);
            sector.write(data, offset - start);
        }
    }

    public byte read(int offset) {
        if (offset >= size) {
            return -1;
        }

        final int startIdx = calcStartIndex(offset);
        final Storage sector = this.data[startIdx];

        if (sector != null) {
            int start = calculateStartPosition(startIdx);
            return sector.read(offset - start);
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
        final int len1 = data[0].size();
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
        final int len1 = data[0].size();
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
                ? this.data[0].size()
                : this.data[0].size() + (index - 1) * growth);
    }
}

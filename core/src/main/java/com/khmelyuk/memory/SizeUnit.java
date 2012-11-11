package com.khmelyuk.memory;

/**
 * The memory size unit.
 *
 * @author Ruslan Khmelyuk
 */
public enum SizeUnit {

    Bytes(1),
    KB(1024),
    MB(1024 * KB.bytes),
    GB(1024 * MB.bytes);

    private final int bytes;

    private SizeUnit(int bytes) {
        this.bytes = bytes;
    }

    /**
     * Gets the length of unit in bytes.
     *
     * @return the length of unit in bytes.
     */
    public int bytes() {
        return bytes;
    }

    public int toBytes(int size) {
        if (size == 0) {
            return 0;
        }
        if (Bytes == this) {
            return size;
        }

        return bytes * size;
    }

    public int fromBytes(int size) {
        return size / bytes + (size % bytes != 0 ? 1 : 0);
    }

    /**
     * Find the size unit that represents the lowest unit of size.
     * For example, min(KB, MB) == KB, min(MB, Bytes) == Bytes.
     *
     * @param left  the first size unit.
     * @param right the second size unit.
     * @return the minimal size unit.
     */
    public static SizeUnit min(SizeUnit left, SizeUnit right) {
        if (left.bytes < right.bytes) {
            return left;
        } else if (right.bytes < left.bytes) {
            return right;
        }

        // left == right, return any
        return left;
    }
}

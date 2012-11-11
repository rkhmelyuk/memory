package com.khmelyuk.memory;


/**
 * Represents the size of memory. This class also allows to convert to different types.
 * Instances of this class are immutable.
 *
 * @author Ruslan Khmelyuk
 */
public final class MemorySize {

    public static final MemorySize ZERO = bytes(0);

    // -------------- factory methods

    public static MemorySize bytes(int size) {
        return new MemorySize(size, SizeUnit.Bytes);
    }

    public static MemorySize kilobytes(int size) {
        return new MemorySize(size, SizeUnit.KB);
    }

    public static MemorySize megabytes(int size) {
        return new MemorySize(size, SizeUnit.MB);
    }

    public static MemorySize gigabytes(int size) {
        return new MemorySize(size, SizeUnit.GB);
    }

    // --------------------------------------------------------

    // TODO - maybe it's better to save bytes not size?

    private final int size;
    private final SizeUnit unit;

    public MemorySize(int size, SizeUnit unit) {
        this.size = size;
        this.unit = unit;
    }

    public int getSize() {
        return size;
    }

    public SizeUnit getUnit() {
        return unit;
    }

    public int getBytes() {
        return unit.toBytes(size);
    }

    public MemorySize convertTo(SizeUnit unit) {
        return new MemorySize(unit.fromBytes(getBytes()), unit);
    }

    /**
     * Add two memory sizes and return the result.
     * The result has the minimal size unit from participating memory sizes.
     *
     * @param memorySize the memory size that should be added to this one.
     * @return the result memory size object.
     */
    public MemorySize add(final MemorySize memorySize) {
        SizeUnit minUnit = SizeUnit.min(unit, memorySize.unit);
        int bytes = getBytes() + memorySize.getBytes();
        return new MemorySize(minUnit.fromBytes(bytes), minUnit);
    }

    /**
     * Subtract two memory sizes and return the result.
     * The result has the minimal size unit from participating memory sizes.
     *
     * @param memorySize the memory size that should be subtracted from this one.
     * @return the result memory size object.
     */
    public MemorySize subtract(final MemorySize memorySize) {
        SizeUnit minUnit = SizeUnit.min(unit, memorySize.unit);
        int bytes = getBytes() - memorySize.getBytes();
        return new MemorySize(minUnit.fromBytes(bytes), minUnit);
    }

    /**
     * Check that two objects are equal. MemorySizes are equal only if the number of the bytes is the same.
     * This method ignores the Size Unit value, only number of bytes is IMPORTANT.
     *
     * @param other other object to compare to.
     * @return true if equal, otherwise false.
     */
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof MemorySize)) return false;

        MemorySize otherObj = (MemorySize) other;
        return otherObj.getBytes() == otherObj.getBytes();
    }

    @Override
    public int hashCode() {
        return getBytes() ^ 31;
    }
}
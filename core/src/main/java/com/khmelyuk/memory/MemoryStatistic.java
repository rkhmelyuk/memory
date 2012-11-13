package com.khmelyuk.memory;

import com.khmelyuk.memory.util.FormatUtil;

import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * Represents a memory statistic information.
 * This is a static information and represents it on the time when it was fetched.
 * <p/>
 * Usually, no locking used to prepare the statistic, so it can be not up-to-date.
 * But that's ok for statistic, isn't it?
 * <p/>
 * Instances of this class are immutable.
 *
 * @author Ruslan Khmelyuk
 */
public final class MemoryStatistic {

    private final MemorySize usedSize;
    private final MemorySize freeSize;

    private final int usedBlocksCount;
    private final int freeBlocksCount;
    private final int successAllocations;
    private final BigDecimal successAllocationsPercentage;

    public MemoryStatistic(MemorySize usedSize, MemorySize freeSize, int usedBlocksCount, int freeBlocksCount,
                           int successAllocations, BigDecimal successAllocationsPercentage) {
        this.usedSize = usedSize;
        this.freeSize = freeSize;
        this.usedBlocksCount = usedBlocksCount;
        this.freeBlocksCount = freeBlocksCount;
        this.successAllocations = successAllocations;
        this.successAllocationsPercentage = successAllocationsPercentage;
    }

    public MemorySize getUsedSize() {
        return usedSize;
    }

    public MemorySize getFreeSize() {
        return freeSize;
    }

    public int getUsedBlocksCount() {
        return usedBlocksCount;
    }

    public int getFreeBlocksCount() {
        return freeBlocksCount;
    }

    public long getTotalSize() {
        // TODO - change to usedSize.add(freeSize).bytes() after bytes are long type
        return (long) usedSize.getBytes() + freeSize.getBytes();
    }

    public BigDecimal getUsedPercentage() {
        return FormatUtil.getPercent(usedSize.getBytes(), getTotalSize());
    }

    public BigDecimal getFreePercentage() {
        return FormatUtil.getPercent(freeSize.getBytes(), getTotalSize());
    }

    public int getSuccessAllocations() {
        return successAllocations;
    }

    public BigDecimal getSuccessAllocationsPercentage() {
        return successAllocationsPercentage;
    }

    /**
     * Prints the statistic information to the {@code System.out}.
     */
    public void print() {
        print(new PrintWriter(System.out));
    }

    /**
     * Prints the statistic information to the specified writer.
     *
     * @param writer the print writer.
     */
    public void print(PrintWriter writer) {
        writer.println("Used\t\t"
                + FormatUtil.sizeAsString(usedSize)
                + "\t\t" + getUsedPercentage() + "%"
                + "\t\t" + usedBlocksCount + " blocks");
        writer.println("Free\t\t"
                + FormatUtil.sizeAsString(freeSize)
                + "\t" + getFreePercentage() + "%"
                + "\t" + freeBlocksCount + " blocks");
        writer.println("Allocated\t"
                + successAllocations + " blocks"
                + "\t" + successAllocationsPercentage + "%");

        writer.flush();
    }

}

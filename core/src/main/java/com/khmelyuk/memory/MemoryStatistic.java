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
 *
 * @author Ruslan Khmelyuk
 */
public class MemoryStatistic {

    private MemorySize usedSize;
    private MemorySize freeSize;

    private int usedBlocksCount;
    private int freeBlocksCount;
    private int successAllocations;
    private BigDecimal successAllocationsPercentage;

    public MemorySize getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(MemorySize usedSize) {
        this.usedSize = usedSize;
    }

    public MemorySize getFreeSize() {
        return freeSize;
    }

    public void setFreeSize(MemorySize freeSize) {
        this.freeSize = freeSize;
    }

    public int getUsedBlocksCount() {
        return usedBlocksCount;
    }

    public void setUsedBlocksCount(int usedBlocksCount) {
        this.usedBlocksCount = usedBlocksCount;
    }

    public int getFreeBlocksCount() {
        return freeBlocksCount;
    }

    public void setFreeBlocksCount(int freeBlocksCount) {
        this.freeBlocksCount = freeBlocksCount;
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

    public void setSuccessAllocations(int successAllocations) {
        this.successAllocations = successAllocations;
    }

    public BigDecimal getSuccessAllocationsPercentage() {
        return successAllocationsPercentage;
    }

    public void setSuccessAllocationsPercentage(BigDecimal successAllocationsPercentage) {
        this.successAllocationsPercentage = successAllocationsPercentage;
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

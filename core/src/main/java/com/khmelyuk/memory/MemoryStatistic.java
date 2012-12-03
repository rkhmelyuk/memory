package com.khmelyuk.memory;

import com.khmelyuk.memory.metrics.MetricsSnapshot;
import com.khmelyuk.memory.util.FormatUtil;

import java.io.PrintWriter;
import java.io.Serializable;
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
public final class MemoryStatistic implements Serializable {

    private final MetricsSnapshot metrics;

    private final MemorySize usedSize;
    private final MemorySize freeSize;

    private final long usedBlocksCount;
    private final long freeBlocksCount;

    private final long totalAllocations;
    private final long failedAllocations;

    private final long totalFrees;
    private final long failedFrees;

    public MemoryStatistic(MetricsSnapshot metrics,
                           MemorySize usedSize, MemorySize freeSize,
                           long usedBlocksCount, long freeBlocksCount,
                           long totalAllocations, long failedAllocations,
                           long totalFrees, long failedFrees) {
        this.metrics = metrics;
        this.usedSize = usedSize;
        this.freeSize = freeSize;
        this.usedBlocksCount = usedBlocksCount;
        this.freeBlocksCount = freeBlocksCount;
        this.totalAllocations = totalAllocations;
        this.failedAllocations = failedAllocations;
        this.totalFrees = totalFrees;
        this.failedFrees = failedFrees;
    }

    public MetricsSnapshot getMetrics() {
        return metrics;
    }

    public MemorySize getUsedSize() {
        return usedSize;
    }

    public MemorySize getFreeSize() {
        return freeSize;
    }

    public long getUsedBlocksCount() {
        return usedBlocksCount;
    }

    public long getFreeBlocksCount() {
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

    public long getTotalAllocations() {
        return totalAllocations;
    }

    public long getFailedAllocations() {
        return failedAllocations;
    }

    public long getSuccessAllocations() {
        return totalAllocations - failedAllocations;
    }

    public BigDecimal getSuccessAllocationsPercentage() {
        return FormatUtil.getPercent(getSuccessAllocations(), getTotalAllocations());
    }

    public BigDecimal getFailedAllocationsPercentage() {
        return FormatUtil.getPercent(getFailedAllocations(), getTotalAllocations());
    }

    public long getTotalFrees() {
        return totalFrees;
    }

    public long getFailedFrees() {
        return failedFrees;
    }

    public long getSuccessFrees() {
        return totalFrees - failedFrees;
    }

    public BigDecimal getSuccessFreesPercentage() {
        return FormatUtil.getPercent(getSuccessFrees(), getTotalFrees());
    }

    public BigDecimal getFailedFreesPercentage() {
        return FormatUtil.getPercent(getFailedFrees(), getTotalFrees());
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
                + "\t" + getFreeBlocksCount() + " blocks");
        writer.println("Allocated\t"
                + getSuccessAllocations() + " blocks"
                + "\t" + getSuccessAllocationsPercentage() + "%");
        writer.println("Freed\t"
                + getSuccessFrees() + " blocks"
                + "\t" + getSuccessFreesPercentage() + "%");

        writer.flush();
    }

}

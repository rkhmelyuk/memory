package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.MemorySize;

/**
 * Represents a VM statistic information.
 *
 * @author Ruslan Khmelyuk
 */
public class VirtualMemoryStatistic {

    private MemorySize usedSize;
    private MemorySize freeSize;

    private int usedBlocksCount;
    private int freeBlocksCount;

    private long totalAllocations;
    private long failedAllocations;

    private long totalFrees;
    private Long failedFrees;

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

    public long getTotalAllocations() {
        return totalAllocations;
    }

    public void setTotalAllocations(long totalAllocations) {
        this.totalAllocations = totalAllocations;
    }

    public long getFailedAllocations() {
        return failedAllocations;
    }

    public void setFailedAllocations(long failedAllocations) {
        this.failedAllocations = failedAllocations;
    }

    public long getTotalFrees() {
        return totalFrees;
    }

    public void setTotalFrees(long totalFrees) {
        this.totalFrees = totalFrees;
    }

    public Long getFailedFrees() {
        return failedFrees;
    }

    public void setFailedFrees(Long failedFrees) {
        this.failedFrees = failedFrees;
    }
}

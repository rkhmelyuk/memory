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

    private int totalAllocations;
    private int failedAllocations;

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

    public int getTotalAllocations() {
        return totalAllocations;
    }

    public void setTotalAllocations(int totalAllocations) {
        this.totalAllocations = totalAllocations;
    }

    public int getFailedAllocations() {
        return failedAllocations;
    }

    public void setFailedAllocations(int failedAllocations) {
        this.failedAllocations = failedAllocations;
    }
}

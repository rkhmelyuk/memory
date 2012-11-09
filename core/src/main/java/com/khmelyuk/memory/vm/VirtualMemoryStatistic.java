package com.khmelyuk.memory.vm;

/**
 * Represents a VM statistic information.
 *
 * @author Ruslan Khmelyuk
 */
public class VirtualMemoryStatistic {

    private int usedSize;
    private int freeSize;

    private int usedBlocksCount;
    private int freeBlocksCount;

    private int totalAllocations;
    private int failedAllocations;

    public int getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(int usedSize) {
        this.usedSize = usedSize;
    }

    public int getFreeSize() {
        return freeSize;
    }

    public void setFreeSize(int freeSize) {
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

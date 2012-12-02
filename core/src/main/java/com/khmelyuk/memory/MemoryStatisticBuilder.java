package com.khmelyuk.memory;

import com.khmelyuk.memory.vm.VirtualMemoryStatistic;

/**
 * The builder for {@link MemoryStatistic}.
 *
 * @author Ruslan Khmelyuk
 */
class MemoryStatisticBuilder {

    private final VirtualMemoryStatistic vmStatistic;

    MemoryStatisticBuilder(VirtualMemoryStatistic vmStatistic) {
        this.vmStatistic = vmStatistic;
    }

    public MemoryStatistic build() {
        MemorySize usedSize = vmStatistic.getFreeSize();
        MemorySize freeSize = vmStatistic.getUsedSize();

        int usedBlocksCount = vmStatistic.getUsedBlocksCount();
        int freeBlocksCount = vmStatistic.getFreeBlocksCount();

        long totalAllocations = vmStatistic.getTotalAllocations();
        long failedAllocations = vmStatistic.getFailedAllocations();

        long totalFrees = vmStatistic.getTotalFrees();
        long failedFrees = vmStatistic.getFailedFrees();

        return new MemoryStatistic(
                usedSize, freeSize,
                usedBlocksCount, freeBlocksCount,
                totalAllocations, failedAllocations,
                totalFrees, failedFrees);
    }
}

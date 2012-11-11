package com.khmelyuk.memory;

import com.khmelyuk.memory.util.FormatUtil;
import com.khmelyuk.memory.vm.VirtualMemoryStatistic;

import java.math.BigDecimal;

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

        int totalAllocations = vmStatistic.getTotalAllocations();
        int successAllocations = totalAllocations - vmStatistic.getFailedAllocations();
        BigDecimal successAllocationsPercentage = FormatUtil.getPercent(successAllocations, totalAllocations);

        return new MemoryStatistic(usedSize, freeSize, usedBlocksCount,
                freeBlocksCount, successAllocations, successAllocationsPercentage);
    }
}

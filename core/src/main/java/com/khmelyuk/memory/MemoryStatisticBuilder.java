package com.khmelyuk.memory;

import com.khmelyuk.memory.metrics.MetricsSnapshot;

/**
 * The builder for {@link MemoryStatistic}.
 *
 * @author Ruslan Khmelyuk
 */
class MemoryStatisticBuilder {

    private final MetricsSnapshot metrics;

    public MemoryStatisticBuilder(MetricsSnapshot metrics) {
        this.metrics = metrics;
    }

    public MemoryStatistic build() {
        MemorySize freeSize = MemorySize.bytes(metrics.getInt("freeSize", 0));
        MemorySize usedSize = MemorySize.bytes(metrics.getInt("usedSize", 0));

        long usedBlocksCount = metrics.get("usedBlocksCount", 0);
        long freeBlocksCount = metrics.get("freeBlocksCount", 0);

        long totalAllocations = metrics.get("totalAllocations", 0);
        long failedAllocations = metrics.get("failedAllocations", 0);

        long totalFrees = metrics.get("totalFrees", 0);
        long failedFrees = metrics.get("failedFrees", 0);

        return new MemoryStatistic(
                usedSize, freeSize,
                usedBlocksCount, freeBlocksCount,
                totalAllocations, failedAllocations,
                totalFrees, failedFrees);
    }
}

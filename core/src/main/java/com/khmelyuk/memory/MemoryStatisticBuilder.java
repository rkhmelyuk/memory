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
        MemorySize freeSize = MemorySize.bytes((int) metrics.getValueMetric("freeSize").get());
        MemorySize usedSize = MemorySize.bytes((int) metrics.getValueMetric("usedSize").get());

        long usedBlocksCount = metrics.getValueMetric("usedBlocksCount").get();
        long freeBlocksCount = metrics.getValueMetric("freeBlocksCount").get();

        long totalAllocations = metrics.getValueMetric("totalAllocations").get();
        long failedAllocations = metrics.getValueMetric("failedAllocations").get();

        long totalFrees = metrics.getValueMetric("totalFrees").get();
        long failedFrees = metrics.getValueMetric("failedFrees").get();

        return new MemoryStatistic(
                usedSize, freeSize,
                usedBlocksCount, freeBlocksCount,
                totalAllocations, failedAllocations,
                totalFrees, failedFrees);
    }
}

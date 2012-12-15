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
        MemorySize freeSize = MemorySize.bytes((int) metrics.getValueMetric("vmtable.freeSize").get());
        MemorySize usedSize = MemorySize.bytes((int) metrics.getValueMetric("vmtable.usedSize").get());

        long usedBlocksCount = metrics.getValueMetric("vmtable.usedBlocksCount").get();
        long freeBlocksCount = metrics.getValueMetric("vmtable.freeBlocksCount").get();

        long totalAllocations = metrics.getValueMetric("vmtable.totalAllocations").get();
        long failedAllocations = metrics.getValueMetric("vmtable.failedAllocations").get();

        long totalFrees = metrics.getValueMetric("vmtable.totalFrees").get();
        long failedFrees = metrics.getValueMetric("vmtable.failedFrees").get();

        return new MemoryStatistic(
                usedSize, freeSize,
                usedBlocksCount, freeBlocksCount,
                totalAllocations, failedAllocations,
                totalFrees, failedFrees);
    }
}

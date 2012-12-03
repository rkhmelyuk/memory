package com.khmelyuk.memory;

import com.khmelyuk.memory.metrics.MetricsSnapshot;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link MemoryStatisticBuilder}.
 *
 * @author Ruslan Khmelyuk
 */
public class MemoryStatisticBuilderTest {

    @Test
    public void build() {
        MetricsSnapshot metrics = createMetrics();
        MemoryStatisticBuilder builder = new MemoryStatisticBuilder(metrics);
        MemoryStatistic statistic = builder.build();

        assertThat(statistic.getFreeSize(), is(MemorySize.bytes(100)));
        assertThat(statistic.getUsedSize(), is(MemorySize.bytes(10)));
        assertThat(statistic.getFreeBlocksCount(), is(10L));
        assertThat(statistic.getUsedBlocksCount(), is(5L));
        assertThat(statistic.getTotalAllocations(), is(5L));
        assertThat(statistic.getFailedAllocations(), is(2L));
        assertThat(statistic.getTotalFrees(), is(6L));
        assertThat(statistic.getFailedFrees(), is(3L));
    }

    private MetricsSnapshot createMetrics() {
        MetricsSnapshot metrics = new MetricsSnapshot();

        metrics.put("freeSize", 100L);
        metrics.put("usedSize", 10L);
        metrics.put("freeBlocksCount", 10L);
        metrics.put("usedBlocksCount", 5L);
        metrics.put("totalAllocations", 5L);
        metrics.put("failedAllocations", 2L);
        metrics.put("totalFrees", 6L);
        metrics.put("failedFrees", 3L);

        return metrics;
    }
}

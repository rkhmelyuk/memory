package com.khmelyuk.memory;

import com.khmelyuk.memory.metrics.MetricsSnapshot;
import com.khmelyuk.memory.metrics.MetricsSnapshotBuilder;
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
        MetricsSnapshotBuilder builder = new MetricsSnapshotBuilder();

        builder.put("freeSize", 100L);
        builder.put("usedSize", 10L);
        builder.put("freeBlocksCount", 10L);
        builder.put("usedBlocksCount", 5L);
        builder.put("totalAllocations", 5L);
        builder.put("failedAllocations", 2L);
        builder.put("totalFrees", 6L);
        builder.put("failedFrees", 3L);

        return builder.build();
    }
}

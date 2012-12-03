package com.khmelyuk.memory;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link MemoryStatistic}.
 *
 * @author Ruslan Khmelyuk
 */
public class MemoryStatisticTest {

    @Test
    public void successAllocations() {
        MemoryStatistic stat = createMemoryStatistic();

        assertThat(stat.getSuccessAllocations(), is(9L));
        assertThat(stat.getSuccessAllocationsPercentage(), is(BigDecimal.valueOf(90).setScale(2)));
    }

    @Test
    public void successFrees() {
        MemoryStatistic stat = createMemoryStatistic();

        assertThat(stat.getSuccessFrees(), is(3L));
        assertThat(stat.getSuccessFreesPercentage(), is(BigDecimal.valueOf(60).setScale(2)));
    }

    @Test
    public void totalSize() {
        MemoryStatistic stat = createMemoryStatistic();

        assertThat(stat.getTotalSize(), is(150L));
    }

    private MemoryStatistic createMemoryStatistic() {
        return new MemoryStatistic(MemorySize.bytes(100), MemorySize.bytes(50), 10, 5, 10, 1, 5, 2);
    }
}

package com.khmelyuk.memory.metrics;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link TimerMetric}.
 *
 * @author Ruslan Khmelyuk
 */
public class TimerMetricTest {

    @Test
    public void initialsAreZero() {
        TimerMetric metric = new TimerMetric();
        assertThat(metric.getCount(), is(0L));
        assertThat(metric.getTime(), is(0L));
    }

    @Test
    public void update() {
        TimerMetric metric = new TimerMetric();

        metric.update(10L);
        assertThat(metric.getCount(), is(1L));
        assertThat(metric.getTime(), is(10L));

        metric.update(20L);
        assertThat(metric.getCount(), is(2L));
        assertThat(metric.getTime(), is(15L));

        metric.update(18L);
        assertThat(metric.getCount(), is(3L));
        assertThat(metric.getTime(), is(16L));
    }

    @Test
    public void reset() {
        TimerMetric metric = new TimerMetric();

        metric.update(10L);
        metric.reset();
        assertThat(metric.getCount(), is(0L));
        assertThat(metric.getTime(), is(0L));
    }

    @Test
    public void value() {
        TimerMetric metric = new TimerMetric();

        metric.update(10L);
        assertThat(metric.value(), is(10L));
    }
}

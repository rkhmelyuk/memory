package com.khmelyuk.memory.metrics;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link ValueMetric}.
 *
 * @author Ruslan Khmelyuk
 */
public class ValueMetricTest {

    @Test
    public void initValues() {
        ValueMetric metric = new ValueMetric();
        assertThat(metric.get(), is(0L));

        metric = new ValueMetric(10L);
        assertThat(metric.get(), is(10L));
    }

    @Test
    public void update() {
        ValueMetric metric = new ValueMetric();
        metric.update(10);
        assertThat(metric.get(), is(10L));

        metric.update(12);
        assertThat(metric.get(), is(12L));

        metric.update(-10);
        assertThat(metric.get(), is(-10L));

        metric.update(0);
        assertThat(metric.get(), is(0L));
    }

    @Test
    public void increment() {
        ValueMetric metric = new ValueMetric();
        metric.increment();
        assertThat(metric.get(), is(1L));

        metric.increment();
        assertThat(metric.get(), is(2L));

        metric.update(10);
        metric.increment();
        assertThat(metric.get(), is(11L));
    }

    @Test
    public void decrement() {
        ValueMetric metric = new ValueMetric();
        metric.decrement();
        assertThat(metric.get(), is(-1L));

        metric.decrement();
        assertThat(metric.get(), is(-2L));

        metric.update(10);
        metric.decrement();
        assertThat(metric.get(), is(9L));
    }

    @Test
    public void reset() {
        ValueMetric metric = new ValueMetric();

        metric.update(10);
        metric.reset();
        assertThat(metric.get(), is(0L));
    }

    @Test
    public void value() {
        ValueMetric metric = new ValueMetric();

        metric.update(10);
        assertThat(metric.value(), is(10L));
    }

}

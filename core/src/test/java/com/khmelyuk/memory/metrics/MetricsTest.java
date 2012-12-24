package com.khmelyuk.memory.metrics;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

/**
 * Tests for {@link Metrics}.
 *
 * @author Ruslan Khmelyuk
 */
public class MetricsTest {

    @Test
    public void addMetric() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("metricName");

        assertTrue(metrics.hasMetric("metricName"));
    }

    @Test
    public void addMetricInitsWithZero() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("metricName");

        assertThat(metrics.get("metricName"), is(0L));
    }

    @Test
    public void addMetricInitsWithSpecifiedValue() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("metricName", 10L);

        assertThat(metrics.get("metricName"), is(10L));
    }

    @Test(expected = DuplicateMetricException.class)
    public void addDuplicateMetricFails() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("metricName");
        metrics.addValueMetric("metricName");
    }

    @Test
    public void addTimerMetric() {
        Metrics metrics = new Metrics();
        metrics.addTimerMetric("timer");

        assertTrue(metrics.hasMetric("timer"));
        assertThat(metrics.getTimer("timer"), is(notNullValue()));
    }

    @Test
    public void get() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("metricName");

        assertThat(metrics.get("metricName"), is(0L));
    }

    @Test(expected = MetricNotFoundException.class)
    public void getForAbsentMetricFails() {
        Metrics metrics = new Metrics();
        metrics.get("absentMetricName");
    }

    @Test
    public void increment() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("metricName");
        metrics.increment("metricName");

        assertThat(metrics.get("metricName"), is(1L));
    }

    @Test(expected = MetricNotFoundException.class)
    public void incrementAbsentMetricFails() {
        Metrics metrics = new Metrics();
        metrics.increment("metricName");
    }

    @Test
    public void decrement() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("metricName");
        metrics.decrement("metricName");
        assertThat(metrics.get("metricName"), is(-1L));
    }

    @Test
    public void customDecrement() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("metricName", 10);
        metrics.decrement("metricName", 5);
        assertThat(metrics.get("metricName"), is(5L));
    }

    @Test(expected = MetricNotFoundException.class)
    public void decrementAbsentMetricFails() {
        Metrics metrics = new Metrics();
        metrics.decrement("metricName");
    }

    @Test
    public void mark() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("metricName");

        metrics.mark("metricName", 11L);
        assertThat(metrics.get("metricName"), is(11L));

        metrics.mark("metricName", 121L);
        assertThat(metrics.get("metricName"), is(121L));
    }

    @Test(expected = MetricNotFoundException.class)
    public void markAbsentMetricFails() {
        Metrics metrics = new Metrics();
        metrics.mark("metricName", 11L);
    }

    @Test
    public void reset() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("metric1", 11L);
        metrics.addValueMetric("metric2", 22L);
        metrics.addValueMetric("metric3", 33L);

        metrics.reset();

        assertThat(metrics.get("metric1"), is(0L));
        assertThat(metrics.get("metric2"), is(0L));
        assertThat(metrics.get("metric3"), is(0L));
    }

    @Test
    public void getTimer() {
        Metrics metrics = new Metrics();
        metrics.addTimerMetric("timer");

        TimeContext timer = metrics.getTimer("timer");
        assertThat(timer, notNullValue());
    }

    @Test(expected = MetricNotFoundException.class)
    public void getTimerForUnknownMetric() {
        Metrics metrics = new Metrics();
        metrics.getTimer("timer");
    }

    @Test
    public void getTimerMetric() {
        Metrics metrics = new Metrics();
        metrics.addTimerMetric("timer");
        assertNotNull(metrics.getTimerMetric("timer"));
    }

    @Test
    public void getValueMetric() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("value");
        assertNotNull(metrics.getValueMetric("value"));
    }
}

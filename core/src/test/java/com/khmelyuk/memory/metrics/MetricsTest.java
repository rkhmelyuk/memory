package com.khmelyuk.memory.metrics;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link Metrics}.
 *
 * @author Ruslan Khmelyuk
 */
public class MetricsTest {

    @Test
    public void addMetric() {
        Metrics metrics = new Metrics();
        metrics.addMetric("metricName");

        assertTrue(metrics.hasMetric("metricName"));
    }

    @Test
    public void addMetricInitsWithZero() {
        Metrics metrics = new Metrics();
        metrics.addMetric("metricName");

        assertThat(metrics.get("metricName"), is(0L));
    }

    @Test
    public void addMetricInitsWithSpecifiedValue() {
        Metrics metrics = new Metrics();
        metrics.addMetric("metricName", 10L);

        assertThat(metrics.get("metricName"), is(10L));
    }

    @Test(expected = DuplicateMetricException.class)
    public void addDuplicateMetricFails() {
        Metrics metrics = new Metrics();
        metrics.addMetric("metricName");
        metrics.addMetric("metricName");
    }

    @Test
    public void get() {
        Metrics metrics = new Metrics();
        metrics.addMetric("metricName");

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
        metrics.addMetric("metricName");
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
        metrics.addMetric("metricName");
        metrics.decrement("metricName");

        assertThat(metrics.get("metricName"), is(-1L));
    }

    @Test(expected = MetricNotFoundException.class)
    public void decrementAbsentMetricFails() {
        Metrics metrics = new Metrics();
        metrics.decrement("metricName");
    }

    @Test
    public void mark() {
        Metrics metrics = new Metrics();
        metrics.addMetric("metricName");

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
        metrics.addMetric("metric1", 11L);
        metrics.addMetric("metric2", 22L);
        metrics.addMetric("metric3", 33L);

        metrics.reset();

        assertThat(metrics.get("metric1"), is(0L));
        assertThat(metrics.get("metric2"), is(0L));
        assertThat(metrics.get("metric3"), is(0L));
    }
}

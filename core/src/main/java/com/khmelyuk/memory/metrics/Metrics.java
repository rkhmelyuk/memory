package com.khmelyuk.memory.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a set of metrics within single entity (this could be Memory, VMT etc.)
 * This is helpful to work with a set of different metrics as a single entity (init, reset, expose etc.)
 * <p/>
 * To avoid problem with mistakes in metric names, the single metric should be created first.
 * On create the metric is added to the registry and can be used and tracked.
 * <p/>
 * Updating a single metric is thread-safe, while updating a set of metrics, and adding a metric are not thread safe.
 *
 * @author Ruslan Khmelyuk
 */
public final class Metrics {

    private final ConcurrentMap<String, Metric> metrics = new ConcurrentHashMap<>();

    /**
     * Add a new timer metric. This used to calculate the time spend on some block of code.
     * <p/>
     * NOTE: at this moment it will add 2 different metrics - for count of examples and for avg value of them.
     *
     * @param name the metric name.
     */
    public void addTimerMetric(String name) {
        addMetric(name, new TimerMetric());
    }

    /**
     * Add a new metric with init value 0.
     * If metric with specified name exists, then wait for exception.
     *
     * @param name the metric name.
     */
    public void addValueMetric(String name) {
        addValueMetric(name, 0L);
    }

    /**
     * Add a new metric with specified init value.
     * If metric with specified name exists, then wait for exception.
     *
     * @param name      the metric name.
     * @param initValue the init value for metric.
     */
    public void addValueMetric(String name, long initValue) {
        addMetric(name, new ValueMetric(initValue));
    }

    private void addMetric(String name, Metric metric) {
        if (metrics.containsKey(name)) {
            throw new DuplicateMetricException(name);
        }

        metrics.putIfAbsent(name, metric);
    }

    /**
     * Check if metric with specified name exists.
     *
     * @param name the metric name.
     * @return true if exists.
     */
    public boolean hasMetric(String name) {
        return metrics.containsKey(name);
    }

    /**
     * Gets the timer metric by name.
     *
     * @param name the metric name.
     * @return the timer metric.
     */
    public TimerMetric getTimerMetric(String name) {
        return (TimerMetric) getMetric(name);
    }

    /**
     * Gets the value metric by name.
     *
     * @param name the metric name.
     * @return the value metric.
     */
    public ValueMetric getValueMetric(String name) {
        return (ValueMetric) getMetric(name);
    }


    /**
     * Gets the metric value.
     * If such metric not exists, then throws exception.
     *
     * @param metric the metric name.
     * @return the metric value.
     */
    public long get(String metric) {
        return getValueMetric(metric).get();
    }

    /**
     * Increments the metric value.
     *
     * @param metric the metric name.
     */
    public void increment(String metric) {
        getValueMetric(metric).increment();
    }

    /**
     * Decrements the metric value.
     *
     * @param metric the metric name.
     */
    public void decrement(String metric) {
        getValueMetric(metric).decrement();
    }

    /**
     * Decrements the metric value.
     *
     * @param metric the metric name.
     */
    public void decrement(String metric, long value) {
        getValueMetric(metric).decrement(value);
    }

    /**
     * Sets the value for the specified metric.
     *
     * @param metric the metric name.
     * @param value  the metric value.
     */
    public void mark(String metric, long value) {
        getValueMetric(metric).update(value);
    }

    /**
     * Gets the context for specified timer metric. This context can be used to gather single block metric information.
     *
     * @param name the timer metric name.
     * @return the created time context.
     */
    public TimeContext getTimer(String name) {
        return new TimeContext(getTimerMetric(name));
    }

    /**
     * Reset the value for all metrics, but not remove them.
     * It's not atomic operation, each metric is thrown to 0 one by one.
     */
    public void reset() {
        for (Metric each : metrics.values()) {
            each.reset();
        }
    }

    /**
     * Gets current metrics snapshot.
     *
     * @return the current metrics shapshot.
     */
    public MetricsSnapshot snapshot() {
        final Map<String, Metric> map = new HashMap<>();
        for (Map.Entry<String, Metric> each : metrics.entrySet()) {
            map.put(each.getKey(), each.getValue());
        }
        return new MapMetricsSnapshot(map);
    }

    private Metric getMetric(String name) {
        Metric metric = metrics.get(name);
        if (metric == null) {
            throw new MetricNotFoundException(name);
        }
        return metric;
    }
}

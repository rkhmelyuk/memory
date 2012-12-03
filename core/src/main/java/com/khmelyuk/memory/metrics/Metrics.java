package com.khmelyuk.memory.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

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

    private final ConcurrentMap<String, AtomicLong> metrics = new ConcurrentHashMap<>();

    /**
     * Add a new metric with init value 0.
     * If metric with specified name exists, then wait for exception.
     *
     * @param name the metric name.
     */
    public void addMetric(String name) {
        addMetric(name, 0L);
    }

    /**
     * Add a new metric with specified init value.
     * If metric with specified name exists, then wait for exception.
     *
     * @param name      the metric name.
     * @param initValue the init value for metric.
     */
    public void addMetric(String name, long initValue) {
        if (metrics.containsKey(name)) {
            throw new DuplicateMetricException(name);
        }

        metrics.putIfAbsent(name, new AtomicLong(initValue));
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
     * Gets the metric value.
     * If such metric not exists, then throws exception.
     *
     * @param metric the metric name.
     * @return the metric value.
     */
    public long get(String metric) {
        return getValue(metric).get();
    }

    /**
     * Increments the metric value.
     *
     * @param metric the metric name.
     */
    public void increment(String metric) {
        getValue(metric).incrementAndGet();
    }

    /**
     * Decrements the metric value.
     *
     * @param metric the metric name.
     */
    public void decrement(String metric) {
        getValue(metric).decrementAndGet();
    }

    /**
     * Sets the value for the specified metric.
     *
     * @param metric the metric name.
     * @param value  the metric value.
     */
    public void mark(String metric, long value) {
        getValue(metric).set(value);
    }

    /**
     * Reset the value for all metrics, but not remove them.
     * It's not atomic operation, each metric is thrown to 0 one by one.
     */
    public void reset() {
        for (AtomicLong each : metrics.values()) {
            each.set(0L);
        }
    }

    /**
     * Gets current metrics snapshot.
     *
     * @return the current metrics shapshot.
     */
    public MetricsSnapshot snapshot() {
        final MetricsSnapshot snapshot = new MetricsSnapshot();
        for (Map.Entry<String, AtomicLong> each : metrics.entrySet()) {
            snapshot.put(each.getKey(), each.getValue().get());
        }
        return snapshot;
    }

    private AtomicLong getValue(String name) {
        AtomicLong value = metrics.get(name);
        if (value == null) {
            throw new MetricNotFoundException(name);
        }
        return value;
    }
}

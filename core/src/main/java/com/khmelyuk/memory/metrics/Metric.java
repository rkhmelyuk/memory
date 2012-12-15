package com.khmelyuk.memory.metrics;

/**
 * Marks some class as metric.
 *
 * @author Ruslan Khmelyuk
 */
public interface Metric<T> {

    /**
     * Resets metric value.
     */
    void reset();

    /**
     * Gets the metric value.
     *
     * @return the metric value.
     */
    T value();

}

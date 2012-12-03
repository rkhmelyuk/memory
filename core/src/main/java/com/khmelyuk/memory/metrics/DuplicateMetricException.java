package com.khmelyuk.memory.metrics;

/**
 * Metric with specified name already exists.
 *
 * @author Ruslan Khmelyuk
 */
public class DuplicateMetricException extends RuntimeException {

    public DuplicateMetricException(String metricName) {
        super("Duplicate metric: " + metricName);
    }
}

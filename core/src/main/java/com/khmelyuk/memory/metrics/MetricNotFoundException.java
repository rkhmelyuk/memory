package com.khmelyuk.memory.metrics;

/**
 * Metric with specified name was not found.
 *
 * @author Ruslan Khmelyuk
 */
public class MetricNotFoundException extends RuntimeException {

    public MetricNotFoundException(String metricName) {
        super("Metric is not found: " + metricName);
    }
}

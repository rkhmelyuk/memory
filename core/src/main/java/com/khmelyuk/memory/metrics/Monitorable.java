package com.khmelyuk.memory.metrics;

/**
 * This interface should be extended by classes that expose some metrics information.
 * Client can use this interface to get the metrics for some part of functionality.
 *
 * @author Ruslan Khmelyuk
 */
public interface Monitorable {

    /**
     * Gets the metrics information.
     * This method shouldn't return {@code null}.
     *
     * @return the metrics information.
     */
    MetricsSnapshot getMetrics();

}

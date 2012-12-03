package com.khmelyuk.memory.metrics;

import java.util.Set;

/**
 * Represents a snapshot of metrics at some moment of time.
 *
 * @author Ruslan Khmelyuk
 */
public interface MetricsSnapshot {

    /**
     * Gets the set of metrics names.
     *
     * @return the set of metrics names.
     */
    Set<String> getMetrics();

    Long get(String metric);

    long get(String metric, long defaultValue);

    int getInt(String metric, int defaultValue);

    int size();

}

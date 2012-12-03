package com.khmelyuk.memory.metrics;

import java.util.HashMap;

/**
 * Represents a snapshot of metrics at some moment of time.
 *
 * @author Ruslan Khmelyuk
 */
public final class MetricsSnapshot extends HashMap<String, Long> {

    public long get(String metric, long defaultValue) {
        Long value = get(metric);
        return value != null ? value : defaultValue;
    }

    public int getInt(String metric, int defaultValue) {
        Long value = get(metric);
        return value != null ? value.intValue() : defaultValue;
    }

}

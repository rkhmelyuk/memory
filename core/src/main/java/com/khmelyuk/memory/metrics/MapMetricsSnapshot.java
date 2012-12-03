package com.khmelyuk.memory.metrics;

import com.khmelyuk.memory.annotation.Immutable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * {@link MetricsSnapshot} implementation based on Maps.
 *
 * @author Ruslan Khmelyuk
 */
@Immutable
public final class MapMetricsSnapshot implements MetricsSnapshot {

    private Map<String, Long> metrics;

    public MapMetricsSnapshot(Map<String, Long> metrics) {
        if (metrics != null) {
            this.metrics = Collections.unmodifiableMap(metrics);
        } else {
            this.metrics = Collections.emptyMap();
        }
    }

    public Set<String> getMetrics() {
        return metrics.keySet();
    }

    public Long get(String metric) {
        return metrics.get(metric);
    }

    public long get(String metric, long defaultValue) {
        Long value = get(metric);
        return value != null ? value : defaultValue;
    }

    public int getInt(String metric, int defaultValue) {
        Long value = get(metric);
        return value != null ? value.intValue() : defaultValue;
    }

    public int size() {
        return metrics.size();
    }

}

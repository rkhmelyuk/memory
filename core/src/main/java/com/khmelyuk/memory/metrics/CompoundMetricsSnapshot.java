package com.khmelyuk.memory.metrics;

import com.khmelyuk.memory.annotation.Immutable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a snapshot of metrics at some moment of time.
 *
 * @author Ruslan Khmelyuk
 */
@Immutable
class CompoundMetricsSnapshot implements MetricsSnapshot {

    private final List<MetricsSnapshot> snapshots;

    public CompoundMetricsSnapshot(List<MetricsSnapshot> snapshots) {
        this.snapshots = snapshots;
    }

    @Override
    public Set<String> getMetrics() {
        Set<String> result = new HashSet<>();
        for (MetricsSnapshot each : snapshots) {
            result.addAll(each.getMetrics());
        }
        return result;
    }

    @Override
    public Long get(String metric) {
        for (MetricsSnapshot each : snapshots) {
            Long value = each.get(metric);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public long get(String metric, long defaultValue) {
        Long value = get(metric);
        return value != null ? value : defaultValue;
    }

    public int getInt(String metric, int defaultValue) {
        Long value = get(metric);
        return value != null ? value.intValue() : defaultValue;
    }

    @Override
    public int size() {
        return getMetrics().size();
    }
}

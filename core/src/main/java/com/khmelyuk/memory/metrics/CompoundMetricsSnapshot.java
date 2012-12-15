package com.khmelyuk.memory.metrics;

import com.khmelyuk.memory.annotation.Immutable;

import java.util.Collections;
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

    public List<MetricsSnapshot> getSnapshots() {
        return Collections.unmodifiableList(snapshots);
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
    public ValueMetric getValueMetric(String metric) {
        for (MetricsSnapshot each : snapshots) {
            ValueMetric value = each.getValueMetric(metric);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public TimerMetric getTimerMetric(String metric) {
        for (MetricsSnapshot each : snapshots) {
            TimerMetric value = each.getTimerMetric(metric);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return getMetrics().size();
    }
}

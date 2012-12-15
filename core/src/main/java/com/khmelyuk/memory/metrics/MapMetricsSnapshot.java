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

    private Map<String, Metric> metrics;

    public MapMetricsSnapshot(Map<String, Metric> metrics) {
        if (metrics != null) {
            this.metrics = Collections.unmodifiableMap(metrics);
        } else {
            this.metrics = Collections.emptyMap();
        }
    }

    @Override
    public Set<String> getMetrics() {
        return metrics.keySet();
    }

    @Override
    public ValueMetric getValueMetric(String metric) {
        Metric result = metrics.get(metric);
        if (result instanceof ValueMetric) {
            return (ValueMetric) result;
        }
        return null;
    }

    @Override
    public TimerMetric getTimerMetric(String metric) {
        Metric result = metrics.get(metric);
        if (result instanceof TimerMetric) {
            return (TimerMetric) result;
        }
        return null;
    }

    @Override
    public int size() {
        return metrics.size();
    }

}

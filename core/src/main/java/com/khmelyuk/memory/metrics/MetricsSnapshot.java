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

    /**
     * Gets the value metric.
     *
     * @param metric the metric name.
     * @return the found value metric or null.
     */
    ValueMetric getValueMetric(String metric);

    /**
     * Gets the timer metric.
     *
     * @param metric the metric name.
     * @return the found timer metric or null.
     */
    TimerMetric getTimerMetric(String metric);

    int size();

}

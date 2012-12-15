package com.khmelyuk.memory.metrics;

/**
 * Factory for metrics fixtures.
 *
 * @author Ruslan Khmelyuk
 */
class FixtureFactory {

    public static Metrics createMetrics() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("metric1", 1);
        metrics.addValueMetric("metric2", 2);
        metrics.addValueMetric("metric3", 3);

        return metrics;
    }

    public static Metrics createOtherMetrics() {
        Metrics metrics = new Metrics();
        metrics.addValueMetric("otherMetric1", 1);
        metrics.addValueMetric("otherMetric2", 2);
        metrics.addValueMetric("otherMetric3", 3);

        return metrics;
    }

    public static MetricsSnapshot createMetricsSnapshot() {
        return createMetrics().snapshot();
    }

    public static MetricsSnapshot createOtherMetricsSnapshot() {
        return createOtherMetrics().snapshot();
    }

}

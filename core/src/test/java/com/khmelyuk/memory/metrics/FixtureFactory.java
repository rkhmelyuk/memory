package com.khmelyuk.memory.metrics;

/**
 * Factory for metrics fixtures.
 *
 * @author Ruslan Khmelyuk
 */
class FixtureFactory {

    public static Metrics createMetrics() {
        Metrics metrics = new Metrics();
        metrics.addMetric("metric1", 1);
        metrics.addMetric("metric2", 2);
        metrics.addMetric("metric3", 3);

        return metrics;
    }

    public static Metrics createOtherMetrics() {
        Metrics metrics = new Metrics();
        metrics.addMetric("otherMetric1", 1);
        metrics.addMetric("otherMetric2", 2);
        metrics.addMetric("otherMetric3", 3);

        return metrics;
    }

    public static MetricsSnapshot createMetricsSnapshot() {
        return createMetrics().snapshot();
    }

    public static MetricsSnapshot createOtherMetricsSnapshot() {
        return createOtherMetrics().snapshot();
    }

}

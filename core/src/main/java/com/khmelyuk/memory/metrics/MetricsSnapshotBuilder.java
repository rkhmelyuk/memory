package com.khmelyuk.memory.metrics;

import com.khmelyuk.memory.annotation.NotThreadSafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to build {@link MetricsSnapshot}, which is immutable.
 *
 * @author Ruslan Khmelyuk
 */
@NotThreadSafe
public final class MetricsSnapshotBuilder {

    private List<MetricsSnapshot> snapshots = new ArrayList<>();
    private Map<String, Long> extension = new HashMap<>();

    /**
     * Add metrics to the snapshot.
     *
     * @param metrics the metrics to add into snapshot.
     * @return the ref to this builder.
     */
    public MetricsSnapshotBuilder fromMetrics(Metrics metrics) {
        this.snapshots.add(metrics.snapshot());
        return this;
    }

    /**
     * Merges with other metrics. Actually, no actual merge so far.
     * First added/merged metric will be accessed and returned first.
     *
     * @param metrics the metrics to merge into existing metrics.
     * @return the ref to this builder.
     */
    public MetricsSnapshotBuilder merge(MetricsSnapshot metrics) {
        this.snapshots.add(metrics);
        return this;
    }

    /**
     * Add another metric to the snapshot.
     *
     * @param metric the metric name.
     * @param value  the metric value.
     * @return the ref to this builder.
     */
    public MetricsSnapshotBuilder put(String metric, long value) {
        this.extension.put(metric, value);
        return this;
    }

    /**
     * Build the {@link MetricsSnapshot} instance and return it.
     *
     * @return the metrics snapshot.
     */
    public MetricsSnapshot build() {
        MetricsSnapshot result = null;
        if (snapshots.size() == 1 && extension.size() == 0) {
            result = snapshots.get(0);
        } else if (snapshots.size() == 0) {
            result = new MapMetricsSnapshot(extension);
        } else {
            if (extension.size() > 0) {
                snapshots.add(new MapMetricsSnapshot(extension));
            }
            result = new CompoundMetricsSnapshot(snapshots);
        }

        snapshots = new ArrayList<>();
        extension = new HashMap<>();

        return result;
    }
}

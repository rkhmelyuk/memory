package com.khmelyuk.memory.metrics;

import org.junit.Test;

import static com.khmelyuk.memory.metrics.FixtureFactory.createMetrics;
import static com.khmelyuk.memory.metrics.FixtureFactory.createOtherMetricsSnapshot;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


/**
 * Test for {@link MetricsSnapshot}.
 *
 * @author Ruslan Khmelyuk
 */
public class MetricsSnapshotBuilderTest {

    @Test
    public void build() {
        MetricsSnapshotBuilder builder = new MetricsSnapshotBuilder();
        MetricsSnapshot snapshot = builder.fromMetrics(createMetrics()).build();

        assertThat(snapshot.size(), is(3));
        assertThat(snapshot.get("metric1"), is(1L));
        assertThat(snapshot.get("metric2"), is(2L));
        assertThat(snapshot.get("metric3"), is(3L));
        assertThat(snapshot.get("metric4"), nullValue());
    }

    @Test
    public void fromMetricsAndPut() {
        MetricsSnapshotBuilder builder = new MetricsSnapshotBuilder();
        MetricsSnapshot snapshot = builder.fromMetrics(createMetrics()).put("metric4", 4L).build();

        assertThat(snapshot.get("metric1"), is(1L));
        assertThat(snapshot.get("metric2"), is(2L));
        assertThat(snapshot.get("metric3"), is(3L));
        assertThat(snapshot.get("metric4"), is(4L));
    }

    @Test
    public void put() {
        MetricsSnapshotBuilder builder = new MetricsSnapshotBuilder();
        MetricsSnapshot snapshot = builder.put("metric1", 1L).build();

        assertThat(snapshot.get("metric1"), is(1L));
    }

    @Test
    public void merge() {
        MetricsSnapshotBuilder builder = new MetricsSnapshotBuilder();
        MetricsSnapshot snapshot = builder
                .fromMetrics(createMetrics())
                .merge(createOtherMetricsSnapshot()).build();

        assertThat(snapshot.get("metric1"), is(1L));
        assertThat(snapshot.get("metric2"), is(2L));
        assertThat(snapshot.get("metric3"), is(3L));
        assertThat(snapshot.get("otherMetric1"), is(1L));
        assertThat(snapshot.get("otherMetric2"), is(2L));
        assertThat(snapshot.get("otherMetric3"), is(3L));
    }

    @Test
    public void mergeAndPut() {
        MetricsSnapshotBuilder builder = new MetricsSnapshotBuilder();
        MetricsSnapshot snapshot = builder
                .fromMetrics(createMetrics())
                .merge(createOtherMetricsSnapshot())
                .put("metric4", 4).build();

        assertThat(snapshot.get("metric1"), is(1L));
        assertThat(snapshot.get("metric2"), is(2L));
        assertThat(snapshot.get("metric3"), is(3L));
        assertThat(snapshot.get("metric4"), is(4L));
        assertThat(snapshot.get("otherMetric1"), is(1L));
        assertThat(snapshot.get("otherMetric2"), is(2L));
        assertThat(snapshot.get("otherMetric3"), is(3L));
    }

}

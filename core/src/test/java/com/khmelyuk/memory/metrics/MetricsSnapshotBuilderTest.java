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
        assertThat(snapshot.getValueMetric("metric1").get(), is(1L));
        assertThat(snapshot.getValueMetric("metric2").get(), is(2L));
        assertThat(snapshot.getValueMetric("metric3").get(), is(3L));
        assertThat(snapshot.getValueMetric("metric4"), nullValue());
        assertThat(snapshot.getTimerMetric("timer1"), nullValue());
    }

    @Test
    public void fromMetricsAndPut() {
        MetricsSnapshotBuilder builder = new MetricsSnapshotBuilder();
        MetricsSnapshot snapshot = builder.fromMetrics(createMetrics()).put("metric4", 4L).build();

        assertThat(snapshot.getValueMetric("metric1").get(), is(1L));
        assertThat(snapshot.getValueMetric("metric2").get(), is(2L));
        assertThat(snapshot.getValueMetric("metric3").get(), is(3L));
        assertThat(snapshot.getValueMetric("metric4").get(), is(4L));
    }

    @Test
    public void put() {
        MetricsSnapshotBuilder builder = new MetricsSnapshotBuilder();
        MetricsSnapshot snapshot = builder.put("metric1", 1L).build();

        assertThat(snapshot.getValueMetric("metric1").get(), is(1L));
    }

    @Test
    public void merge() {
        MetricsSnapshotBuilder builder = new MetricsSnapshotBuilder();
        MetricsSnapshot snapshot = builder
                .fromMetrics(createMetrics())
                .merge(createOtherMetricsSnapshot()).build();

        assertThat(snapshot.getValueMetric("metric1").get(), is(1L));
        assertThat(snapshot.getValueMetric("metric2").get(), is(2L));
        assertThat(snapshot.getValueMetric("metric3").get(), is(3L));
        assertThat(snapshot.getValueMetric("otherMetric1").get(), is(1L));
        assertThat(snapshot.getValueMetric("otherMetric2").get(), is(2L));
        assertThat(snapshot.getValueMetric("otherMetric3").get(), is(3L));
    }

    @Test
    public void mergeAndPut() {
        MetricsSnapshotBuilder builder = new MetricsSnapshotBuilder();
        MetricsSnapshot snapshot = builder
                .fromMetrics(createMetrics())
                .merge(createOtherMetricsSnapshot())
                .put("metric4", 4).build();

        assertThat(snapshot.getValueMetric("metric1").get(), is(1L));
        assertThat(snapshot.getValueMetric("metric2").get(), is(2L));
        assertThat(snapshot.getValueMetric("metric3").get(), is(3L));
        assertThat(snapshot.getValueMetric("metric4").get(), is(4L));
        assertThat(snapshot.getValueMetric("otherMetric1").get(), is(1L));
        assertThat(snapshot.getValueMetric("otherMetric2").get(), is(2L));
        assertThat(snapshot.getValueMetric("otherMetric3").get(), is(3L));
    }

}

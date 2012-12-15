package com.khmelyuk.memory.metrics;

import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

import static com.khmelyuk.memory.metrics.FixtureFactory.createMetricsSnapshot;
import static com.khmelyuk.memory.metrics.FixtureFactory.createOtherMetricsSnapshot;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

/**
 * Test for {@link com.khmelyuk.memory.metrics.MetricsSnapshot}.
 *
 * @author Ruslan Khmelyuk
 */
public class CompoundMetricsSnapshotTest {

    @Test
    public void getMetrics() {
        CompoundMetricsSnapshot snapshot = new CompoundMetricsSnapshot(
                Arrays.asList(createMetricsSnapshot(), createOtherMetricsSnapshot()));

        Set<String> metrics = snapshot.getMetrics();
        assertThat(metrics, hasItems(
                "metric1", "metric2", "metric3",
                "otherMetric1", "otherMetric2", "otherMetric3"));
    }

    @Test
    public void get() {
        CompoundMetricsSnapshot snapshot = new CompoundMetricsSnapshot(
                Arrays.asList(createMetricsSnapshot(), createOtherMetricsSnapshot()));

        assertThat(snapshot.getValueMetric("metric1").get(), is(1L));
        assertThat(snapshot.getValueMetric("metric2").get(), is(2L));
        assertThat(snapshot.getValueMetric("metric3").get(), is(3L));
        assertThat(snapshot.getValueMetric("otherMetric1").get(), is(1L));
        assertThat(snapshot.getValueMetric("otherMetric2").get(), is(2L));
        assertThat(snapshot.getValueMetric("otherMetric3").get(), is(3L));
        assertThat(snapshot.getValueMetric("unknownMetric"), is(nullValue()));
    }

    @Test
    public void size() {
        CompoundMetricsSnapshot snapshot = new CompoundMetricsSnapshot(
                Arrays.asList(createMetricsSnapshot(), createOtherMetricsSnapshot()));

        assertThat(snapshot.size(), is(6));
    }


}

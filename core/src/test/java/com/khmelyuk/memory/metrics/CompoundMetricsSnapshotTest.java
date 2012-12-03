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

        assertThat(snapshot.get("metric1"), is(1L));
        assertThat(snapshot.get("metric2"), is(2L));
        assertThat(snapshot.get("metric3"), is(3L));
        assertThat(snapshot.get("otherMetric1"), is(1L));
        assertThat(snapshot.get("otherMetric2"), is(2L));
        assertThat(snapshot.get("otherMetric3"), is(3L));
        assertThat(snapshot.get("unknownMetric"), is(nullValue()));
    }

    @Test
    public void size() {
        CompoundMetricsSnapshot snapshot = new CompoundMetricsSnapshot(
                Arrays.asList(createMetricsSnapshot(), createOtherMetricsSnapshot()));

        assertThat(snapshot.size(), is(6));
    }


}

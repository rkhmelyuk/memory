package com.khmelyuk.memory.metrics;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * Test for {@link TimeContext}.
 *
 * @author Ruslan Khmelyuk
 */
public class TimeContextTest {

    Metrics metrics;

    @Before
    public void before() {
        metrics = new Metrics();
    }

    @Test
    public void start() {
        TimeContext context = createTimeContext();
        assertTrue(context.start());
    }

    @Test
    public void stop() {
        TimeContext context = createTimeContext();
        assertTrue(context.start());
        assertTrue(context.stop());
    }

    @Test
    public void stopIfNotStartedFails() {
        TimeContext context = createTimeContext();
        assertFalse(context.stop());
    }

    @Test
    public void startIfNotStartedFails() {
        TimeContext context = createTimeContext();
        assertTrue(context.start());
        assertFalse(context.start());
    }

    @Test
    public void updatesMetrics() {
        TimeContext context = createTimeContext();
        context.start();
        for (int i = 0; i < 9999999; i++) ;
        context.stop();

        assertThat(metrics.getTimerMetric("timer").getCount(), is(1L));
        assertThat(metrics.getTimerMetric("timer").getTime(), is(not(0L)));

        context.start();
        context.stop();
        assertThat(metrics.getTimerMetric("timer").getCount(), is(2L));
    }

    private TimeContext createTimeContext() {
        metrics.addTimerMetric("timer");
        return metrics.getTimer("timer");
    }

}

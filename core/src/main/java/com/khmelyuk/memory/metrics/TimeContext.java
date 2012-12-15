package com.khmelyuk.memory.metrics;

/**
 * Represents a context for time metric.
 * This context used to gather information for single operation.
 *
 * @author Ruslan Khmelyuk
 */
public class TimeContext {

    private final TimerMetric metric;
    private long startTime = -1;

    public TimeContext(TimerMetric timerMetric) {
        this.metric = timerMetric;
    }

    public boolean start() {
        if (startTime >= 0) {
            return false;
        }
        startTime = System.nanoTime();
        return true;
    }

    public boolean stop() {
        if (startTime < 0) {
            return false;
        }
        long time = System.nanoTime() - startTime;
        startTime = -1;

        metric.update(time);

        return true;
    }
}

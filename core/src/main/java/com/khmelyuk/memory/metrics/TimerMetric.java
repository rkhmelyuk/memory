package com.khmelyuk.memory.metrics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * This metric to be used to calculate the average time for some operation.
 *
 * @author Ruslan Khmelyuk
 */
public class TimerMetric implements Metric<Long> {

    private final AtomicLong time;
    private final AtomicLong count;

    public TimerMetric() {
        time = new AtomicLong(0);
        count = new AtomicLong(0);
    }

    public long getTime() {
        return time.longValue();
    }

    public long getCount() {
        return count.longValue();
    }

    /**
     * Updates the average time.
     *
     * @param newTime the new time to mark.
     */
    public void update(long newTime) {
        long count = this.count.get();
        long time = this.time.get();

        // TODO: make it concurrent safe
        long newAvgTime = (time * count + newTime) / (count + 1);
        if (this.time.compareAndSet(time, newAvgTime)) {
            this.count.incrementAndGet();
        }
    }

    @Override
    public void reset() {
        time.set(0);
        count.set(0);
    }

    @Override
    public Long value() {
        return getTime();
    }
}

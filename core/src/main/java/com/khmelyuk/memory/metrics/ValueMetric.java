package com.khmelyuk.memory.metrics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Implements the metric used to work with long values.
 *
 * @author Ruslan Khmelyuk
 */
public class ValueMetric implements Metric<Long> {

    private final AtomicLong value;

    public ValueMetric() {
        this(0L);
    }

    public ValueMetric(long value) {
        this.value = new AtomicLong(value);
    }

    public long get() {
        return this.value.get();
    }

    public void update(long value) {
        this.value.set(value);
    }

    public void increment() {
        this.value.incrementAndGet();
    }

    public void decrement() {
        this.value.decrementAndGet();
    }

    public void decrement(long value) {
        this.value.addAndGet(-value);
    }

    @Override
    public void reset() {
        update(0);
    }

    @Override
    public Long value() {
        return get();
    }
}

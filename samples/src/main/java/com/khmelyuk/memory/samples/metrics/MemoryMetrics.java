package com.khmelyuk.memory.samples.metrics;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.*;
import com.yammer.metrics.reporting.ConsoleReporter;
import com.yammer.metrics.reporting.CsvReporter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A class that helps to gather metrics during testing.
 *
 * @author Ruslan Khmelyuk
 */
public class MemoryMetrics {

    private static Map<String, Gauge> gauges = new HashMap<String, Gauge>();
    private static Map<String, Counter> counters = new HashMap<String, Counter>();
    private static Map<String, Histogram> histograms = new HashMap<String, Histogram>();
    private static Map<String, Meter> meters = new HashMap<String, Meter>();
    private static Map<String, Timer> timers = new HashMap<String, Timer>();
    private static Map<String, TimerContext> timerContexts = new HashMap<String, TimerContext>();

    static {
        ConsoleReporter.enable(500, TimeUnit.MILLISECONDS);
        CsvReporter.enable(new File("/Users/ruslan/projects/memory/core/measurements/"), 500, TimeUnit.MILLISECONDS);
    }

    public static void reset() {
        for (Counter counter : counters.values()) {
            counter.clear();
        }
        for (Meter meter : meters.values()) {
            meter.stop();
        }
        for (Timer timer : timers.values()) {
            timer.stop();
        }

        counters.clear();
        meters.clear();
        timers.clear();
        timerContexts.clear();
        histograms.clear();
        gauges.clear();
    }

    public static void addMetric(String name, Gauge gauge) {
        if (gauges.get(name) == null) {
            gauge = Metrics.newGauge(MemoryMetrics.class, name, gauge);
            gauges.put(name, gauge);

        }
    }

    public static void meter(String metric, long value) {
        getMeter(metric).mark(value);
    }

    public static void update(String metric, long value) {
        getHistogram(metric).update(value);
    }

    public static void startTimer(String metric) {
        TimerContext context = getTimer(metric).time();
        timerContexts.put(metric, context);
    }

    public static void stopTimer(String metric) {
        timerContexts.get(metric).stop();
    }

    public static void increment(String metric) {
        getCounter(metric).inc();
    }

    public static void decrement(String metric) {
        getCounter(metric).dec();
    }

    private static Counter getCounter(String metric) {
        Counter counter = counters.get(metric);
        if (counter == null) {
            counter = Metrics.newCounter(new MetricName(MemoryMetrics.class, metric));
            counters.put(metric, counter);
        }
        return counter;
    }

    private static Meter getMeter(String metric) {
        Meter meter = meters.get(metric);
        if (meter == null) {
            meter = Metrics.newMeter(MemoryMetrics.class, metric, "test", TimeUnit.SECONDS);
            meters.put(metric, meter);
        }
        return meter;
    }

    private static Timer getTimer(String metric) {
        Timer timer = timers.get(metric);
        if (timer == null) {
            timer = Metrics.newTimer(MemoryMetrics.class, metric, TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS);
            timers.put(metric, timer);
        }
        return timer;
    }

    private static Histogram getHistogram(String metric) {
        Histogram histogram = histograms.get(metric);
        if (histogram == null) {
            histogram = Metrics.newHistogram(MemoryMetrics.class, metric);
            histograms.put(metric, histogram);
        }
        return histogram;
    }

}

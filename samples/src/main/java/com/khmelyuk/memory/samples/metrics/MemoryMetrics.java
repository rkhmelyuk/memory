package com.khmelyuk.memory.samples.metrics;

import com.khmelyuk.memory.metrics.MetricsSnapshot;
import com.khmelyuk.memory.metrics.Monitorable;
import com.khmelyuk.memory.metrics.TimerMetric;
import com.khmelyuk.memory.metrics.ValueMetric;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.reporting.CsvReporter;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A class that helps to gather metrics during testing.
 *
 * @author Ruslan Khmelyuk
 */
public class MemoryMetrics {

    public static void init(String sampleName, long reportTimeMillis) {
        File dir = new File("./samples/measurements/" + sampleName + "/");
        if (dir.exists()) {
            removeDirectory(dir);
        }
        dir.mkdirs();
        CsvReporter.enable(dir, reportTimeMillis, TimeUnit.MILLISECONDS);
        //ConsoleReporter.enable(10, TimeUnit.MILLISECONDS);
    }

    public static void monitor(Monitorable monitorable) {
        // --- init monitoring
        final Map<TimerMetric, Timer> timers = new HashMap<>();

        MetricsSnapshot snapshot = monitorable.getMetrics();
        final Collection<String> metrics = snapshot.getMetrics();
        for (String metricName : metrics) {
            final ValueMetric valueMetric = snapshot.getValueMetric(metricName);
            if (valueMetric != null) {
                Metrics.newGauge(MemoryMetrics.class, metricName, new Gauge<Long>() {
                    //                    long prevValue = 0;
                    @Override
                    public Long value() {
                        return valueMetric.get();
                    }
                });
                continue;
            }

            final TimerMetric timerMetric = snapshot.getTimerMetric(metricName);
            if (timerMetric != null) {
                final Timer timer = Metrics.newTimer(MemoryMetrics.class, metricName);
                timers.put(timerMetric, timer);
                continue;
            }

            System.err.println("Unknown metric " + metricName);
        }

        Metrics.newGauge(ConcurrencyTestCase.class, "timestamp", new Gauge<Long>() {
            @Override
            public Long value() {
                for (Map.Entry<TimerMetric, Timer> entry : timers.entrySet()) {
                    TimerMetric timerMetric = entry.getKey();
                    Timer timer = entry.getValue();

                    long count = timer.count();
                    long newCount = timerMetric.getCount();
                    long newTime = timerMetric.getTime();
                    for (long i = count; i < newCount; i++) {
                        timer.update(newTime, TimeUnit.NANOSECONDS);
                    }
                }
                return System.currentTimeMillis();
            }
        });
    }


    public static void monitorSystemResources() {
        Metrics.newGauge(MemoryMetrics.class, "cpu", new Gauge<Long>() {
            @Override
            public Long value() {
                return (long) (100 * ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
            }
        });
        Metrics.newGauge(MemoryMetrics.class, "memory", new Gauge<Long>() {
            @Override
            public Long value() {
                return Runtime.getRuntime().freeMemory() / 1024;
            }
        });
    }

    private static void removeDirectory(File dir) {
        File[] files = dir.listFiles();
        for (File each : files) {
            each.delete();
        }
        dir.delete();
    }
}

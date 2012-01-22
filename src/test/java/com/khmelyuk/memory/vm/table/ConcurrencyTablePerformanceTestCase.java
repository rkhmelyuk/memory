package com.khmelyuk.memory.vm.table;

import com.khmelyuk.memory.Memory;
import org.junit.Test;

/**
 * Test performance for different linked virtual memory tables
 *
 * @author Ruslan Khmelyuk
 */
public class ConcurrencyTablePerformanceTestCase {

    static final int N = 5;
    static final int SIZE = 2 * Memory.MB;
    static final int COUNT_COEFF = 2000;
    static final int THREAD_NUM = 3;

    @Test
    public void testLinkedTablePerformance() throws Exception {
        testPerformance(new LinkedVirtualMemoryTable(SIZE), 0, THREAD_NUM);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(new LinkedVirtualMemoryTable(SIZE), i, THREAD_NUM);
        }

        System.out.println("Linked: Avg. duration " + (total / N) + "ms");
    }


    @Test
    public void testQueueTablePerformance() throws Exception {
        testPerformance(new QueueVirtualMemoryTable(SIZE), 0, THREAD_NUM);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(new QueueVirtualMemoryTable(SIZE), i, THREAD_NUM);
        }

        System.out.println("Queue: Avg. duration " + (total / N) + "ms");
    }

    @Test
    public void testLinkedTableQuality() throws Exception {
        testQuality(new LinkedVirtualMemoryTable(SIZE), 0, THREAD_NUM);
        float total = 0;
        for (int i = 0; i < N; i++) {
            total += testQuality(new LinkedVirtualMemoryTable(SIZE), i, THREAD_NUM);
        }

        System.out.println("Linked: Avg. nulls percentage " + Math.round(total / N) + "%");
    }

    @Test
    public void testQueueTableQuality() throws Exception {
        testQuality(new QueueVirtualMemoryTable(SIZE), 0, THREAD_NUM);
        float total = 0;
        for (int i = 0; i < N; i++) {
            total += testQuality(new QueueVirtualMemoryTable(SIZE), i, THREAD_NUM);
        }

        System.out.println("Queue: Avg. nulls percentage " + Math.round(total / N) + "%");
    }

    private static long testPerformance(final VirtualMemoryTable table, int n, int threadsNum) throws Exception {
        final int max = COUNT_COEFF * (n + 5);
        final int avgBlockSize = table.getFreeMemorySize() / max;
        final Block[] blocks = new Block[max];

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int nullsCount = 0;
                for (int i = 0; i < max; i++) {
                    blocks[i] = table.allocate(avgBlockSize);
                    if (blocks[i] == null) {
                        nullsCount++;
                    }
                    if (i % 2 == 0) {
                        free(table, blocks, i - 1);
                    }
                }
            }
        };

        long begin = System.currentTimeMillis();
        Thread[] threads = new Thread[threadsNum];
        for (int i = 0; i < threadsNum; i++) {
            threads[i] = new Thread(runnable);
            threads[i].start();
        }

        for (int i = 0; i < threadsNum; i++) {
            threads[i].join();
        }

        return (System.currentTimeMillis() - begin);
    }

    private static float testQuality(final VirtualMemoryTable table, int n, int threadsNum) throws Exception {
        final int max = COUNT_COEFF * (n + 5);
        final int avgBlockSize = table.getFreeMemorySize() / max;
        final Block[] blocks = new Block[max];

        final float[] nullsPercentTotal = new float[] {0};

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int nullsCount = 0;
                for (int i = 0; i < max; i++) {
                    blocks[i] = table.allocate(avgBlockSize);
                    if (blocks[i] == null) {
                        nullsCount++;
                    }
                    if (i % 2 == 0) {
                        free(table, blocks, i - 1);
                    }
                }

                nullsPercentTotal[0] +=  ((float)nullsCount / max) * 100;
            }
        };

        Thread[] threads = new Thread[threadsNum];
        for (int i = 0; i < threadsNum; i++) {
            threads[i] = new Thread(runnable);
            threads[i].start();
        }

        for (int i = 0; i < threadsNum; i++) {
            threads[i].join();
        }

        return (nullsPercentTotal[0] / threadsNum);
    }

    private static void free(VirtualMemoryTable table, Block[] blocks, int n) {
        if (n > 2) {
            table.free(blocks[n - 2]);
        }
    }
}

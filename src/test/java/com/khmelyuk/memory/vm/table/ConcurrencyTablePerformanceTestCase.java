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
    static final int SIZE = 1 * Memory.MB;
    static final int COUNT_COEFF = 1000;
    static final int THREAD_NUM = 8;

    @Test
    public void testLinkedTablePerformance() throws Exception {
        testPerformance(new LinkedVirtualMemoryTable(SIZE), 0, THREAD_NUM);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(new LinkedVirtualMemoryTable(SIZE), i, THREAD_NUM);
        }

        System.out.println("Linked: Avg. duration " + (total / N) + "ms");
    }

    private static long testPerformance(final VirtualMemoryTable table, int n, int threadsNum) throws Exception {
        final int max = COUNT_COEFF * (n + 5);
        final int avgBlockSize = table.getFreeMemorySize() / max;
        final Block[] blocks = new Block[max];

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < max; i++) {
                    blocks[i] = table.allocate(avgBlockSize);
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

    private static void free(VirtualMemoryTable table, Block[] blocks, int n) {
        if (n > 2) {
            table.free(blocks[n - 2]);
        }
    }
}

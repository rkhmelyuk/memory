package com.khmelyuk.memory.vm.table;

import com.khmelyuk.memory.MemorySize;
import org.junit.Test;

/**
 * Test performance for different linked virtual memory tables
 *
 * @author Ruslan Khmelyuk
 */
public class TablePerformanceTest {

    static final int N = 20;
    static final int SIZE = MemorySize.megabytes(2).getBytes();
    static final int COUNT_COEFF = 2000;

    @Test(timeout = 500)
    public void testLinkedTablePerformance() {
        testPerformance(new LinkedVirtualMemoryTable(SIZE), 0);
        long total = 0;
        for (int i = 0; i < N; i++) {
            total += testPerformance(new LinkedVirtualMemoryTable(SIZE), i);
        }

        System.out.println("Linked: Avg. duration " + (total / N) + "ms");
    }

    private static long testPerformance(final VirtualMemoryTable table, int n) {
        final int max = COUNT_COEFF * (n + 5);
        final int avgBlockSize = table.getFreeMemorySize() / max;
        final Block[] blocks = new Block[max];

        long begin = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
            blocks[i] = table.allocate(avgBlockSize);
            if (i != 0) {
                free(table, blocks, i - 1);
            }
        }
        return (System.currentTimeMillis() - begin);
    }

    private static void free(VirtualMemoryTable table, Block[] blocks, int n) {
        if (n > 2) {
            table.free(blocks[n - 2]);
        }
    }
}

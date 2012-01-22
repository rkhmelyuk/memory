package com.khmelyuk.memory.tools;

import com.khmelyuk.memory.vm.table.Block;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ruslan Khmelyuk
 */
public class VirtualMemoryTableVisualizerTestCase {

    VirtualMemoryTableVisualizer visualizer = new VirtualMemoryTableVisualizer();

    @Test
    public void testFree() {
        VirtualMemoryTable table = new LinkedVirtualMemoryTable(100);
        Block block1 = table.allocate(10);
        table.allocate(20);
        Block block3 = table.allocate(15);

        Assert.assertTrue(table.free(block1));

        table.allocate(5);
        block1 = table.allocate(5);

        Assert.assertTrue(table.free(block1));
        Assert.assertTrue(table.free(block3));
        table.allocate(50);

        Assert.assertEquals(
                "xxxxx.....xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx....................",
                visualizer.getUsageAsString(table, 1));
    }
}

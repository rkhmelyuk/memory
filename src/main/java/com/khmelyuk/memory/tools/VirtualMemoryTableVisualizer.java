package com.khmelyuk.memory.tools;

import com.khmelyuk.memory.util.FormatUtil;
import com.khmelyuk.memory.vm.table.Block;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;

/**
 * This tool to visualize the virtual memory table.
 *
 * @author Ruslan Khmelyuk
 */
public class VirtualMemoryTableVisualizer {

    private static final char Free = '.';
    private static final char Used = 'x';

    public void printUsage(VirtualMemoryTable table, int blockSize) {
        boolean[] model = buildTableModel(table, blockSize);

        StringBuilder builder = new StringBuilder(model.length);
        for (int i = 0; i < model.length; i++) {
            builder.append(model[i] ? Used : Free);
            if ((i + 1) % 100 == 0) {
                builder.append('\n');
            }
        }

        System.out.println(builder.toString());

        final int freeSize = table.getFreeMemorySize();
        final int usedSize = table.getUsedMemorySize();
        final int totalSize = freeSize + usedSize;

        System.out.println("Used\t"
                + FormatUtil.sizeAsString(usedSize)
                + "\t" + FormatUtil.getPercent(usedSize, totalSize) + "%"
                + "\t" + table.getUsed().size() + " blocks");
        System.out.println("Free\t" + FormatUtil.sizeAsString(freeSize)
                + "\t" + FormatUtil.getPercent(freeSize, totalSize) + "%"
                + "\t" + table.getFree().size() + " blocks");
    }

    public String getUsageAsString(VirtualMemoryTable table, int blockSize) {
        boolean[] model = buildTableModel(table, blockSize);

        StringBuilder builder = new StringBuilder(model.length);
        for (boolean each : model) {
            builder.append(each ? Used : Free);
        }

        return builder.toString();
    }

    public boolean[] buildTableModel(VirtualMemoryTable table, int blockSize) {
        int totalSize = table.getFreeMemorySize() + table.getUsedMemorySize();
        assert blockSize > 0 : "Block size can't be 0 or less.";

        int len = (int) Math.ceil(totalSize / blockSize);
        boolean[] data = new boolean[len];
        for (Block each : table.getUsed()) {
            int startAddress = calcAddress(blockSize, each.getAddress());
            int endAddress = calcAddress(blockSize, each.getAddress() + each.getSize());

            for (int i = startAddress; i < endAddress; i++) {
                data[i] = true;
            }
        }
        return data;
    }

    private int calcAddress(int blockSize, int address) {
        return (int) Math.ceil(address / blockSize);
    }

}

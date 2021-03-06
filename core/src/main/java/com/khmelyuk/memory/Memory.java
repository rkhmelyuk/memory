package com.khmelyuk.memory;

import com.khmelyuk.memory.metrics.Metrics;
import com.khmelyuk.memory.metrics.MetricsSnapshot;
import com.khmelyuk.memory.metrics.MetricsSnapshotBuilder;
import com.khmelyuk.memory.metrics.Monitorable;
import com.khmelyuk.memory.space.FreeSpaceListener;
import com.khmelyuk.memory.space.MemorySpace;
import com.khmelyuk.memory.space.Space;
import com.khmelyuk.memory.vm.VirtualMemory;
import com.khmelyuk.memory.vm.VirtualMemoryBlock;

/**
 * Represents a memory block.
 *
 * @author Ruslan Khmelyuk
 */
public class Memory implements Monitorable {

    private final VirtualMemory vm;
    private final FreeSpaceListener freeSpaceListener;
    private final Metrics metrics;

    public Memory(VirtualMemory vm) {
        this.vm = vm;

        metrics = new Metrics();
        metrics.addValueMetric("memory.spaces");

        freeSpaceListener = new FreeSpaceListener() {
            public void onFreeSpace(Space space) {
                Memory.this.vm.free(space.getBlock());
                metrics.decrement("memory.spaces");
            }
        };
    }

    /**
     * Allocates a memory space of specified size.
     *
     * @param length the memory size.
     * @return the new space
     * @throws OutOfMemoryException error to allocate a memory.
     */
    public MemorySpace allocate(int length) throws OutOfMemoryException {
        final VirtualMemoryBlock block = vm.allocate(length);
        metrics.increment("memory.spaces");
        return new MemorySpace(this, block, freeSpaceListener);
    }

    /**
     * Allocates a memory space of specified size.
     *
     * @param size the memory size.
     * @return the new space
     * @throws OutOfMemoryException error to allocate a memory.
     * @see Memory#allocate(int)
     */
    public MemorySpace allocate(MemorySize size) throws OutOfMemoryException {
        return allocate(size.getBytes());
    }

    /**
     * Frees the memory.
     */
    public void free() {
        vm.free();
    }

    /**
     * Gets the memory size.
     *
     * @return the memory size.
     */
    public int size() {
        return vm.size();
    }

    /**
     * Gets the size of free memory.
     *
     * @return the size of free memory.
     */
    public int getFreeMemorySize() {
        return vm.getFreeSize();
    }

    /**
     * Gets the size of used memory.
     *
     * @return the size of used memory.
     */
    public int getUsedMemorySize() {
        return vm.getUsedSize();
    }

    /**
     * Returns the memory statistic information.
     * The returned instance is a snapshot, so it's not updated
     *
     * @return the new instance with memory statistic information.
     */
    public MemoryStatistic getStatistic() {
        return new MemoryStatisticBuilder(getMetrics()).build();
    }

    /**
     * Return the metrics information.
     *
     * @return the metrics information.
     */
    public MetricsSnapshot getMetrics() {
        return new MetricsSnapshotBuilder().fromMetrics(metrics).merge(vm.getMetrics()).build();
    }
}

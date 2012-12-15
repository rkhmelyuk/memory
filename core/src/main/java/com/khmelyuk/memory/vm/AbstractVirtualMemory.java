package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.metrics.Metrics;
import com.khmelyuk.memory.metrics.MetricsSnapshot;
import com.khmelyuk.memory.metrics.MetricsSnapshotBuilder;
import com.khmelyuk.memory.metrics.TimeContext;
import com.khmelyuk.memory.vm.storage.Storage;
import com.khmelyuk.memory.vm.table.VirtualMemoryTable;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents the abstract virtual memory. This class contains a list of general implementations.
 *
 * @author Ruslan Khmelyuk
 */
public abstract class AbstractVirtualMemory<S extends Storage> implements VirtualMemory {

    protected S storage;
    protected VirtualMemoryTable table;
    protected int size;

    protected FreeEventListener freeEventListener;

    protected final Metrics metrics;

    protected AbstractVirtualMemory(S storage, VirtualMemoryTable table) {
        this.storage = storage;
        this.table = table;
        this.size = storage.size();

        this.metrics = new Metrics();
        metrics.addTimerMetric("vm.allocationTime");
        metrics.addTimerMetric("vm.freeTime");
    }

    public int size() {
        return size;
    }

    public int getMaxSize() {
        return size;
    }

    public int getFreeSize() {
        return table.getFreeMemorySize();
    }

    public int getUsedSize() {
        return table.getUsedMemorySize();
    }

    public void free() {
        storage.free();
        table.reset(0);
        size = 0;

        // call the FreeEventListener if any
        if (freeEventListener != null) {
            freeEventListener.onFree(this);
        }
    }

    public void free(VirtualMemoryBlock block) {
        TimeContext timer = metrics.getTimer("vm.freeTime");
        timer.start();
        table.free(block.getBlock());
        timer.stop();
    }

    public void setFreeEventListener(FreeEventListener listener) {
        this.freeEventListener = listener;
    }

    @Override
    public MetricsSnapshot getMetrics() {
        return new MetricsSnapshotBuilder().fromMetrics(metrics).merge(table.getMetrics()).build();
    }

    // ---------------------------------------------- Read/write support

    public InputStream getInputStream() {
        return new VMInputStream(this);
    }

    public InputStream getInputStream(int offset, int length) throws OutOfBoundException {
        if (offset + length > size()) {
            throw new OutOfBoundException();
        }

        return new VMInputStream(this, offset, length);
    }

    public OutputStream getOutputStream() {
        return new VMOutputStream(this);
    }

    public OutputStream getOutputStream(int offset, int length) throws OutOfBoundException {
        if (offset + length > size()) {
            throw new OutOfBoundException();
        }

        return new VMOutputStream(this, offset, length);
    }

    public void write(byte[] data) throws OutOfBoundException {
        this.storage.write(data);
    }

    public void write(byte[] data, int offset) throws OutOfBoundException {
        this.storage.write(data, offset);
    }

    public void write(byte[] data, int offset, int length) throws OutOfBoundException {
        this.storage.write(data, offset, length);
    }

    public int read(byte[] data) throws OutOfBoundException {
        return this.storage.read(data);
    }

    public int read(byte[] data, int offset, int length) {
        return this.storage.read(data, offset, length);
    }

    public void write(byte data, int offset) throws OutOfBoundException {
        this.storage.write(data, offset);
    }

    public byte read(int offset) {
        return this.storage.read(offset);
    }
}

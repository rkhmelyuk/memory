package com.khmelyuk.memory.space.transactional;

import com.khmelyuk.memory.MemoryException;
import com.khmelyuk.memory.space.MemorySpace;
import com.khmelyuk.memory.space.Space;
import com.khmelyuk.memory.vm.VirtualMemoryBlock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A transactional space.
 *
 * @author Ruslan Khmelyuk
 */
public class CopyTransactionalSpace implements TransactionalSpace {

    private static final int STATUS_NONE = 0;
    private static final int STATUS_STARTING = 1;
    private static final int STATUS_STARTED = 2;
    private static final int STATUS_ENDING = 3;

    private final MemorySpace space;
    private final AtomicInteger status;
    private Space tSpace;
    private Space currentSpace;

    public CopyTransactionalSpace(MemorySpace space) {
        this.space = space;
        this.tSpace = null;
        this.currentSpace = space;
        this.status = new AtomicInteger(STATUS_NONE);
    }

    @Override
    public int getAddress() {
        return currentSpace.getAddress();
    }

    @Override
    public int size() {
        return currentSpace.size();
    }

    @Override
    public VirtualMemoryBlock getBlock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void free() {
        if (tSpace != null) {
            tSpace.free();
        }
    }

    @Override
    public InputStream getInputStream() {
        return currentSpace.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return currentSpace.getOutputStream();
    }

    @Override
    public void write(Object object) {
        currentSpace.write(object);
    }

    @Override
    public void write(String string) {
        currentSpace.write(string);
    }

    @Override
    public Object read() {
        return currentSpace.read();
    }

    @Override
    public String readString() {
        return currentSpace.readString();
    }

    @Override
    public void write(byte[] data) {
        currentSpace.write(data);
    }

    @Override
    public void write(byte[] data, int spaceOffset, int length) {
        currentSpace.write(data, spaceOffset, length);
    }

    @Override
    public int read(byte[] buffer) {
        return currentSpace.read(buffer);
    }

    @Override
    public int read(byte[] buffer, int spaceOffset, int length) {
        return currentSpace.read(buffer, spaceOffset, length);
    }

    @Override
    public Space readOnly() {
        return space.readOnly();
    }

    @Override
    public void dump(OutputStream out) throws IOException {
        currentSpace.dump(out);
    }

    @Override
    public Space copy() throws MemoryException {
        return currentSpace.copy();
    }

    @Override
    public TransactionalSpace transactional() {
        return space.transactional();
    }

    @Override
    public void start() throws TransactionException {
        if (!status.compareAndSet(STATUS_NONE, STATUS_STARTING)) {
            throw new TransactionException("Space is in transaction already. Only one transaction is supported");
        }

        try {
            tSpace = space.copy();
            currentSpace = tSpace;
            status.set(STATUS_STARTED);
        } catch (Exception e) {
            if (tSpace != null) {
                tSpace.free();
                currentSpace = space;
                status.set(STATUS_NONE);
            }
            throw new TransactionException("Error to start a transaction", e);
        }
    }

    @Override
    public void commit() throws TransactionException {
        if (!status.compareAndSet(STATUS_STARTED, STATUS_ENDING)) {
            throw new TransactionException("Not in transaction currently.");
        }

        try {
            tSpace.dump(space.getOutputStream());
            tSpace.free();
            tSpace = null;
            currentSpace = space;
            status.set(STATUS_NONE);
        } catch (IOException e) {
            status.set(STATUS_STARTED);
            throw new TransactionException("Error to commit a transaction. Transaction is rolled back.", e);
        }
    }

    @Override
    public void rollback() throws TransactionException {
        if (!status.compareAndSet(STATUS_STARTED, STATUS_ENDING)) {
            throw new TransactionException("Not in transaction currently.");
        }

        currentSpace = space;
        tSpace.free();
        tSpace = null;

        status.set(STATUS_NONE);
    }
}

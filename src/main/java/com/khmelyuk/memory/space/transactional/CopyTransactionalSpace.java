package com.khmelyuk.memory.space.transactional;

import com.khmelyuk.memory.MemoryException;
import com.khmelyuk.memory.space.MemorySpace;
import com.khmelyuk.memory.space.Space;
import com.khmelyuk.memory.vm.VirtualMemoryBlock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A transactional space.
 *
 * @author Ruslan Khmelyuk
 */
public class CopyTransactionalSpace implements TransactionalSpace {

    private final MemorySpace space;
    private final AtomicBoolean inTransaction;
    private Space tSpace;
    private Space currentSpace;

    public CopyTransactionalSpace(MemorySpace space) {
        this.space = space;
        this.tSpace = null;
        this.currentSpace = space;
        this.inTransaction = new AtomicBoolean(false);
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

    public void start() throws TransactionException {
        try {
            if (!inTransaction.compareAndSet(false, true)) {
                throw new TransactionException("Space is in transaction already. Only one transaction is supported");
            }

            tSpace = space.copy();
            currentSpace = tSpace;
        }
        catch (TransactionException e) {
            throw e;
        }
        catch (Exception e) {
            if (tSpace != null) {
                tSpace.free();
                currentSpace = space;
            }
            throw new TransactionException("Error to start a transaction", e);
        }
    }

    public void commit() throws TransactionException {
        if (!inTransaction.get()) {
            throw new TransactionException("Not in transaction currently.");
        }

        try {
            tSpace.dump(space.getOutputStream());
            tSpace.free();
            tSpace = null;
            currentSpace = space;
            inTransaction.set(true);
        }
        catch (IOException e) {
            throw new TransactionException("Error to commit a transaction.", e);
        }
    }

    public void rollback() throws TransactionException {
        if (!inTransaction.get()) {
            throw new TransactionException("Not in transaction currently.");
        }

        tSpace.free();
        tSpace = null;
        currentSpace = space;
        inTransaction.set(false);
    }
}

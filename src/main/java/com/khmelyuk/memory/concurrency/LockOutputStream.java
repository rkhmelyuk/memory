package com.khmelyuk.memory.concurrency;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;

/**
 * The output stream that supports locking.
 *
 * @author Ruslan Khmelyuk
 */
public class LockOutputStream extends OutputStream {

    private final Lock lock;
    private final OutputStream out;

    public LockOutputStream(OutputStream out, Lock lock) {
        this.out = out;
        this.lock = lock;
    }

    @Override
    public void write(byte[] data) throws IOException {
        try {
            lock.lock();
            out.write(data);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void write(byte[] data, int off, int len) throws IOException {
        try {
            lock.lock();
            out.write(data, off, len);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void write(int data) throws IOException {
        try {
            lock.lock();
            out.write(data);
        }
        finally {
            lock.unlock();
        }
    }
}

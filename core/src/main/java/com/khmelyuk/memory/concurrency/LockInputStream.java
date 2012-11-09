package com.khmelyuk.memory.concurrency;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;

/**
 * The input stream that supports locking.
 *
 * @author Ruslan Khmelyuk
 */
public class LockInputStream extends InputStream {

    private final Lock lock;
    private final InputStream in;

    public LockInputStream(InputStream in, Lock lock) {
        this.in = in;
        this.lock = lock;
    }

    @Override
    public int read() throws IOException {
        try {
            lock.lock();
            return in.read();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        try {
            lock.lock();
            return in.read(b);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try {
            lock.lock();
            return in.read(b, off, len);
        } finally {
            lock.unlock();
        }
    }
}

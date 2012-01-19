package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.ReadException;
import com.khmelyuk.memory.WriteException;
import com.khmelyuk.memory.concurrency.LockInputStream;
import com.khmelyuk.memory.concurrency.LockOutputStream;
import com.khmelyuk.memory.vm.table.Block;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents a block of virtual memory.
 *
 * @author Ruslan Khmelyuk
 */
public class VirtualMemoryBlock {

    private static final int BUFFER_SIZE = 8192;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    private final VirtualMemory vm;
    private final Block block;

    public VirtualMemoryBlock(VirtualMemory vm, Block block) {
        this.vm = vm;
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public int getAddress() {
        return block.getAddress();
    }

    public int size() {
        return block.getSize();
    }

    public InputStream getInputStream() {
        InputStream in = vm.getInputStream(block.getAddress(), block.getSize());
        return new LockInputStream(in, readLock);
    }

    public OutputStream getOutputStream() {
        OutputStream out = vm.getOutputStream(block.getAddress(), block.getSize());
        return new LockOutputStream(out, writeLock);
    }

    public void write(byte[] data) throws OutOfBoundException {
        try {
            writeLock.lock();
            final int length = data.length;
            if (length > block.getSize()) {
                throw new OutOfBoundException();
            }
            vm.write(data, block.getAddress(), length);
        }
        finally {
            writeLock.unlock();
        }
    }

    public void write(byte[] data, int offset, int length) throws OutOfBoundException {
        try {
            writeLock.lock();

            if (data.length < length) {
                length = data.length;
            }
            if (offset + length > block.getSize()) {
                throw new OutOfBoundException();
            }
            vm.write(data, block.getAddress() + offset, length);
        }
        finally {
            writeLock.unlock();
        }
    }

    public int read(byte[] data) {
        try {
            readLock.lock();

            final int blockSize = block.getSize();
            int length = data.length;
            if (data.length > blockSize) {
                length = blockSize;
            }
            return vm.read(data, block.getAddress(), length);
        }
        finally {
            readLock.unlock();
        }
    }

    public int read(byte[] data, int offset, int length) {
        try {
            readLock.lock();

            return readWithoutLock(data, offset, length);
        }
        finally {
            readLock.unlock();
        }
    }

    public String readString() {
        return (String) readObject();
    }

    public void write(String string) {
        writeObject(string);
    }

    public void writeObject(Object obj) throws OutOfBoundException, WriteException {
        try {
            writeLock.lock();

            OutputStream out = vm.getOutputStream(block.getAddress(), block.getSize());
            new ObjectOutputStream(out).writeObject(obj);
        }
        catch (IOException e) {
            throw new WriteException("Error to read an object", e);
        }
        finally {
            writeLock.unlock();
        }
    }

    public Object readObject() throws OutOfBoundException, ReadException {
        try {
            readLock.lock();

            InputStream in = vm.getInputStream(block.getAddress(), block.getSize());
            try {
                return new ObjectInputStream(in).readObject();
            }
            catch (IOException e) {
                throw new ReadException("Error to read an object", e);
            }
            catch (ClassNotFoundException e) {
                throw new ReadException("Error to read an object", e);
            }
        }
        finally {
            readLock.unlock();
        }
    }

    public void dump(OutputStream out) throws IOException {
        try {
            readLock.lock();

            final int blockSize = block.getSize();
            final int bufferSize = blockSize < BUFFER_SIZE ? blockSize : BUFFER_SIZE;

            int length, offset = 0;
            final byte[] buffer = new byte[bufferSize];
            while ((length = readWithoutLock(buffer, offset, bufferSize)) != -1) {
                out.write(buffer, 0, length);
                offset += length;
            }
        }
        finally {
            readLock.unlock();
        }
    }

    private int readWithoutLock(byte[] data, int offset, int length) {
        if (data.length < length) {
            length = data.length;
        }
        if (offset + length > block.getSize()) {
            return -1;
        }
        return vm.read(data, block.getAddress() + offset, length);
    }

}

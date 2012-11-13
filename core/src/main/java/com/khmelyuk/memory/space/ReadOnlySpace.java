package com.khmelyuk.memory.space;

import com.khmelyuk.memory.MemoryException;
import com.khmelyuk.memory.space.transactional.TransactionalSpace;
import com.khmelyuk.memory.space.transactional.WriteNotAllowedException;
import com.khmelyuk.memory.vm.VirtualMemoryBlock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The read only wrapper for specified space.
 *
 * @author Ruslan Khmelyuk
 */
public final class ReadOnlySpace implements Space {

    private final Space space;

    public ReadOnlySpace(Space space) {
        this.space = space;
    }

    @Override
    public int getAddress() {
        return space.getAddress();
    }

    @Override
    public int size() {
        return space.size();
    }

    @Override
    public void free() {
        throw new WriteNotAllowedException();
    }

    @Override
    public InputStream getInputStream() {
        return space.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        throw new WriteNotAllowedException();
    }

    @Override
    public Object read() {
        return space.read();
    }

    @Override
    public String readString() {
        return space.readString();
    }

    @Override
    public void write(Object object) {
        throw new WriteNotAllowedException();
    }

    @Override
    public void write(String string) {
        throw new WriteNotAllowedException();
    }

    @Override
    public void write(byte[] data) {
        throw new WriteNotAllowedException();
    }

    @Override
    public void write(byte[] data, int spaceOffset, int length) {
        throw new WriteNotAllowedException();
    }

    @Override
    public int read(byte[] buffer) {
        return space.read(buffer);
    }

    @Override
    public int read(byte[] buffer, int spaceOffset, int length) {
        return space.read(buffer, spaceOffset, length);
    }

    @Override
    public Space readOnly() {
        return this;
    }

    @Override
    public VirtualMemoryBlock getBlock() {
        return space.getBlock();
    }

    @Override
    public void dump(OutputStream out) throws IOException {
        space.dump(out);
    }

    @Override
    public Space copy() throws MemoryException {
        return space.copy();
    }

    @Override
    public TransactionalSpace transactional() {
        return space.transactional();
    }
}

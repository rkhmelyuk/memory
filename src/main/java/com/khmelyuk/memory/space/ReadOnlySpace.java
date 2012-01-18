package com.khmelyuk.memory.space;

import com.khmelyuk.memory.vm.VirtualMemoryBlock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The read only wrapper for specified space.
 *
 * @author Ruslan Khmelyuk
 */
public class ReadOnlySpace implements Space {

    private final Space space;

    public ReadOnlySpace(Space space) {
        this.space = space;
    }

    public int getAddress() {
        return space.getAddress();
    }

    public int size() {
        return space.size();
    }

    public void free() {
        space.free();
    }

    @Override
    public InputStream getInputStream() {
        return space.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        throw new WriteNotAllowedException();
    }

    public Object read() {
        return space.read();
    }

    public String readString() {
        return space.readString();
    }

    public void write(Object object) {
        throw new WriteNotAllowedException();
    }

    public void write(String string) {
        throw new WriteNotAllowedException();
    }

    public Space readOnly() {
        return this;
    }

    public VirtualMemoryBlock getBlock() {
        return space.getBlock();
    }

    public void dump(OutputStream out) throws IOException {
        space.dump(out);
    }

    public TransactionalSpace transactional() {
        return space.transactional();
    }
}

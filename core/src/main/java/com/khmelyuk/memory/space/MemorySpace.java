package com.khmelyuk.memory.space;

import com.khmelyuk.memory.Memory;
import com.khmelyuk.memory.MemoryException;
import com.khmelyuk.memory.space.transactional.CopyTransactionalSpace;
import com.khmelyuk.memory.space.transactional.TransactionalSpace;
import com.khmelyuk.memory.vm.VirtualMemoryBlock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a space in memory, some part of memory.
 *
 * @author Ruslan Khmelyuk
 */
public final class MemorySpace implements Space {

    private final Memory memory;
    private final VirtualMemoryBlock block;
    private final FreeSpaceListener freeSpaceListener;

    public MemorySpace(Memory memory, VirtualMemoryBlock block, FreeSpaceListener freeSpaceListener) {
        this.block = block;
        this.memory = memory;
        this.freeSpaceListener = freeSpaceListener;
    }

    @Override
    public int getAddress() {
        return block.getAddress();
    }

    @Override
    public int size() {
        return block.size();
    }

    @Override
    public void free() {
        if (freeSpaceListener != null) {
            freeSpaceListener.onFreeSpace(this);
        }
    }

    @Override
    public InputStream getInputStream() {
        return block.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return block.getOutputStream();
    }

    @Override
    public void write(Object object) {
        block.writeObject(object);
    }

    @Override
    public void write(String string) {
        block.write(string);
    }

    @Override
    public Object read() {
        return block.readObject();
    }

    @Override
    public String readString() {
        return block.readString();
    }

    @Override
    public void write(byte[] data) {
        block.write(data);
    }

    @Override
    public void write(byte[] data, int spaceOffset, int length) {
        block.write(data, spaceOffset, length);
    }

    @Override
    public int read(byte[] buffer) {
        return block.read(buffer);
    }

    @Override
    public int read(byte[] buffer, int spaceOffset, int length) {
        return block.read(buffer, spaceOffset, length);
    }

    @Override
    public Space readOnly() {
        return new ReadOnlySpace(this);
    }

    @Override
    public VirtualMemoryBlock getBlock() {
        return block;
    }

    @Override
    public void dump(OutputStream out) throws IOException {
        block.dump(out);
    }

    @Override
    public TransactionalSpace transactional() {
        return new CopyTransactionalSpace(this);
    }

    @Override
    public Space copy() {
        MemorySpace space = null;
        try {
            space = memory.allocate(size());
            dump(space.getOutputStream());
            return space;
        } catch (IOException e) {
            // failed to copy, so free a space
            space.free();

            // throw appropriate exception
            throw new MemoryException("Error to copy a memory space.");
        }
    }
}

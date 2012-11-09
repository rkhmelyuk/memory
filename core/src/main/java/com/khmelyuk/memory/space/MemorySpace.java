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
public class MemorySpace implements Space {

    private Memory memory;
    private VirtualMemoryBlock block;
    private FreeSpaceListener freeSpaceListener;

    public MemorySpace(Memory memory, VirtualMemoryBlock block) {
        this.memory = memory;
        this.block = block;
    }

    public void setFreeSpaceListener(FreeSpaceListener freeSpaceListener) {
        this.freeSpaceListener = freeSpaceListener;
    }

    public int getAddress() {
        return block.getAddress();
    }

    public int size() {
        return block.size();
    }

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

    public void write(Object object) {
        block.writeObject(object);
    }

    public void write(String string) {
        block.write(string);
    }

    public Object read() {
        return block.readObject();
    }

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

    public Space readOnly() {
        return new ReadOnlySpace(this);
    }

    public VirtualMemoryBlock getBlock() {
        return block;
    }

    public void dump(OutputStream out) throws IOException {
        block.dump(out);
    }

    public TransactionalSpace transactional() {
        return new CopyTransactionalSpace(this);
    }

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

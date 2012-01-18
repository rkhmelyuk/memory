package com.khmelyuk.memory.space;

import com.khmelyuk.memory.Memory;
import com.khmelyuk.memory.MemoryException;
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
        return new TransactionalSpace(this);
    }

    MemorySpace copy() {
        try {
            MemorySpace space = memory.allocate(size());
            dump(space.getOutputStream());
            return space;
        }
        catch (IOException e) {
            throw new MemoryException("Error to copy a memory space.");
        }
    }
}

package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.ReadException;
import com.khmelyuk.memory.WriteException;
import com.khmelyuk.memory.vm.table.Block;

import java.io.*;

/**
 * Represents a block of virtual memory.
 *
 * @author Ruslan Khmelyuk
 */
public class VirtualMemoryBlock {

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
        return vm.getInputStream(block.getAddress(), block.getSize());
    }

    public OutputStream getOutputStream() {
        return vm.getOutputStream(block.getAddress(), block.getSize());
    }

    public void write(byte[] data) throws OutOfBoundException {
        if (data.length > block.getSize()) {
            throw new OutOfBoundException();
        }
        vm.write(data, block.getAddress(), block.getSize());
    }

    public void write(byte[] data, int offset, int length) throws OutOfBoundException {
        if (offset + length > block.getSize()) {
            throw new OutOfBoundException();
        }
        vm.write(data, block.getAddress() + offset, length);
    }

    public int read(byte[] data) {
        return vm.read(data, block.getAddress(), block.getSize());
    }

    public int read(byte[] data, int offset, int length) {
        return vm.read(data, block.getAddress() + offset, length);
    }

    public String readString() {
        return (String) readObject();
    }

    public void write(String string) {
        writeObject(string);
    }

    public void writeObject(Object obj) throws OutOfBoundException, WriteException {
        try {
            OutputStream out = vm.getOutputStream(block.getAddress(), block.getSize());
            new ObjectOutputStream(out).writeObject(obj);
        }
        catch (IOException e) {
            throw new WriteException("Error to read an object", e);
        }
    }

    public Object readObject() throws OutOfBoundException, ReadException {
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
}

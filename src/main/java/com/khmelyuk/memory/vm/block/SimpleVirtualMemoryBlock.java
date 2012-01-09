package com.khmelyuk.memory.vm.block;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.ReadException;
import com.khmelyuk.memory.WriteException;
import com.khmelyuk.memory.vm.VirtualMemory;

import java.io.*;

/**
 * Represents a block of virtual memory.
 *
 * @author Ruslan Khmelyuk
 */
public class SimpleVirtualMemoryBlock implements VirtualMemoryBlock {

    private final VirtualMemory vm;
    private final int address;
    private final int length;

    public SimpleVirtualMemoryBlock(VirtualMemory vm, int address, int length) {
        this.vm = vm;
        this.address = address;
        this.length = length;
    }

    public int getAddress() {
        return address;
    }

    public int length() {
        return length;
    }

    public InputStream getInputStream() {
        return vm.getInputStream(address, length);
    }

    public OutputStream getOutputStream() {
        return vm.getOutputStream(address, length);
    }

    public void write(byte[] data) throws OutOfBoundException {
        if (data.length > length) {
            throw new OutOfBoundException();
        }
        vm.write(data, address, length);
    }

    public void write(byte[] data, int offset, int length) throws OutOfBoundException {
        if (offset + length > this.length) {
            throw new OutOfBoundException();
        }
        vm.write(data, address + offset, length);
    }

    public int read(byte[] data) {
        return vm.read(data, address, length);
    }

    public int read(byte[] data, int offset, int length) {
        return vm.read(data, address + offset, length);
    }

    public String readString() {
        return (String) readObject();
    }

    public void write(String string) {
        writeObject(string);
    }

    public void writeObject(Object obj) {
        try {
            OutputStream out = vm.getOutputStream(address, length);
            new ObjectOutputStream(out).writeObject(obj);
        }
        catch (IOException e) {
            throw new WriteException("Error to read an object", e);
        }
    }

    public Object readObject() {
        InputStream in = vm.getInputStream(address, length);
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

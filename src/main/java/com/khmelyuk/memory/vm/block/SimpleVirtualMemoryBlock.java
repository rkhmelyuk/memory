package com.khmelyuk.memory.vm.block;

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

    public void write(byte[] data) {
        // TODO - limit check
        vm.write(data, address, length);
    }

    public void write(byte[] data, int offset, int length) {
        // TODO - limit check
        vm.write(data, address + offset, length);
    }

    public int read(byte[] data) {
        // TODO - limit check
        return vm.read(data, address, length);
    }

    public int read(byte[] data, int offset, int length) {
        // TODO - limit check
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
            // TODO =- log error
            e.printStackTrace();
        }
    }

    public Object readObject() {
        InputStream in = vm.getInputStream(address, length);
        try {
            return new ObjectInputStream(in).readObject();
        }
        catch (IOException e) {
            // TODO - handle error
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            // TODO - handle error
            e.printStackTrace();
        }

        return null;
    }
}

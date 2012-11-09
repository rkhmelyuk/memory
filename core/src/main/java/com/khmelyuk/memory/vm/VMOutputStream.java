package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an output stream for the Virtual Memory.
 *
 * @author Ruslan Khmelyuk
 */
public class VMOutputStream extends OutputStream {

    private final VirtualMemory vm;
    private final int offset;
    private final int length;

    private int localOffset;

    public VMOutputStream(VirtualMemory vm) {
        this.vm = vm;
        this.offset = 0;
        this.length = vm.size();
    }

    public VMOutputStream(VirtualMemory vm, int offset, int length) {
        this.vm = vm;
        this.offset = offset;
        this.length = length;

        if (offset + length > vm.size()) {
            throw new OutOfBoundException();
        }
    }

    /**
     * Writes the byte to the virtual memory.
     *
     * @param b the byte to write.
     * @throws IOException error to write a byte or output stream is ended.
     */
    @Override
    public void write(int b) throws IOException {
        if (localOffset + 1 > length) {
            throw new IOException("Stream is ended.");
        }
        vm.write((byte) b, offset + localOffset++);
    }

}

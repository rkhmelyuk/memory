package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an input stream for the Virtual Memory.
 *
 * @author Ruslan Khmelyuk
 */
public class VMInputStream extends InputStream {

    private final VirtualMemory vm;
    private final int offset;
    private final int length;

    private int localOffset;

    public VMInputStream(VirtualMemory vm) {
        this.vm = vm;
        this.offset = 0;
        this.length = vm.length();
    }

    public VMInputStream(VirtualMemory vm, int offset, int length) {
        this.vm = vm;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public int read() {
        if (localOffset + 1 > length) {
            return -1;
        }
        return vm.read(offset + localOffset++);
    }

    /**
     * Always resets to the begin of the input stream.
     */
    @Override
    public void reset() {
        localOffset = 0;
    }

    @Override
    public long skip(long n) {
        long realOffset = localOffset + n;
        if (realOffset > length) {
            int result = length - localOffset;
            localOffset = length;
            return result;
        }

        localOffset = (int) realOffset;
        return n;
    }

    @Override
    public int available() throws IOException {
        return length - localOffset;
    }
}

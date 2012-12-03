package com.khmelyuk.memory.vm;

import com.khmelyuk.memory.OutOfBoundException;
import com.khmelyuk.memory.OutOfMemoryException;
import com.khmelyuk.memory.metrics.Monitorable;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a virtual memory.
 *
 * @author Ruslan Khmelyuk
 */
public interface VirtualMemory extends Monitorable {

    /**
     * Gets the virtual memory size.
     *
     * @return the virtual memory size.
     */
    int size();

    /**
     * Gets the max size for virtual memory.
     *
     * @return the virtual memory max size.
     */
    int getMaxSize();

    /**
     * Get how many bytes is free for use.
     *
     * @return the free size of memory in bytes.
     */
    int getFreeSize();

    /**
     * Get how many bytes is used.
     *
     * @return the size of used memory in bytes.
     */
    int getUsedSize();

    /**
     * Allocates a block in the virtual memory.
     *
     * @param length the block size.
     * @return the allocated virtual memory block.
     * @throws OutOfMemoryException failed to allocate the block of need size.
     */
    VirtualMemoryBlock allocate(int length) throws OutOfMemoryException;

    /**
     * Frees the entire virtual memory.
     */
    void free();

    /**
     * Frees a block of the virtual memory.
     *
     * @param block the block to free.
     */
    void free(VirtualMemoryBlock block);

    /**
     * Sets the free memory listener.
     *
     * @param listener the listener to set.
     */
    void setFreeEventListener(FreeEventListener listener);

    // -------------------------------------------------

    /**
     * Gets the virtual memory as input stream.
     * Returned InputStream reads directly from the virtual memory.
     *
     * @return the virtual memory as input stream.
     */
    InputStream getInputStream();

    /**
     * Gets the virtual memory part within specified offset and size as input stream.
     * Returned InputStream reads directly from the virtual memory.
     *
     * @param offset the input stream offset.
     * @param length the input stream size.
     * @return the virtual memory part as input stream.
     * @throws OutOfBoundException error to access the memory out of bound.
     */
    InputStream getInputStream(int offset, int length) throws OutOfBoundException;

    /**
     * Gets the virtual memory as output stream.
     * Returned OutputStream writes directly to the virtual memory.
     *
     * @return the virtual memory as output stream.
     */
    OutputStream getOutputStream();

    /**
     * Gets the virtual memory part as output stream.
     * Returned OutputStream writes directly to the virtual memory.
     *
     * @param offset the input stream offset.
     * @param length the input stream size.
     * @return the virtual memory part as output stream.
     * @throws OutOfBoundException error to access the memory out of bound.
     */
    OutputStream getOutputStream(int offset, int length) throws OutOfBoundException;

    /**
     * Writes the data as byte array directly to the virtual memory.
     *
     * @param data the byte array to write to the virtual memory.
     * @throws OutOfBoundException error to access the memory out of bound.
     */
    void write(byte[] data) throws OutOfBoundException;

    /**
     * Writes the data as byte array directly to the virtual memory from specified offset.
     *
     * @param data   the byte array to write to the virtual memory.
     * @param offset the offset to write from.
     * @throws OutOfBoundException error to access the memory out of bound.
     */
    void write(byte[] data, int offset) throws OutOfBoundException;

    /**
     * Writes the byte value directly to the virtual memory on specified offset.
     *
     * @param data   the byte to write to the virtual memory.
     * @param offset the offset to write from.
     * @throws OutOfBoundException error to access the memory out of bound.
     */
    void write(byte data, int offset) throws OutOfBoundException;

    /**
     * Writes the byte array directly to the virtual memory
     * started from specified offset and with specified size.
     *
     * @param data   the byte array to write to the virtual memory.
     * @param offset the offset to write from.
     * @param length the size of the data to write to.
     * @throws OutOfBoundException error to access the memory out of bound.
     */
    void write(byte[] data, int offset, int length) throws OutOfBoundException;

    /**
     * Read data from virtual memory into byte array and returns the number of read bytes.
     *
     * @param data the buffer to read data from VM to.
     * @return the number of read bytes.
     */
    int read(byte[] data);

    /**
     * Read a single byte by specified offset.
     *
     * @param offset the offset.
     * @return the read byte.
     */
    byte read(int offset);

    /**
     * Read data from virtual memory into byte array from specified offset and with specified size.
     *
     * @param data   the buffer to read data from VM to.
     * @param offset the offset of the
     * @param length how many bytes to read into buffer.
     * @return the number of read bytes.
     */
    int read(byte[] data, int offset, int length);
}

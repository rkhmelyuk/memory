package com.khmelyuk.memory.vm.storage;

import com.khmelyuk.memory.OutOfBoundException;

/**
 * The interface for the storage. Framework should support different types of storage, like byte array or ByteBuffers.
 *
 * @author Ruslan Khmelyuk
 */
public interface Storage {

    /**
     * Gets a storage size.
     *
     * @return a storage size.
     */
    int size();

    /**
     * Free the storage memory.
     */
    void free();

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
     * Writes the byte array directly to the virtual memory
     * started from specified offset and with specified size.
     *
     * @param data       the byte array to write to the virtual memory.
     * @param offset     the offset to write to.
     * @param length     the size of the data to write to.
     * @param dataOffset the of data to read from.
     * @throws OutOfBoundException error to access the memory out of bound.
     */
    void write(byte[] data, int offset, int dataOffset, int length) throws OutOfBoundException;

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

    /**
     * Read data from virtual memory into byte array from specified offset and with specified size.
     *
     * @param data       the buffer to read data from VM to.
     * @param offset     the offset of the storage.
     * @param dataOffset the offset of the data.
     * @param length how many bytes to read into buffer.
     * @return the number of read bytes.
     */
    int read(byte[] data, int offset, int dataOffset, int length);
}

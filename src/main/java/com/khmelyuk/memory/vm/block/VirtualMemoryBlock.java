package com.khmelyuk.memory.vm.block;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a virtual memory block.
 * Usually block is a window to some part of the memory.
 * Once user writes to the block, it's written to the virtual memory. The same is with reads.
 *
 * @author Ruslan Khmelyuk
 */
public interface VirtualMemoryBlock {

    /**
     * Gets the address of this block.
     *
     * @return the block address.
     */
    int getAddress();

    /**
     * Gets the length of this block.
     *
     * @return the block length.
     */
    int length();

    /**
     * Gets the block content as input stream.
     * The returned stream read directly from the block;
     *
     * @return the input stream of the content.
     */
    InputStream getInputStream();

    /**
     * Gets the block as output stream to write directly to the block.
     *
     * @return the output stream for the content.
     */
    OutputStream getOutputStream();

    /**
     * Write array of bytes to the block.
     *
     * @param data the array to write to the block.
     */
    void write(byte[] data);

    /**
     * Read the data from block into the array.
     *
     * @param data the buffer to read data into.
     * @return the number of read bytes.
     */
    int read(byte[] data);

    /**
     * Writes the byte array directly to the virtual memory block
     * starting from specified offset and with specified length.
     *
     * @param data   the byte array to write to the virtual memory block.
     * @param offset the offset to write from.
     * @param length the length of the data to write to.
     */
    void write(byte[] data, int offset, int length);

    /**
     * Read data from virtual memory block into byte array from specified offset and with specified length.
     *
     * @param data   the buffer to read data from VM to.
     * @param offset the offset of the
     * @param length how many bytes to read into buffer.
     * @return the number of read bytes.
     */
    int read(byte[] data, int offset, int length);

    /**
     * Write the string to the block.
     *
     * @param string the string to write to the block.
     */
    void write(String string);

    /**
     * Read the string from the block.
     *
     * @return the read string, if not found than null.
     */
    String readString();

    /**
     * Write the object to the block.
     * @param object the object to write to the block.
     */
    void writeObject(Object object);

    /**
     * Read the object from the block.
     * @return the read object, if not found than null.
     */
    Object readObject();
}

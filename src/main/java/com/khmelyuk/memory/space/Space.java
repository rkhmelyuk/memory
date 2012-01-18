package com.khmelyuk.memory.space;

import com.khmelyuk.memory.vm.VirtualMemoryBlock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a space in the memory, some part of memory.
 *
 * @author Ruslan Khmelyuk
 */
public interface Space {

    /**
     * Gets the space address.
     *
     * @return the space address.
     */
    int getAddress();

    /**
     * Gets the space size.
     *
     * @return the space size.
     */
    int size();

    /**
     * Gets the virtual memory block.
     *
     * @return the virtual memory block.
     */
    VirtualMemoryBlock getBlock();

    /**
     * Frees this space.
     */
    void free();

    /**
     * Gets the space input stream.
     * @return the input stream.
     */
    InputStream getInputStream();

    /**
     * Gets the space output stream.
     * @return the output stream.
     */
    OutputStream getOutputStream();

    /**
     * Write the object to the space.
     *
     * @param object the object to write to the space.
     */
    void write(Object object);

    /**
     * Write the string to the space.
     *
     * @param string the string to write to the space.
     */
    void write(String string);

    /**
     * Reads the object from the space.
     *
     * @return the object from the space or null.
     */
    Object read();

    /**
     * Reads the string from the space.
     *
     * @return the string from space or null.
     */
    String readString();

    /**
     * Returns the read only version of this space.
     *
     * @return the read only version of this space.
     */
    Space readOnly();

    /**
     * Dump the content of the space to the output stream.
     *
     * @param out the output stream to write the space content to.
     * @throws IOException error to dump content.
     */
    void dump(OutputStream out) throws IOException;

    /**
     * Gets the space that supports a transaction.
     * @return the transactional space.
     */
    TransactionalSpace transactional();

}

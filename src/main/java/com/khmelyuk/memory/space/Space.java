package com.khmelyuk.memory.space;

/**
 * Represents a space in the memory, some part of memory.
 *
 * @author Ruslan Khmelyuk
 */
public interface Space {

    /**
     * Gets the space address.
     * @return the space address.
     */
    int getAddress();

    /**
     * Gets the space size.
     * @return the space size.
     */
    int size();

    /**
     * Frees this space.
     */
    void free();

    /**
     * Write the object to the space.
     * @param object the object to write to the space.
     */
    void write(Object object);

    /**
     * Write the string to the space.
     * @param string the string to write to the space.
     */
    void write(String string);

    /**
     * Reads the object from the space.
     * @return the read object from the space or null.
     */
    Object read();

    String readString();

    Space readOnly();
}

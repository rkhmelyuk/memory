package com.khmelyuk.memory;

/**
 * Represents a space in the memory, some part of memory.
 *
 * @author Ruslan Khmelyuk
 */
public interface Space {

    int getAddress();

    int size();

    void free();

    void write(Object object);

    void write(String string);

    Object read();

    String readString();

    Space readOnly();
}

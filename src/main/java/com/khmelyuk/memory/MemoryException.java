package com.khmelyuk.memory;

/**
 * Represents a base memory exception.
 *
 * @author Ruslan Khmelyuk
 */
public class MemoryException extends RuntimeException {

    public MemoryException() {
    }

    public MemoryException(String message) {
        super(message);
    }

    public MemoryException(String message, Throwable cause) {
        super(message, cause);
    }
}

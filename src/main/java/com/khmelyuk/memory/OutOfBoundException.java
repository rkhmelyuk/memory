package com.khmelyuk.memory;

/**
 * Error to access memory out of restricted bound.
 *
 * @author Ruslan Khmelyuk
 */
public class OutOfBoundException extends MemoryException {

    public OutOfBoundException() {
    }

    public OutOfBoundException(String message) {
        super(message);
    }

    public OutOfBoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

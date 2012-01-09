package com.khmelyuk.memory;

/**
 * Error to write data.
 *
 * @author Ruslan Khmelyuk
 */
public class WriteException extends MemoryException {

    public WriteException() {
    }

    public WriteException(String message, Throwable cause) {
        super(message, cause);
    }
}

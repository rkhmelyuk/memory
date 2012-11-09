package com.khmelyuk.memory;

/**
 * Error to read data.
 *
 * @author Ruslan Khmelyuk
 */
public class ReadException extends MemoryException {

    public ReadException() {
    }

    public ReadException(String message, Throwable cause) {
        super(message, cause);
    }
}

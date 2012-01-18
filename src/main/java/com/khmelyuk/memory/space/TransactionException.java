package com.khmelyuk.memory.space;

/**
 * Error to start or commit a transaction.
 *
 * @author Ruslan Khmelyuk
 */
public class TransactionException extends Exception {

    public TransactionException() {
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

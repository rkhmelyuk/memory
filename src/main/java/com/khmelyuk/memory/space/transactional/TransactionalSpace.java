package com.khmelyuk.memory.space.transactional;

import com.khmelyuk.memory.space.Space;

/**
 * A space that supports transactions.
 *
 * @author Ruslan Khmelyuk
 */
public interface TransactionalSpace extends Space {

    /**
     * Starts a new transaction.
     *
     * @throws TransactionException error to start a transaction.
     */
    void start() throws TransactionException;

    /**
     * Commits current transaction if any.
     *
     * @throws TransactionException error if transaction can't be committed or space is not in transaction.
     */
    void commit() throws TransactionException;

    /**
     * Rollbacks current transaction if any.
     *
     * @throws TransactionException error if space in not in transaction.
     */
    void rollback() throws TransactionException;
}


package com.asteroid.duck.jena.util.txn;

/**
 * An exception thrown by {@link JenaTransaction} if the outer transaction
 * has ended and methods of the "child" transactions are invoked.
 *
 * This can only occur if a single thread ends an outer transaction before the inner ones.
 *
 * Any data or state from the inner transactions will be lost!
 *
 * It is a runtime exception since it is a (typically) irrecoverable programming error.
 */
public class JenaTransactionEndedException extends RuntimeException {
}

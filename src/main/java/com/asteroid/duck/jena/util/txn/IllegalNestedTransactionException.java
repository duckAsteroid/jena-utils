
package com.asteroid.duck.jena.util.txn;

/**
 * Thrown to indicate that a {@link org.apache.jena.query.ReadWrite#WRITE} transaction has been attempted inside
 * an existing {@link org.apache.jena.query.ReadWrite#READ}. This is not permitted.
 */
public class IllegalNestedTransactionException extends RuntimeException {
}

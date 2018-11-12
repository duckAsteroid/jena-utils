
package com.asteroid.duck.jena.util.txn;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.JenaTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A re-entrant wrapper around Apache Jena's Transaction capability.
 * instances are {@link AutoCloseable} so you can use try-with constructs.
 */
public abstract class JenaTransaction implements AutoCloseable {
    /** Logging */
    private static final Logger LOG = LoggerFactory.getLogger(JenaTransaction.class);
    /**
     * Each thread gets a single concrete transaction, all others are simple
     * wrappers
     */
    private static ThreadLocal<Concrete> localHelper = new ThreadLocal<>();

    /**
     * A helper method to run a {@link Callable} inside a {@link ReadWrite#READ} transaction. This method takes care of {@link #begin(Dataset, ReadWrite)}
     * and {@link #end()}. The rest is up to you...
     * @param function the function to run inside a {@link ReadWrite#READ} transaction
     * @param d the dataset for the transaction
     * @param <T> the type returned by this method
     * @return whatever is returned by the {@link Callable} function
     */
    public static <T> T readWith(final Supplier<T> function, final Dataset d) {
        return runWith(jenaTransaction -> function.get(), d, ReadWrite.READ);
    }

    /**
     * A helper method to run inside a {@link Callable} inside a {@link ReadWrite#WRITE} transaction. This method takes care of {@link #begin(Dataset, ReadWrite)}
     * and {@link #end()}.
     *
     * If you ask it, it will also {@link #commit()} the transaction when you are done (before it
     * ends).
     *
     * NOTE: Any exceptions in the function will be swallowed and logged
     *
     * @param function the function to run inside a {@link ReadWrite#WRITE} transaction
     * @param d the dataset for the transaction
     * @param commitAfter should {@link #commit()} be called after the function has successfully run
     * @param <T> the type returned by this method
     * @return whatever is returned by the {@link Callable} function (if it ran succesfully)
     */
    public static <T> T writeWith(final Supplier<T> function, final Dataset d, final boolean commitAfter) {
        return runWith(txn -> {
                T result = function.get();
                if (commitAfter) {
                    txn.commit();
                }
                return result;
        }, d, ReadWrite.WRITE);
    }

    /**
     * A helper method to run inside a {@link Callable} inside a {@link ReadWrite#WRITE} transaction. This method takes
     * care of {@link #begin(Dataset, ReadWrite)}, {@link #commit()} after your function completes (normally)
     * and finally {@link #end()} (even if the function exits abnormally with an exception).
     *
     * @param function the function to run inside a {@link ReadWrite#WRITE} transaction
     * @param d the dataset for the transaction
     * @param <T> the type returned by this method
     * @return whatever is returned by the {@link Callable} function (if there is no error)
     */
    public static <T> T writeWithCommit(final Supplier<T> function, final Dataset d)  {
        return writeWith(function, d, true);
    }

    /**
     * A helper method to run a {@link Function} function inside a transaction.
     * This method takes care of {@link #begin(Dataset, ReadWrite)}
     * and {@link #end()}. The rest is up to you...
     *
     * This variant will pass the resulting {@link JenaTransaction} instance to the function (e.g. to do
     * {@link #commit()}s)
     *
     * @param function the function to run inside transaction
     * @param d the dataset for the transaction
     * @param mode the transaction mode
     * @param <T> the type returned by this method
     * @return whatever is returned by the {@link Function} function
     */
    public static <T> T runWith(final Function<JenaTransaction, T> function, final Dataset d, final ReadWrite mode) {
        if (function != null) {
            JenaTransaction txn = JenaTransaction.begin(d, mode);
            try {
                return function.apply(txn);
            } finally {
                try {
                    txn.end();
                }
                catch(JenaTransactionException e) {
                    LOG.warn("Unable to end transaction", e);
                }
            }
        }
        return null;
    }

    /**
     * Called to begin a transaction in the current context. There can be only one concrete transaction per thread
     * regardless of the dataset.
     *
     * {@link JenaTransaction}s are re-entrant (unlike real Jena Transactions) i.e. they can be nested.
     *
     * @param dataset The dataset for the transaction
     * @param mode The data access mode of the transaction
     * @return a {@link JenaTransaction} instance for the client to {@link #commit()}, {@link #abort()} or {@link #end()} the transaction
     */
    public static JenaTransaction begin(Dataset dataset, ReadWrite mode) {
        if (localHelper.get() == null) {
            Concrete concrete = new Concrete(dataset, mode);
            localHelper.set(concrete);
            return concrete;
        }
        else {
            Concrete concrete = localHelper.get();
            // we are trying to enter a new mode - and that new mode is write then fail
            if (concrete.getMode() != mode && mode == ReadWrite.WRITE) {
                throw new IllegalNestedTransactionException();
            }
            return new Dummy();
        }
    }

    /**
     * Called by various methods to validate an outer transaction is still underway.
     * @throws JenaTransactionEndedException If the outer transaction has already terminated
     */
    protected Concrete validateOuterTransaction() throws JenaTransactionEndedException {
        Concrete concrete = localHelper.get();
        if (concrete == null){
            throw new JenaTransactionEndedException();
        }
        return concrete;
    }

    /**
     * Commit data to the transaction if this is the outermost, otherwise ignored
     */
    public abstract void commit();

    /**
     * Aborts the transaction immediately via the Jena dataset (via it's transaction) - regardless of the level of nesting
     * @throws JenaTransactionEndedException If the outermost Jena transaction is already {@link Dataset#end()}ed.
     */
    public void abort() {
        Concrete concrete = validateOuterTransaction();
        concrete.dataset.abort();
    }


    /**
     * {@link AutoCloseable#close()} - simply calls {@link #end()}
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        end();
    }

    /**
     * Ends the transaction. If this is the outermost transaction then the Jena transaction is {@link Dataset#end()}ed.
     */
    public abstract void end();

    /**
     * This is a real concrete transaction - it actually calls stuff in Jena
     */
    public static class Concrete extends JenaTransaction {
        private final Dataset dataset;
        private final ReadWrite mode;

        public Concrete(Dataset dataset, ReadWrite mode) {
            this.dataset = dataset;
            this.mode = mode;

            dataset.begin(mode);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Begin CONCRETE " + mode + ":" + Thread.currentThread().getName());
            }
        }

        @Override
        public void end() {
            dataset.end();
            localHelper.set(null);
            if (LOG.isDebugEnabled()) {
                LOG.debug("End CONCRETE " + mode +":" + Thread.currentThread().getName());
            }
        }

        public ReadWrite getMode() {
            return mode;
        }

        @Override
        public void commit() {
            dataset.commit();
        }
    }

    /**
     * This is a dummy transaction - all but the first transaction per thread are dummies
     */
    public static class Dummy extends JenaTransaction {
        public Dummy() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Begin nested " + Thread.currentThread().getName());
            }
        }

        @Override
        public void end() {
            validateOuterTransaction();
            if (LOG.isDebugEnabled()) {
                LOG.debug("End nested " + Thread.currentThread().getName());
            }
        }

        @Override
        public void commit() {
            LOG.trace("No commit");
        }
    }
}

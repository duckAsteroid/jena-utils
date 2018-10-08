
package com.asteroid.duck.jena.util.txn;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

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
     * An interface (Lambda) that can be used to access the transaction for commits etc.
     * @param <V> The type returned
     */
    public interface WithTransaction<V> {
        /**
         * A function called within a transaction
         * @param txn the transaction
         * @return some value to calling code
         * @throws Exception If anything bad happens
         */
        V call(JenaTransaction txn) throws Exception;
    }

    /**
     * A helper method to run a {@link Callable} inside a transaction. This method takes care of {@link #begin(Dataset, ReadWrite)}
     * and {@link #end()}. The rest is up to you...
     * @param function the function to run inside transaction
     * @param d the dataset for the transaction
     * @param mode the transaction mode
     * @param <T> the type returned by this method
     * @return whatever is returned by the {@link Callable} function
     * @throws Exception If there is a problem running the function. Note: the transaction will always be {@link #end()}ed!
     */
    public static <T> T runWith(final Callable<T> function, final Dataset d, final ReadWrite mode) throws Exception {
        if (function != null) {
            JenaTransaction txn = JenaTransaction.begin(d, mode);
            try {
                return function.call();
            } finally {
                txn.end();
            }
        }
        return null;
    }

    /**
     * A helper method to run a {@link WithTransaction} function inside a transaction.
     * This method takes care of {@link #begin(Dataset, ReadWrite)}
     * and {@link #end()}. The rest is up to you...
     *
     * This variant will pass the resulting {@link JenaTransaction} instance to the function (e.g. to {@link #commit()})
     * @param function the function to run inside transaction
     * @param d the dataset for the transaction
     * @param mode the transaction mode
     * @param <T> the type returned by this method
     * @return whatever is returned by the {@link Callable} function
     * @throws Exception If there is a problem running the function. Note: the transaction will always be {@link #end()}ed!
     */
    public static <T> T runWith(final WithTransaction<T> function, final Dataset d, final ReadWrite mode) throws Exception {
        if (function != null) {
            JenaTransaction txn = JenaTransaction.begin(d, mode);
            try {
                return function.call(txn);
            } finally {
                txn.end();
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
     * Commits immediatly to the Jena dataset (via it's transaction) - regardless of the level of nesting
     * @throws JenaTransactionEndedException If the outermost Jena transaction is already {@link Dataset#end()}ed.
     */
    public void commit() {
        Concrete concrete = validateOuterTransaction();
        concrete.dataset.commit();
    }

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
    }
}

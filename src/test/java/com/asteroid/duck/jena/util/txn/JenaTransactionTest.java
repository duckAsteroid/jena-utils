package com.asteroid.duck.jena.util.txn;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

import java.util.Optional;

import static org.junit.Assert.*;

public class JenaTransactionTest {


    /**
     * Direct use of the begin/end API on JenaTransaction in a nested way
     */
    @Test
    public void normal()  {
        Dataset dataset = Mockito.mock(Dataset.class);

        JenaTransaction outermost = JenaTransaction.begin(dataset, ReadWrite.READ);
        try {
            JenaTransaction okInner = JenaTransaction.begin(dataset, ReadWrite.READ);
            okInner.end();
        } finally {
            outermost.end();
        }

        // begin & end should only be called once...
        Mockito.verify(dataset, once()).begin(ReadWrite.READ);
        Mockito.verify(dataset, once()).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.never()).commit();
    }

    /**
     * Direct use of the {@link java.io.Closeable} API in a try/with.
     * Automatically calls end on the transaction when closed...
     */
    @Test
    public void normalTryWith() throws Exception {
        Dataset dataset = Mockito.mock(Dataset.class);

        // fancy try-with syntax (automatically calls close, which calls end() )
        try (JenaTransaction outermost = JenaTransaction.begin(dataset, ReadWrite.READ)) {
            JenaTransaction okInner = JenaTransaction.begin(dataset, ReadWrite.READ);
            okInner.end();
        }

        // begin & end should only be called once...
        Mockito.verify(dataset, once()).begin(ReadWrite.READ);
        Mockito.verify(dataset, once()).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.never()).commit();
    }

    /**
     * Run a simple "callable" inside a transaction
     * @throws Exception
     */
    @Test
    public void runWith()  {
        Dataset dataset = Mockito.mock(Dataset.class);
        JenaTransaction.WithTransaction withTransaction = Mockito.mock(JenaTransaction.WithTransaction.class);


        JenaTransaction.runWith(withTransaction, dataset, ReadWrite.READ);


        Mockito.verify(dataset, once()).begin(ReadWrite.READ);
        Mockito.verify(dataset, once()).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.never()).commit();
    }

    @Test
    public void runWithTransaction()  {
        Dataset dataset = Mockito.mock(Dataset.class);
        
        JenaTransaction.runWith(txn -> {
            txn.commit();
            return null;
        }, dataset, ReadWrite.WRITE);


        Mockito.verify(dataset, once()).begin(ReadWrite.WRITE);
        Mockito.verify(dataset, once()).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.times(1)).commit();
    }


    @Test
    public void nestedWriteInReadAreBad() {
        Dataset dataset = Mockito.mock(Dataset.class);

        JenaTransaction outermost = JenaTransaction.begin(dataset, ReadWrite.READ);
        try {
            JenaTransaction okInner = JenaTransaction.begin(dataset, ReadWrite.WRITE);
            Assert.fail("Write inside read is not permitted");
        } catch (IllegalNestedTransactionException e) {
            // expected
        } finally {
            outermost.end();
        }
        Mockito.verify(dataset, once()).begin(ReadWrite.READ);
        Mockito.verify(dataset, once()).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.never()).commit();
    }

    @Test
    public void commit()  {
        Dataset dataset = Mockito.mock(Dataset.class);


        JenaTransaction outermost = JenaTransaction.begin(dataset, ReadWrite.READ);
        try {
            JenaTransaction inner = JenaTransaction.begin(dataset, ReadWrite.READ);
            try {
                JenaTransaction anotherInner = JenaTransaction.begin(dataset, ReadWrite.READ);
                try {
                    anotherInner.commit();
                } finally {
                    anotherInner.end();
                }
                inner.commit();
            } finally {
                inner.end();
            }
        } finally {
            outermost.end();
        }

        // begin & end should only be called once...
        Mockito.verify(dataset, once()).begin(ReadWrite.READ);
        Mockito.verify(dataset, once()).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.times(2)).commit();
    }

    @Test
    public void abort() {
        Dataset dataset = Mockito.mock(Dataset.class);


        JenaTransaction outermost = JenaTransaction.begin(dataset, ReadWrite.READ);
        try {
            JenaTransaction inner = JenaTransaction.begin(dataset, ReadWrite.READ);
            try {
                JenaTransaction anotherInner = JenaTransaction.begin(dataset, ReadWrite.READ);
                try {
                    anotherInner.abort();
                } finally {
                    anotherInner.end();
                }
                inner.abort();
            } finally {
                inner.end();
            }
        } finally {
            outermost.end();
        }

        // begin & end should only be called once...
        Mockito.verify(dataset, once()).begin(ReadWrite.READ);
        Mockito.verify(dataset, once()).end();
        Mockito.verify(dataset, Mockito.times(2)).abort();
        Mockito.verify(dataset, Mockito.never()).commit();
    }

    @Test
    public void outOfSequenceEnd() {
        Dataset dataset = Mockito.mock(Dataset.class);


        JenaTransaction outermost = JenaTransaction.begin(dataset, ReadWrite.READ);
        try {
            JenaTransaction okInner = JenaTransaction.begin(dataset, ReadWrite.READ);
            outermost.end();
            try {
                okInner.end();
                Assert.fail("The nested transaction cannot end normally; as the outermost has ended");
            } catch (JenaTransactionEndedException e) {
                // expected
            }
        } finally {
            outermost.end(); // this just happens
        }

        // begin & end should only be called once...
        Mockito.verify(dataset, once()).begin(ReadWrite.READ);
        Mockito.verify(dataset, Mockito.times(2)).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.never()).commit();
    }

    @Test
    public void read() {
        Dataset dataset = Mockito.mock(Dataset.class);
        final String TEST_DATA = "This is a test";

        Optional<String> result = JenaTransaction.readWith(() -> TEST_DATA, dataset);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(TEST_DATA, result.get());

        // begin & end should only be called once...
        Mockito.verify(dataset, once()).begin(ReadWrite.READ);
        Mockito.verify(dataset, Mockito.times(1)).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.never()).commit();
    }

    @Test
    public void readNested() {
        Dataset dataset = Mockito.mock(Dataset.class);
        final String TEST_DATA = "This is a test";

        Optional<String> result = JenaTransaction.readWith(() -> JenaTransaction.readWith(() -> TEST_DATA, dataset).get(), dataset);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(TEST_DATA, result.get());

        // begin & end should only be called once...
        Mockito.verify(dataset, once()).begin(ReadWrite.READ);
        Mockito.verify(dataset, Mockito.times(1)).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.never()).commit();
    }

    @Test
    public void write() {
        Dataset dataset = Mockito.mock(Dataset.class);
        final String TEST_DATA = "This is a test";

        Optional<String> result = JenaTransaction.writeWithCommit(() -> TEST_DATA, dataset);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(TEST_DATA, result.get());

        // begin & end should only be called once...
        Mockito.verify(dataset, once()).begin(ReadWrite.WRITE);
        Mockito.verify(dataset, Mockito.times(1)).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.times(1)).commit();
    }

    @Test
    public void badWrite() {
        Dataset dataset = Mockito.mock(Dataset.class);
        Optional<String> result = JenaTransaction.writeWithCommit(() -> { throw new UnsupportedOperationException(); }, dataset);
        assertNotNull(result);
        assertFalse(result.isPresent());

        // begin & end should only be called once...
        Mockito.verify(dataset, once()).begin(ReadWrite.WRITE);
        Mockito.verify(dataset, Mockito.times(1)).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.never()).commit();
    }

    @Test
    public void writeWithNestedRead() {
        Dataset dataset = Mockito.mock(Dataset.class);
        final String TEST_DATA = "This is a test";

        Optional<String> result = JenaTransaction.writeWithCommit(() ->
            JenaTransaction.readWith(() -> TEST_DATA, dataset).get(), dataset);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(TEST_DATA, result.get());

        // begin & end should only be called once...
        Mockito.verify(dataset, once()).begin(ReadWrite.WRITE);
        Mockito.verify(dataset, Mockito.times(1)).end();
        Mockito.verify(dataset, Mockito.never()).abort();
        Mockito.verify(dataset, Mockito.times(1)).commit();
    }


    public static VerificationMode once() {
        return Mockito.times(1);
    }
}
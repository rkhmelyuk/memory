package com.khmelyuk.memory.space;

import com.khmelyuk.memory.FixedMemoryAllocator;
import com.khmelyuk.memory.Memory;
import com.khmelyuk.memory.space.transactional.TransactionException;
import com.khmelyuk.memory.space.transactional.TransactionalSpace;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ruslan Khmelyuk
 */
public class TransactionalMemorySpaceTestCase {

    private Memory memory;

    @Before
    public void setUp() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        memory = allocator.allocate(20 * Memory.KB);
    }

    @After
    public void tearDown() {
        memory.free();
    }

    @Test(expected = TransactionException.class)
    public void testFailsToAllocate() throws TransactionException {
        Space s = memory.allocate(15 * Memory.KB);
        s.transactional().start();
    }

    @Test
    public void testTransaction() throws Exception {
        MemorySpace space = memory.allocate(5 * Memory.KB);

        space.write("Hello");
        Assert.assertEquals("Hello", space.readString());

        TransactionalSpace transactional = space.transactional();
        Assert.assertEquals("Hello", transactional.readString());

        space.write("Hello2");
        Assert.assertEquals("Hello2", space.readString());
        Assert.assertEquals("Hello2", transactional.readString());

        transactional.start();

        space.write("Hello3");
        Assert.assertEquals("Hello3", space.readString());
        Assert.assertEquals("Hello2", transactional.readString());

        transactional.write("Hi world!");

        Assert.assertEquals("Hello3", space.readString());
        Assert.assertEquals("Hi world!", transactional.readString());

        transactional.commit();

        Assert.assertEquals("Hi world!", space.readString());
        Assert.assertEquals("Hi world!", transactional.readString());
    }

    @Test(expected = TransactionException.class)
    public void testNestedTransaction() throws Exception {
        MemorySpace space = memory.allocate(5 * Memory.KB);

        space.write("Hello");
        Assert.assertEquals("Hello", space.readString());

        TransactionalSpace transactional = space.transactional();
        Assert.assertEquals("Hello", transactional.readString());

        space.write("Hello2");
        Assert.assertEquals("Hello2", space.readString());
        Assert.assertEquals("Hello2", transactional.readString());

        transactional.start();
        transactional.start();
    }

    @Test
    public void testRollbackTransaction() throws Exception {
        MemorySpace space = memory.allocate(5 * Memory.KB);

        space.write("Hello");
        Assert.assertEquals("Hello", space.readString());

        TransactionalSpace transactional = space.transactional();
        Assert.assertEquals("Hello", transactional.readString());

        space.write("Hello2");
        Assert.assertEquals("Hello2", space.readString());
        Assert.assertEquals("Hello2", transactional.readString());

        transactional.start();

        space.write("Hello3");
        Assert.assertEquals("Hello3", space.readString());
        Assert.assertEquals("Hello2", transactional.readString());

        transactional.write("Hi world!");

        Assert.assertEquals("Hello3", space.readString());
        Assert.assertEquals("Hi world!", transactional.readString());

        transactional.rollback();

        Assert.assertEquals("Hello3", space.readString());
        Assert.assertEquals("Hello3", transactional.readString());
    }

    @Test
    public void testMultiTransaction() throws Exception {
        MemorySpace space = memory.allocate(5 * Memory.KB);

        space.write("Hello");
        Assert.assertEquals("Hello", space.readString());

        TransactionalSpace transactional1 = space.transactional();
        TransactionalSpace transactional2 = space.transactional();

        transactional1.start();
        transactional2.start();

        transactional1.write("Hi world!");
        transactional2.write("Hi world of foo!");

        transactional1.commit();
        Assert.assertEquals("Hi world!", space.readString());

        transactional2.commit();
        Assert.assertEquals("Hi world of foo!", space.readString());
    }
}

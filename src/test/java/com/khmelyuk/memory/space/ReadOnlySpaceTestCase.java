package com.khmelyuk.memory.space;

import com.khmelyuk.memory.FixedMemoryAllocator;
import com.khmelyuk.memory.Memory;
import com.khmelyuk.memory.space.transactional.CopyTransactionalSpace;
import com.khmelyuk.memory.space.transactional.TransactionalSpace;
import com.khmelyuk.memory.space.transactional.WriteNotAllowedException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

/**
 * Tests for memory Spaces
 *
 * @author Ruslan Khmelyuk
 */
public class ReadOnlySpaceTestCase {

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

    @Test
    public void testAddressAndSize() {
        MemorySpace s = memory.allocate(2 * Memory.KB);
        Space ro = s.readOnly();

        Assert.assertEquals(s.getAddress(), ro.getAddress());
        Assert.assertEquals(s.size(), ro.size());
    }

    @Test
    public void testGetBlock() {
        MemorySpace s = memory.allocate(2 * Memory.KB);
        Space ro = s.readOnly();

        Assert.assertSame(s.getBlock(), ro.getBlock());
    }

    @Test
    public void testReadWrite_String() {
        MemorySpace s = memory.allocate(2 * Memory.KB);
        s.write("hello world");

        Space ro = s.readOnly();
        Assert.assertEquals("hello world", ro.readString());
    }

    @Test
    public void testReadWrite_Num() {
        MemorySpace s = memory.allocate(2 * Memory.KB);

        s.write(12323);

        Space ro = s.readOnly();
        int num = (Integer) ro.read();

        Assert.assertEquals(12323, num);
    }

    @Test
    public void testReadOnly() {
        Space s = memory.allocate(2 * Memory.KB);
        Space ro = s.readOnly();

        Assert.assertNotNull(ro);
        Assert.assertSame(ro, ro.readOnly());
    }

    @Test(expected = WriteNotAllowedException.class)
    public void testWriteStringFails() {
        Space s = memory.allocate(2 * Memory.KB);
        Space ro = s.readOnly();

        ro.write("Hello");
    }

    @Test(expected = WriteNotAllowedException.class)
    public void testWriteObjectFails() {
        Space s = memory.allocate(2 * Memory.KB);
        Space ro = s.readOnly();

        ro.write(new User());
    }

    @Test
    public void testTransactional() {
        Space s = memory.allocate(2 * Memory.KB);
        TransactionalSpace ts = s.readOnly().transactional();
        
        Assert.assertNotNull(ts);
        Assert.assertTrue(ts instanceof CopyTransactionalSpace);
    }


    public static class User implements Serializable {
        String firstName;
        String lastName;
        int age;
    }
}

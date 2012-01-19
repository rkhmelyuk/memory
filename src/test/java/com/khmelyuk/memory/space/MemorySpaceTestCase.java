package com.khmelyuk.memory.space;

import com.khmelyuk.memory.FixedMemoryAllocator;
import com.khmelyuk.memory.Memory;
import com.khmelyuk.memory.space.transactional.TransactionalSpace;
import com.khmelyuk.memory.space.transactional.TransactionalSpaceImpl;
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
public class MemorySpaceTestCase {

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
    public void testReadWrite_String() {
        MemorySpace s = memory.allocate(2 * Memory.KB);

        s.write("hello world");

        Assert.assertEquals("hello world", s.readString());
    }

    @Test
    public void testReadWrite_Num() {
        MemorySpace s = memory.allocate(2 * Memory.KB);

        s.write(12323);
        int num = (Integer) s.read();

        Assert.assertEquals(12323, num);
    }

    @Test
    public void testReadWrite_UDT() {
        MemorySpace s = memory.allocate(2 * Memory.KB);

        User john = new User();
        john.firstName = "John";
        john.lastName = "Doe";
        john.age = 18;

        s.write(john);
        User user = (User) s.read();

        Assert.assertNotNull(user);
        Assert.assertEquals(john.firstName, user.firstName);
        Assert.assertEquals(john.lastName, user.lastName);
        Assert.assertEquals(john.age, user.age);
    }

    @Test
    public void testReadOnly() {
        Space s = memory.allocate(2 * Memory.KB);
        Space ro = s.readOnly();

        Assert.assertNotNull(ro);
    }

    @Test(expected = WriteNotAllowedException.class)
    public void testReadOnly_WriteStringFails() {
        Space s = memory.allocate(2 * Memory.KB);
        Space ro = s.readOnly();

        ro.write("Hello");
    }

    @Test(expected = WriteNotAllowedException.class)
    public void testReadOnly_WriteObjectFails() {
        Space s = memory.allocate(2 * Memory.KB);
        Space ro = s.readOnly();

        ro.write(new User());
    }

    @Test
    public void testTransactional() {
        Space s = memory.allocate(2 * Memory.KB);
        TransactionalSpace ts = s.transactional();
        
        Assert.assertNotNull(ts);
        Assert.assertTrue(ts instanceof TransactionalSpaceImpl);
    }


    public static class User implements Serializable {
        String firstName;
        String lastName;
        int age;
    }
}

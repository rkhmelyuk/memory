package com.khmelyuk.memory.space;

import com.khmelyuk.memory.FixedMemoryAllocator;
import com.khmelyuk.memory.Memory;
import com.khmelyuk.memory.MemorySize;
import com.khmelyuk.memory.space.transactional.CopyTransactionalSpace;
import com.khmelyuk.memory.space.transactional.TransactionalSpace;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

/**
 * Tests for memory Spaces
 *
 * @author Ruslan Khmelyuk
 */
public class MemorySpaceTest {

    private Memory memory;

    @Before
    public void setUp() {
        FixedMemoryAllocator allocator = new FixedMemoryAllocator();
        memory = allocator.allocate(MemorySize.kilobytes(20));
    }

    @After
    public void tearDown() {
        memory.free();
    }

    @Test
    public void testReadWrite_String() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));

        s.write("hello world");

        Assert.assertEquals("hello world", s.readString());
    }

    @Test
    public void testReadWrite_Num() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));

        s.write(12323);
        int num = (Integer) s.read();

        Assert.assertEquals(12323, num);
    }

    @Test
    public void testReadWrite_Data() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));
        byte[] data = {1, 2, 3, 4, 5, 6, 7, 11, 12, 14};

        s.write(data);

        byte[] buffer = new byte[data.length];
        Assert.assertThat(s.read(buffer), equalTo(data.length));
        Assert.assertThat(buffer, equalTo(data));
    }

    @Test
    public void testReadWrite_Data_WithOffset() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));
        byte[] data = {1, 2, 3, 4, 5, 6, 7, 11, 12, 14};

        s.write(data, 10, 5);

        byte[] buffer = new byte[data.length];
        Assert.assertThat(s.read(buffer, 10, 5), equalTo(5));
        Assert.assertThat(buffer[0], equalTo(data[0]));
        Assert.assertThat(buffer[1], equalTo(data[1]));
        Assert.assertThat(buffer[2], equalTo(data[2]));
        Assert.assertThat(buffer[3], equalTo(data[3]));
        Assert.assertThat(buffer[4], equalTo(data[4]));
        Assert.assertThat(buffer[5], not(equalTo(data[5])));
    }

    @Test
    public void testReadWrite_UDT() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));

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
    public void testTransactional() {
        Space s = memory.allocate(MemorySize.kilobytes(2));
        TransactionalSpace ts = s.transactional();

        Assert.assertNotNull(ts);
        Assert.assertTrue(ts instanceof CopyTransactionalSpace);
    }

    public static class User implements Serializable {
        String firstName;
        String lastName;
        int age;
    }
}

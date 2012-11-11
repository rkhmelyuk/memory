package com.khmelyuk.memory.space;

import com.khmelyuk.memory.FixedMemoryAllocator;
import com.khmelyuk.memory.Memory;
import com.khmelyuk.memory.MemorySize;
import com.khmelyuk.memory.space.transactional.CopyTransactionalSpace;
import com.khmelyuk.memory.space.transactional.TransactionalSpace;
import com.khmelyuk.memory.space.transactional.WriteNotAllowedException;
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
public class ReadOnlySpaceTestCase {

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
    public void testAddressAndSize() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));
        Space ro = s.readOnly();

        Assert.assertEquals(s.getAddress(), ro.getAddress());
        Assert.assertEquals(s.size(), ro.size());
    }

    @Test
    public void testGetBlock() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));
        Space ro = s.readOnly();

        Assert.assertSame(s.getBlock(), ro.getBlock());
    }

    @Test
    public void testReadWrite_String() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));
        s.write("hello world");

        Space ro = s.readOnly();
        Assert.assertEquals("hello world", ro.readString());
    }

    @Test
    public void testReadWrite_Num() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));

        s.write(12323);

        Space ro = s.readOnly();
        int num = (Integer) ro.read();

        Assert.assertEquals(12323, num);
    }

    @Test
    public void testReadOnly() {
        Space s = memory.allocate(MemorySize.kilobytes(2));
        Space ro = s.readOnly();

        Assert.assertNotNull(ro);
        Assert.assertSame(ro, ro.readOnly());
    }

    @Test(expected = WriteNotAllowedException.class)
    public void testWriteStringFails() {
        Space s = memory.allocate(MemorySize.kilobytes(2));
        Space ro = s.readOnly();

        ro.write("Hello");
    }

    @Test(expected = WriteNotAllowedException.class)
    public void testWriteObjectFails() {
        Space s = memory.allocate(MemorySize.kilobytes(2));
        Space ro = s.readOnly();

        ro.write(new User());
    }

    @Test(expected = WriteNotAllowedException.class)
    public void testReadWrite_WriteDataFails() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));
        Space roSpace = s.readOnly();

        roSpace.write(new byte[]{1, 2, 3, 4, 5, 6, 7, 11, 12, 14});
    }

    @Test(expected = WriteNotAllowedException.class)
    public void testReadWrite_WriteDataWithOffsetFails() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));
        Space roSpace = s.readOnly();

        roSpace.write(new byte[]{1, 2, 3, 4, 5, 6, 7, 11, 12, 14}, 5, 10);
    }

    @Test
    public void testReadWrite_ReadData() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));
        byte[] data = {1, 2, 3, 4, 5, 6, 7, 11, 12, 14};

        s.write(data);

        byte[] buffer = new byte[data.length];
        Assert.assertThat(s.readOnly().read(buffer), equalTo(data.length));
        Assert.assertThat(buffer, equalTo(data));
    }

    @Test
    public void testReadWrite_ReadDataWithOffset() {
        MemorySpace s = memory.allocate(MemorySize.kilobytes(2));
        byte[] data = {1, 2, 3, 4, 5, 6, 7, 11, 12, 14};

        s.write(data, 10, 5);

        byte[] buffer = new byte[data.length];
        Assert.assertThat(s.readOnly().read(buffer, 10, 5), equalTo(5));
        Assert.assertThat(buffer[0], equalTo(data[0]));
        Assert.assertThat(buffer[1], equalTo(data[1]));
        Assert.assertThat(buffer[2], equalTo(data[2]));
        Assert.assertThat(buffer[3], equalTo(data[3]));
        Assert.assertThat(buffer[4], equalTo(data[4]));
        Assert.assertThat(buffer[5], not(equalTo(data[5])));
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
    public void testTransactional() {
        Space s = memory.allocate(MemorySize.kilobytes(2));
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

package com.khmelyuk.memory.vm.storage;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ruslan Khmelyuk
 */
public class ByteArrayStorageTest {

    @Test
    public void testWriteWithOffsets() {
        ByteArrayStorage storage = new ByteArrayStorage(100);

        byte[] data = {10, 20, 30, 40, 50};
        storage.write(data, 10, 2, 3);

        byte[] read = new byte[5];
        storage.read(read, 10, 3);

        Assert.assertEquals(read[0], data[2]);
        Assert.assertEquals(read[1], data[3]);
        Assert.assertEquals(read[2], data[4]);
    }
}

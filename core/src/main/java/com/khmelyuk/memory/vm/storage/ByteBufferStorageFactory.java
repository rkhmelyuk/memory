package com.khmelyuk.memory.vm.storage;

import java.nio.ByteBuffer;

/**
 * A factory for {@link ByteBufferStorage}
 *
 * @author Ruslan Khmelyuk
 */
public class ByteBufferStorageFactory implements StorageFactory {

    private static StorageFactory instance = new ByteBufferStorageFactory();

    public static StorageFactory getInstance() {
        return instance;
    }

    @Override
    public Storage create(int begin, int size) {
        return new ByteBufferStorage(ByteBuffer.allocate(size));
    }
}

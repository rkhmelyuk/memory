package com.khmelyuk.memory.vm.storage;

/**
 * A factory for {@link ByteArrayStorage}
 *
 * @author Ruslan Khmelyuk
 */
public class ByteArrayStorageFactory implements StorageFactory {

    private static StorageFactory instance = new ByteArrayStorageFactory();

    public static StorageFactory getInstance() {
        return instance;
    }

    @Override
    public Storage create(int begin, int size) {
        return new ByteArrayStorage(size);
    }
}

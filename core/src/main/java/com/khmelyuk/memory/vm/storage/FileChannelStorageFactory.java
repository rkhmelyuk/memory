package com.khmelyuk.memory.vm.storage;

import com.khmelyuk.memory.MemoryException;

import java.nio.channels.FileChannel;

/**
 * A storage factory for specified file channel.
 *
 * @author Ruslan Khmelyuk
 */
public class FileChannelStorageFactory implements StorageFactory {

    private final FileChannel channel;
    private final boolean readOnly;

    public FileChannelStorageFactory(FileChannel channel, boolean readOnly) {
        this.channel = channel;
        this.readOnly = readOnly;
    }

    @Override
    public Storage create(int begin, int size) {
        try {
            FileChannel.MapMode mode =
                    readOnly
                            ? FileChannel.MapMode.READ_ONLY
                            : FileChannel.MapMode.READ_WRITE;

            return new ByteBufferStorage(channel.map(mode, begin, size));
        } catch (Exception e) {
            throw new MemoryException("Error to create a storage for file", e);
        }
    }
}

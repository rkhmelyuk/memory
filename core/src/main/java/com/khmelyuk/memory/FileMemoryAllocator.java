package com.khmelyuk.memory;

import com.khmelyuk.memory.vm.DynamicVirtualMemory;
import com.khmelyuk.memory.vm.FixedVirtualMemory;
import com.khmelyuk.memory.vm.FreeEventListener;
import com.khmelyuk.memory.vm.VirtualMemory;
import com.khmelyuk.memory.vm.storage.ByteBufferStorage;
import com.khmelyuk.memory.vm.storage.DynamicStorage;
import com.khmelyuk.memory.vm.storage.FileChannelStorageFactory;
import com.khmelyuk.memory.vm.storage.StorageFactory;
import com.khmelyuk.memory.vm.table.LinkedVirtualMemoryTable;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Allocates a virtual memory that is mapped by file.
 *
 * @author Ruslan Khmelyuk
 */
public class FileMemoryAllocator {

    public Memory allocate(File file, int size) throws IOException {
        assert size >= 0 : "Memory size can't negative";

        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        final FileChannel channel = randomAccessFile.getChannel();
        channel.force(true);

        ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);

        VirtualMemory vm = new FixedVirtualMemory(
                new ByteBufferStorage(buffer),
                new LinkedVirtualMemoryTable(size));

        vm.setFreeEventListener(new FreeEventListener() {
            public void onFree(VirtualMemory memory) {
                try {
                    channel.close();
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return new Memory(vm);
    }

    public Memory allocate(File file, int size, int maxSize) throws IOException {
        assert size >= 0 : "Memory size can't negative";

        if (size == maxSize) {
            return allocate(file, size);
        }

        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        final FileChannel channel = randomAccessFile.getChannel();
        channel.force(true);

        StorageFactory factory = new FileChannelStorageFactory(channel, false);
        DynamicStorage storage = new DynamicStorage(size, maxSize, size, factory);
        VirtualMemory vm = new DynamicVirtualMemory(storage, new LinkedVirtualMemoryTable(size));

        vm.setFreeEventListener(new FreeEventListener() {
            public void onFree(VirtualMemory memory) {
                try {
                    channel.close();
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return new Memory(vm);
    }

}

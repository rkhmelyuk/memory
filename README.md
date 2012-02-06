## Memory
This is a simple implementation fo virtual memory on Java. Virtual memory may be useful when need to allocate once a large
amount of memory buffer and work with it. It is possible to allocate spaces from this memory buffer, work with it and free.

## Virtual Memory
Library currently supports two types of virtual memory: memory with fixed and dynamic sizes. Both types are represented via
simple class `Memory`. Different allocators used to allocate memory.

### Fixed Memory
Fixed memory is represented by `FixedVirtualMemory` and can be allocated using 'FixedMemoryAllocator'. Size of fixed memory can't be changed.

    FixedMemoryAllocator allocator = new FixedMemoryAllocator();
    Memory memory = allocator.allocate(20 * Memory.MB);

### Dynamic Memory
Dynamic memory is represented by `DynamicVirtualMemory` and can be allocating using `DynamicMemoryAllocator`.
It is required to set an initial and max size of virtual memory when allocate memory.

    final int initSize    = 20 * Memory.MB;
    final int maxSize     = 80 * Memory.MB;
    final int growthSize  = 10 * Memory.MB;

    DynamicMemoryAllocator allocator = new DynamicMemoryAllocator();
    Memory memory = allocator.allocate(initSize, maxSize, growthSize);

### Spaces
`Memory` doesn't provide direct access to read/write a memory. Instead it is required to allocate a slice of memory first and work with it.
This slice of memory is called a Space and represented by `Space` interface. Space allows to read/write strings, objects and bytes to the memory.
It also gives a way to be sure the same slice of memory can't be allocated again, and read/written without a reference to appropriate Space object.

    Memory memory = allocator.allocate(20 * Memory.KB);
    Space space = memory.allocate(10 * Memory.KB);

    space.write(user);
    processData(space);

Space data can be easily read/written byte-by-byte using `InputStream` and `OutputStream`:

    space.getOutputStream.write(buffer);
    ...
    processData(space.getInputStream());

After space is used and is not needed anymore, it can be freed easily. After this slice of memory can be allocated again.

    space.free();

For debugging purposes, it is possible to dump the space content to output stream, like console, file or even network.

    space.dump(out);

On top of dump operation built another useful function called copy, which creates a new space in the memory and copy the content from this space.

    Space copySpace = space.copy(); // copySpace represents another location in the memory

#### Read-only space
It's possible also to get a read-only version of the space. Read-only version is just an wrapper for a regular space,
that allows to read data from memory, but not to write. Exception is thrown on a try to write a data.
It's also impossible to free a read-only space.

    Space readOnlySpace = space.readOnly();

    readOnlySpace.read(...);    // is ak
    readOnlySpace.write(...);   // will throw an exception


#### Transactional space
Another important feature of space is support for transactions. When transaction is started, a snapshot of space is
created (using `Space.copy()`) and user continuous to work with it till this transaction is committed or rolled back.
Any changes to the space will be not available for other users of the space. On commit, committed data overrides old data.
Transactional space has a pure implementation, and doesn't support an optimistic transaction currently.

    TransactionalSpace tSpace = space.transactional();
    tSpace.start();
    try {
        ...
        tSpace.commit();
    }
    catch (Exception e) {
        tSpace.rollback();
    }

### Storage
As of version 0.1, the default storage is an array of bytes, represented by `ByteArrayStorage`.
In the next version, there will be a way to use another memory storage, like `ByteBufferStorage`.

## Concurrency
This library is written to work correctly in multi-threaded environment.
The instance of `Memory` can be used in different threads to allocate and free spaces. Space is built on top of `VirtualMemoryBlock`, which is allocated by `VirtualMemoryTable` instance.
`VirtualMemoryTable` uses a different lock-based techniques to avoid allocating the same memory space twice.
And `VirtualMemoryBlock` uses a locks on read/write of data. Locks also used on space dump, to avoid mess in the data.
`Space` also supports locking on for read/write operations of Input/OutputStreams.

There are tests for checking the work of library in multi-threaded environment: `ConcurrencyTestCase` and `ConcurrencyTablePrerformanceTestCase`.
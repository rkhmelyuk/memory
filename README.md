## Memory
This is a simple implementation fo virtual memory on Java. This may be useful when need to allocate once a large
amount of memory buffer and work with it.

Library supports allocation fixed and dynamic memory blocks. It's also possible to work with read-only and transactional spaces,
different storage types (byte array, byte buffer).

*Read [documentation](https://github.com/rkhmelyuk/memory/wiki) for more information and examples*.

### Example
A simple example, where two memories are allocated: a fixed size 20KB memory and
a dynamic memory with a initial size 1MB and max size 5MB and backed by file user.db.

    // setup cache and records memory
    FixedMemoryAllocator allocator = new FixedMemoryAllocator();
    Memory memory = allocator.allocate(20 * Memory.MB);
    Space cacheSpace = memory.allocate(10 * Memory.KB);
    Space recordSpace = memory.allocate(10 * Memory.KB);

    setCacheMemory(cacheSpace);
    setRecordDb(recordSpace);

    // setup user database memory
    FileMemoryAllocator allocator = new FileMemoryAllocator();
    File dbFile = new File("user.db");
    Memory memory = allocator.allocate(dbFile, Memory.MB, 5 * Memory.MB);
    setUserDatabaseMemory(memory);


### Usage
Current library version is 0.1, and it's possible to [download it](http://maven.khmelyuk.com/repo/com/khmelyuk/memory/0.1/memory-0.1.jar).
Also, it's possible to use it as Maven dependency:

    <repositories>
        ...
         <repository>
             <id>maven.khmelyuk.com</id>
             <url>http://maven.khmelyuk.com/repo/</url>
         </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.khmelyuk</groupId>
            <artifactId>memory</artifactId>
            <version>0.1</version>
        </dependency>
    </dependencies>
package com.khmelyuk.memory.vm;

/**
 * An VM event listener that is called on free.
 * Usually used to clean some resources, like opened connections etc.
 *
 * @author Ruslan Khmelyuk
 */
public interface FreeEventListener {

    void onFree(VirtualMemory memory);

}

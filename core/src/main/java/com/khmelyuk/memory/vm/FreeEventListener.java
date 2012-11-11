package com.khmelyuk.memory.vm;

/**
 * An VM event listener that is called on free.
 * Usually used to clean some resources, like opened connections etc.
 *
 * @author Ruslan Khmelyuk
 */
public interface FreeEventListener {

    /**
     * Called after the VM is freed.
     *
     * @param memory the VM that is freed.
     */
    void onFree(VirtualMemory memory);

}

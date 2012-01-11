package com.khmelyuk.memory.space;

/**
 * The listener for the free space event.
 *
 * @author Ruslan Khmelyuk
 */
public interface FreeSpaceListener {

    /**
     * Called when space is freed.
     *
     * @param space the space that is freed.
     */
    void onFreeSpace(Space space);

}

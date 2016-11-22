package io.github.memfis19.cadar.internal.process.utils;

import java.util.Collection;

/**
 * Created by memfis on 7/19/16.
 */
public interface BoundedCollection<E> extends Collection<E> {

    /**
     * Returns true if this collection is full and no new elements can be added.
     *
     * @return <code>true</code> if the collection is full
     */
    boolean isFull();

    /**
     * Gets the maximum size of the collection (the bound).
     *
     * @return the maximum number of elements the collection can hold
     */
    int maxSize();

}

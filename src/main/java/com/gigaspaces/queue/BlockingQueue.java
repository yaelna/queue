package com.gigaspaces.queue;

/**
 * Created by Barak Bar Orion
 * on 3/30/16.
 *
 * @since 11.0
 */
@SuppressWarnings("unused")
interface BlockingQueue<T> {
    T get() throws InterruptedException;
    void put(T t) throws InterruptedException;
}

// synchronize
// wait
// notify
// interrupt
package com.gigaspaces.queue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Barak Bar Orion
 * on 3/30/16.
 *
 * @since 11.0
 */
public class EventsQueue<T extends Event> implements BlockingQueue<T> {
    private List<T> data = new ArrayList<>();

    public EventsQueue(int size) {

    }

    @Override
    public synchronized T get() throws InterruptedException {
    }

    @Override
    public synchronized void put(T t) throws InterruptedException {
        Event e = t;
    }
}

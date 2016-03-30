package com.gigaspaces.queue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Barak Bar Orion
 * on 3/30/16.
 *
 * @since 11.0
 */
public class UnboundedBlockingQueue<T> implements BlockingQueue<T> {
    private List<T> data = new ArrayList<>();

    @Override
    public synchronized T get() throws InterruptedException {
        while(data.isEmpty()){
            wait();
        }
        return data.remove(0);
    }

    @Override
    public synchronized void put(T t) throws InterruptedException {
        data.add(t);
        notifyAll();
    }
}

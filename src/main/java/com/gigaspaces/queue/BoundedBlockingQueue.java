package com.gigaspaces.queue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Barak Bar Orion
 * on 3/30/16.
 *
 * @since 11.0
 */
public class BoundedBlockingQueue<T> implements BlockingQueue<T> {
    private List<T> data = new ArrayList<>();
    private final int SIZE;

    public BoundedBlockingQueue(int size) {
        this.SIZE = size;
    }

    @Override
    public synchronized T get() throws InterruptedException {
        while(data.isEmpty()){
            wait();
        }
//        notifyAll();
        return data.remove(0);
    }

    @Override
    public synchronized void put(T t) throws InterruptedException {
        if (SIZE <= data.size()){
            throw new IllegalStateException();
        }
//        while(SIZE <= data.size()){
//            wait();
//        }
        data.add(t);
        notifyAll();
    }
}

package com.gigaspaces.queue;

import com.sun.org.apache.regexp.internal.RE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.gigaspaces.queue.Op.CREATE;

/**
 * Created by Barak Bar Orion
 * on 3/30/16.
 *
 * @since 11.0
 */
public class EventsQueue<T extends Event> implements BlockingQueue<T> {
    private List<T> data = new ArrayList<>();
    private HashMap RecentOperation= new HashMap();

    public EventsQueue() {

    }

    @Override
    public synchronized T get() throws InterruptedException {
        System.out.println("IN GET, PID: "+Thread.currentThread().getId());
        while(data.isEmpty()){
            wait();
        }
        return data.remove(0);
    }

    @Override
    public synchronized void put(T t) throws InterruptedException {
        System.out.println("IN PUT, PID: "+Thread.currentThread().getId());
        Event e = t;
        boolean shouldBeAdded = compress(e);
        if(shouldBeAdded){
            data.add(t);
            if(data.size()>Main.MAX_SIZE){
                    Main.setMax(data.size());
            }
            System.out.println("size: " + data.size());
        }
        notifyAll();
    }

    //return true if the event should be added
    private synchronized boolean compress(Event e) {
        System.out.println("IN COMPRESS, PID: "+Thread.currentThread().getId());
        // first time operation
        System.out.println("size: " + data.size());
        if (!RecentOperation.containsKey(e.getId())) {
            RecentOperation.put(e.getId(), e.getOperation());
            return true;
        }
        // not first time but last op not in queue
        else if (!data.contains(new Event(e.getId(), (Op) RecentOperation.get(e.getId())))) {
            RecentOperation.put(e.getId(), e.getOperation());
            return true;
        }
        // not first time op and last one still in queue
        else {
            return shouldAddToQueue(e);
        }
    }

    private Boolean shouldAddToQueue(Event e) {
        Op newOp = e.getOperation();
        switch (newOp) {
            case CREATE:
                if (RecentOperation.get(e.getId()) == Op.UPDATE) {
                    RecentOperation.put(e.getId(), e.getOperation());
                    data.remove(new Event(e.getId(), Op.UPDATE));
                    return true;
                } else {
                    return false; // else --> RecentOperation.get(e.getId()) == Op.DELETE / Op.CREATE --> do nothing
                }
            case UPDATE:
                if (RecentOperation.get(e.getId()) == Op.CREATE) {
                    RecentOperation.put(e.getId(), e.getOperation());
                    return true;
                } else {
                    return false; // else --> RecentOperation.get(e.getId()) == Op.DELETE / Op.UPDATE --> do nothing
                }
            case DELETE:
                if (RecentOperation.get(e.getId()) == Op.CREATE) {
                    data.remove(new Event(e.getId(), Op.CREATE));
                    RecentOperation.put(e.getId(), e.getOperation());
                    return true;
                } else if (RecentOperation.get(e.getId()) == Op.UPDATE) {
                    data.remove(new Event(e.getId(), Op.UPDATE));
                    RecentOperation.put(e.getId(), e.getOperation());
                    return true;
                } else {
                    return false;// else --> RecentOperation.get(e.getId()) == Op.DELETE --> do nothing
                }
        }
        return false;
    }

    @Override
    public String toString() {
        return data.toString();
    }
}

package com.gigaspaces.queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yaeln
 * on 4/5/16.
 */
public class AnotherEventsQueue<T extends Event> implements BlockingQueue<T> {
    private List<T> data = new ArrayList<>();
    private HashMap RecentOperation= new HashMap();
    private int SIZE;

    public AnotherEventsQueue(int size) {
         SIZE = size;
    }
    @Override
    public synchronized T get() throws InterruptedException {
        while(data.isEmpty()){
            System.out.println("WAITING IN GET, PID: "+Thread.currentThread().getId());
            wait();
        }
        notifyAll();
        return data.remove(0);
    }
    @Override
    public synchronized void put(T t) throws InterruptedException {

        boolean shouldBeAdded = true;
            if(data.size() == SIZE){
                Event e = t;
                shouldBeAdded = compress(e);
            }
            while (SIZE <= data.size()){
                System.out.println("WAITING IN PUT, PID: "+Thread.currentThread().getId());
                wait();
            }
            if (shouldBeAdded) {
                data.add(t);
                /*if(data.size()>Main.MAX_SIZE){
                    Main.setMax(data.size());
                }*/
            }
            notifyAll();
        }
    //return true if the event should be added
    private synchronized boolean compress(Event e) {
        System.out.println("IN COMP, PID: "+Thread.currentThread().getId());
        // first time operation
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



package com.gigaspaces.queue;

import java.util.ArrayList;

import static java.lang.Thread.sleep;


/**
 * Created by Barak Bar Orion
 * on 3/30/16.
 *
 * @since 11.0
 */
public class Main {
    public static int MAX_SIZE;

    public static void main(String[] args) throws InterruptedException {
        EventsQueue<Event> eventsQueue = new EventsQueue<Event>();
        //AnotherEventsQueue<Event> eventsQueue = new AnotherEventsQueue(100);
        Thread producer = new Producer(eventsQueue);
        Thread consumer = new Consumer(eventsQueue);
        producer.start();
        try{
            sleep(1500);
        }catch (Exception e){
            System.out.println("cant sleep");
        }
        consumer.start();
        while (producer.isAlive() || consumer.isAlive()){
            sleep(500);
        }
        System.out.println("MAX SIZE was " + MAX_SIZE);


    }

    public static void setMax(int max) {
        Main.MAX_SIZE = max;
    }

    public static class Consumer extends Thread {
        private BlockingQueue<Event> queue;

        public Consumer(BlockingQueue eventsQueue) {
            queue = eventsQueue;
        }

        public void run() {
            Event e = null;
            for (int i = 0; i < 100; i++) {
                try {
                    //System.out.println("getting from "+queue.toString());
                    queue.get();
                    //System.out.println("got it!");
                } catch (InterruptedException exception) {
                    System.out.println("failed fo get");
                }
                try {
                    sleep((int) (Math.random() * 100));
                } catch (InterruptedException exception) {
                    System.out.println("cant sleep");
                }
            }
        }
    }

    public static class Producer extends Thread {
        private BlockingQueue<Event> queue;

        public Producer(BlockingQueue eventsQueue) {
            queue = eventsQueue;
        }
        Event event;
        public Event generateEvent() {
            int randomNum = 1 + (int) (Math.random() * 3);
            byte id = (byte) (1 + (byte) (Math.random() * 50));
            switch (randomNum) {
                case 1:
                    event = new Event(id, Op.CREATE);
                    return event;
                case 2:
                    event = new Event(id, Op.UPDATE);
                    return event;
                case 3:
                    event = new Event(id, Op.DELETE);
                    return event;
            }
            return new Event(id, Op.UPDATE);
        }

        public void run() {
            for (int i = 0; i < 300; i++) {
                try {
                    //System.out.println("putting in "+queue.toString());
                    queue.put(generateEvent());
                    //System.out.println("Its In!");
                } catch (InterruptedException e) {
                    System.out.println("failed to put");
                }
                try {
                    sleep((int) (Math.random() * 100));
                } catch (InterruptedException e) {
                    System.out.println("cant sleep");
                }
            }
        }
    }
}

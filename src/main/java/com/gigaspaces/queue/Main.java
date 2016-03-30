package com.gigaspaces.queue;

/**
 * Created by Barak Bar Orion
 * on 3/30/16.
 *
 * @since 11.0
 */
public class Main {
    public synchronized static void main(String[] args) throws InterruptedException {
        Main.class.wait();
    }
}

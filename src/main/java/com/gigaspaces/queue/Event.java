package com.gigaspaces.queue;


/**
 * Created by Barak Bar Orion
 * on 3/30/16.
 *
 * @since 11.0
 */
public class Event{
    private byte id;
    private Op operation;

    public Event(byte id, Op operation) {
        this.id = id;
        this.operation = operation;
    }

    public byte getId() {
        return id;
    }

    public Op getOperation() {
        return operation;
    }

    @Override
    public String toString(){
        return "Event id: " + this.id + ", Operation: " + this.operation;
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        Event e = (Event) obj;
        return ((this.id == e.getId()) && (this.operation == e.getOperation()));
    }
}

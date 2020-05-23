package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class TakeBookEvent implements Event {
    private String name;

    public TakeBookEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
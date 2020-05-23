package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailability implements Event {
    //fields
    private String book;

    //constructor
    public CheckAvailability(String book){
        this.book=book;
    }


    public String getBook() {
        return book;
    }
}

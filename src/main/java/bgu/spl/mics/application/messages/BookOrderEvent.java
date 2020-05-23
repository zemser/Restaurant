package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event {

    //fields
    private String requestedBook;
    private Customer requestingCustomer;
    private int orderTick;


    //constructor
    public BookOrderEvent(String book, Customer c,int tick){
        requestedBook=book;
        requestingCustomer=c;
        orderTick=tick;
    }

    public String getRequestedBook() {
        return requestedBook;
    }

    public Customer getRequestingCustomer() {
        return requestingCustomer;
    }

    public int orderCurrTick() {
        return orderTick;
    }
}

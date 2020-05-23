package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    //fields
    private int tick;

    //constructor
    public TickBroadcast(int i){
        tick=i;
    }

}

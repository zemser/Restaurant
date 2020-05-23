package bgu.spl.mics;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadCounter {

    private AtomicInteger threadCounter;

    private static class ThreadCounterHolder {
        private static ThreadCounter instance = new ThreadCounter();
    }

    private ThreadCounter(){
        threadCounter=new AtomicInteger(0);
    }

    public static ThreadCounter getInstance(){
        return ThreadCounterHolder.instance;
    }

    public void increase(){
        threadCounter.incrementAndGet();
    }

    public AtomicInteger getThreadCounter() {
        return threadCounter;
    }
}

package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private Future f;

    @Before
    public void setUp() throws Exception {
        f=new Future();
    }

    @After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void get() {
        assertNull(f.get());
    }

    @org.junit.Test
    public void resolve() {
        f.resolve(5);
        assertTrue(f.isDone());
        assertNotNull(f.get());
    }

    @org.junit.Test
    public void isDone() {
        assertFalse(f.isDone());
        f.resolve(6);
        assertTrue(f.isDone());
    }

    @org.junit.Test
    public void get1() {
        f.get(1, TimeUnit.MILLISECONDS);
        assertNull(f.get());
        assertFalse(f.isDone());
        f.resolve(55);
        assertNotNull(f.get());
        assertTrue(f.isDone());
    }
}
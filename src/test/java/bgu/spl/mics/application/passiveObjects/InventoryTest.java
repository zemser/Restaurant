package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    private Inventory i;
    @Test
    public void getInstance() {
        assertNotNull(i);
    }

    @Before
    public void setUp() throws Exception {
        i.getInstance();


    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void load() {
        BookInventoryInfo a=new BookInventoryInfo("harry potter",-2,3);
        BookInventoryInfo b=new BookInventoryInfo("lord of the rings",3,6);
        BookInventoryInfo [] temp={a,b};
        i.load(temp);
        int x=i.checkAvailabiltyAndGetPrice("harry potter");
        int y=i.checkAvailabiltyAndGetPrice("lord of the rings");
        assertTrue(x==-1);
        assertTrue(y==6);
    }

    @Test
    public void take() {
        BookInventoryInfo[] s={new BookInventoryInfo("harry potter",2,3)};
        i.load(s);
        OrderResult res1=i.take("lord of the rings");
        assertEquals(OrderResult.NOT_IN_STOCK, res1);
        OrderResult res2=i.take("harry potter");
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN, res2);
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        BookInventoryInfo[] s={new BookInventoryInfo("harry potter",2,3)};
        i.load(s);
        int x=i.checkAvailabiltyAndGetPrice("harry potter");
        assertNotEquals(-1, x);
        int y=i.checkAvailabiltyAndGetPrice("lord of the rings");
        assertEquals(-1, y);
    }

    @Test
    public void printInventoryToFile() {
    }
}
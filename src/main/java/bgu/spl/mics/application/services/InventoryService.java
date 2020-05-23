package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.ThreadCounter;
import bgu.spl.mics.application.messages.CheckAvailability;
import bgu.spl.mics.application.messages.LastTickBrodcast;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;


import static bgu.spl.mics.application.passiveObjects.OrderResult.NOT_IN_STOCK;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	//fields
	private int currentTick;

	public InventoryService(int i) {
		super("InventoryService"+i);
		currentTick=0;
	}

	@Override
	protected void initialize() {

		//terminates at last tick
		subscribeBroadcast(LastTickBrodcast.class, lastTick->{
			terminate();
		});

		/**
		 * sends a message to the inventory to check if there is a book in the inventory
		 * 		if there is resolves the future with its price, else resolves with -1
		 */
		subscribeEvent(CheckAvailability.class, checkInInventory->{
			int p=Inventory.getInstance().checkAvailabiltyAndGetPrice(checkInInventory.getBook());
			if(p==-1)
				complete(checkInInventory, null);
			else
				complete(checkInInventory, p);
		});

		/**
		 * sends an event to an inventory service to try to take a book
		 * if it's in stoke takes it and resolves the event with true, else resolves with false
		 */
		subscribeEvent(TakeBookEvent.class, takeBook->{
			OrderResult takeResult=Inventory.getInstance().take(takeBook.getName());
			if (takeResult==NOT_IN_STOCK){
				complete(takeBook,false);
			}
			else
				complete(takeBook,true);
		});
		ThreadCounter.getInstance().increase(); //for time service initialize

	}

}

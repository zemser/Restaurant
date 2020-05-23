package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.Inventory;


/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	//fields
	private  int currentTick;


	public SellingService(int i) {
		super("SellingService"+i);
		this.currentTick=0;
	}

	@Override
	protected void initialize() {
		//terminates the service
		subscribeBroadcast(LastTickBrodcast.class, lastTick->{
			terminate();
		});

		//increase the current tick
		subscribeBroadcast(TickBroadcast.class, time->{
			this.currentTick++;
		});

		/**
		 * sends a CheckAvailability event to the inventory service to check if the book is available
		 * if so checks if the customer has enough money
		 * if so sends a TakeBookEvent to the inventory service, if it returns true charges the customer
		 * then creates a receipt  for the customer and files it to the money register
		 * at the end sends a DeliveryEvent
		 */
		subscribeEvent(BookOrderEvent.class, checkIfAvailible->{
			Future<Integer> checkAvl=sendEvent(new CheckAvailability(checkIfAvailible.getRequestedBook()));//sends CheckAvailability massage to the inventory service
			int proccesTick=currentTick;
			if(checkAvl.get()!=null){
				int bookPrice=checkAvl.get(); //return price if book is available ,else -1
					Customer buyer = checkIfAvailible.getRequestingCustomer(); //the customer who made the order
					boolean succeedToBuy=false; // boolean- true if the customer was charged
					try {   //lock the customer with semaphore and when the lock is acquired check if the the customer has enough credit and charge him. after the action is done release the lock and flag succeedToBuy to true
						buyer.getSem().acquire();
						if (buyer.getAvailableCreditAmount() >= bookPrice) {
							Future<Boolean> take = sendEvent(new TakeBookEvent(checkIfAvailible.getRequestedBook()));
							if (take.get() != null && take.get()) {
								MoneyRegister.getInstance().chargeCreditCard(buyer, bookPrice); //charge the customer
								succeedToBuy = true;
							}
						}
						buyer.getSem().release();
					} catch (InterruptedException e) { }

					if(succeedToBuy){  //continue the delivery process

						OrderReceipt newReceipt;
						//create a receipt
						synchronized (MoneyRegister.getInstance()) {
							int orderCount = MoneyRegister.getInstance().getOrderCounter().get();
							newReceipt=new OrderReceipt(orderCount, this.getName(), buyer.getId(), checkIfAvailible.getRequestedBook(), bookPrice, checkIfAvailible.orderCurrTick(), proccesTick, currentTick);
							MoneyRegister.getInstance().file(newReceipt);
						}
						//start of delivery
						sendEvent(new DeliveryEvent(buyer.getAddress(), buyer.getDistance()));
						complete(checkIfAvailible, newReceipt);
						}
					else
						complete(checkIfAvailible, null);
			}
			else
				complete(checkIfAvailible, null);
		});
		ThreadCounter.getInstance().increase(); //for time service initialize
	}

}

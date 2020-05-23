package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.LastTickBrodcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.Inventory;





import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	//fields
	private Customer customer;
	private int currentTick;

	public APIService(Customer c,int i) {
		super("APIService"+i);
		this.customer=c;
		this.currentTick=0;

	}

	@Override
	protected void initialize() {

		subscribeBroadcast(LastTickBrodcast.class,lastTick->{ terminate(); });
		/**
		 * For each tick broadcast an API service checks in his list of order:
		 * if it has orders in that tick it orders them and waits for a future receipts to be resolve
		 */
		subscribeBroadcast(TickBroadcast.class, order ->{
				this.currentTick++;
				if (customer.getOrders(currentTick) != null) {
					int orderTick = currentTick; //should be the tick in which the order was made(for the receipt)
					ConcurrentLinkedQueue<Future<OrderReceipt>> futureBookOrders = new ConcurrentLinkedQueue<>();// a vector of futures for each sent massage

					for (String s:customer.getOrders(currentTick)) {
						Event newMessage = new BookOrderEvent(s, customer, orderTick);
						Future<OrderReceipt> t = sendEvent(newMessage);
						futureBookOrders.add(t);
					}

					//goes through a list of futures of each sent message, and tries to get each one of them
					for (Future<OrderReceipt> f : futureBookOrders) {
						if (f.get()!= null) {
							customer.getCustomerReceiptList().add(f.get());
						}
					}
				}
		});
		ThreadCounter.getInstance().increase(); //for time service initialize
	}

}

package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements java.io.Serializable{
	//fields
	private int totalEarnings;
	private LinkedList<OrderReceipt> receiptslist;
	private AtomicInteger orderCounter;

	//
	private static class MoneyRegisterHolder {
		private static MoneyRegister instance = new MoneyRegister();
	}

	//constructor
	private MoneyRegister() {
		totalEarnings=0;
		receiptslist=new LinkedList<>();
		orderCounter=new AtomicInteger(0);
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return  MoneyRegisterHolder.instance;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
			synchronized (receiptslist){
			receiptslist.add(r);
			orderCounter.incrementAndGet();
		}
		}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() { ////////////maybe need to syncronize??
		return totalEarnings;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		if(c.getAvailableCreditAmount()>=amount) {
			c.setAvailableCreditAmount( c.getAvailableCreditAmount() - amount);
			totalEarnings=totalEarnings+amount;
		}
	}

	public AtomicInteger getOrderCounter() {
		return orderCounter;
	}

	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		synchronized (receiptslist) {
			try {
				FileOutputStream file=new FileOutputStream(filename);
				ObjectOutputStream out=new ObjectOutputStream(file);
				out.writeObject(receiptslist);
				out.close();
				file.close();

			} catch (IOException e) {}
		}
	}

}

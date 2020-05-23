package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements java.io.Serializable{
	//fields
	private String name;
	private int id;
	private String address;
	private int distance;
	private List<OrderReceipt> receipts;
	private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> orders;
	private int availableCreditAmount;
	private int creditNumber;
	private  Semaphore sem;

	public Customer(String name, int id, String address, int distance, int creditAmount, int visa, Pair<String,Integer>[] customerOrders){
		this.name=name;
		this.id=id;
		this.address=address;
		this.distance=distance;
		this.receipts=new LinkedList<>();
		this.availableCreditAmount=creditAmount;
		this.creditNumber=visa;
		orders=new ConcurrentHashMap<>();
		initializeOrderList(customerOrders);
		sem=new Semaphore(1);

	}

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return receipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return availableCreditAmount;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditNumber;
	}

	/**
	 * sets the AvailableCreditAmount of the customer
	 * @param availableCreditAmount
	 */
	public void setAvailableCreditAmount(int availableCreditAmount) {
		this.availableCreditAmount = availableCreditAmount;
	}

	public ConcurrentLinkedQueue<String> getOrders(int tick){   ///added for initialize in apiservice
		return orders.get(tick);
	}

	/**
	 * transfers the order pair array to a ConcurrentLinkedQueue
	 * key- is the order tick
	 * value- all orders which are supposed to be ordered in this tick
	 * @param customerOrders
	 */
	public void initializeOrderList(Pair<String,Integer>[] customerOrders){
		for (Pair<String,Integer> p:customerOrders) {
			if(orders.get(p.getSecond())==null)
				orders.put(p.getSecond(),new ConcurrentLinkedQueue<>());
			orders.get(p.getSecond()).add(p.getFirst());
		}
	}

	public Semaphore getSem(){
		return sem;
	}

}

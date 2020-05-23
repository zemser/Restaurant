package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements java.io.Serializable{ ////////////////////////////not sure needed here
	private AtomicInteger OrderId;
	private String seller;
	private int customerId;
	private String bookTitel;
	private int price;
	private int issuedTick;
	private int orderTick;
	private int processTick;

	public OrderReceipt(int orderId,String seller,int customerId,String bookTitle, int price,int orderTick,int processTick,int issuedTick){
		this.OrderId=new AtomicInteger(orderId);
		this.seller=seller;
		this.customerId=customerId;
		this.bookTitel=bookTitle;
		this.price=price;
		this.orderTick=orderTick;
		this.processTick=processTick;
		this.issuedTick=issuedTick;

	}

	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {

		return this.OrderId.get();
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return this.seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {

		return this.customerId;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return this.bookTitel;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {

		return this.price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {
		return this.issuedTick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase request.
     */
	public int getOrderTick() {

		return this.orderTick;
	}
	
	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {
		return this.processTick;
	}


}

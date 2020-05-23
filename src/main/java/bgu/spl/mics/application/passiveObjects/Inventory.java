package bgu.spl.mics.application.passiveObjects;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */

public class Inventory implements java.io.Serializable{
	//fields
	private LinkedList<BookInventoryInfo> bookList;

	private static class InventoryHolder {
		private static Inventory instance = new Inventory();
	}

	//constructor
	private Inventory(){
		bookList=new LinkedList<>();
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		return InventoryHolder.instance;
	}


	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		for(int i=0; i<inventory.length; i++)
				bookList.add(inventory[i]);
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		Iterator<BookInventoryInfo> it=bookList.iterator();
		boolean notInStock=false;
		while(it.hasNext() && !notInStock){
			BookInventoryInfo tmp=it.next();
			if(tmp.getBookTitle().equals(book)) {
				tmp.getLocker().writeLock().lock();//locks the kind of the given book
				if (tmp.getAmountInInventory() > 0) {   //checks if the book exists
					tmp.reduceAmountInInventory();
					tmp.getLocker().writeLock().unlock();//unlocks it
					return OrderResult.SUCCESSFULLY_TAKEN;
				}
				notInStock=true;
				tmp.getLocker().writeLock().unlock();
			}
		}
		return OrderResult.NOT_IN_STOCK;
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		Iterator<BookInventoryInfo> it = bookList.iterator();
		boolean notInStock = false;
		while (it.hasNext() && !notInStock) {
			BookInventoryInfo tmp = it.next();
			if (tmp.getBookTitle().equals(book)) {
				tmp.getLocker().readLock().lock(); //locks the kind of the given book
				if (tmp.getAmountInInventory() > 0) {
					int p = tmp.getPrice();
					tmp.getLocker().readLock().unlock(); //unlocks it
					return p;
				}
				notInStock = true;
				tmp.getLocker().readLock().unlock();
			}
		}
		return -1;
	}

	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		HashMap<String,Integer> map=new HashMap<>();

		synchronized (this) { //creates a hash map of book name and amount
			for (BookInventoryInfo book : bookList)
				map.put(book.getBookTitle(), book.getAmountInInventory());
		}

		synchronized (map){ //write to file
			try {
				FileOutputStream file=new FileOutputStream(filename);
				ObjectOutputStream out=new ObjectOutputStream(file);
				out.writeObject(map);
				out.close();
				file.close();

			} catch (IOException e) {}
		}
	}

}


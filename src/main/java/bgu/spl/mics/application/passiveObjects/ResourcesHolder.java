package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	//fields
	private ConcurrentLinkedQueue<DeliveryVehicle>avialbleVehicleQueue;
	private ConcurrentLinkedQueue<DeliveryVehicle>occupiedVehicleQueue;
	private ConcurrentLinkedQueue<Future> futureQueue;
	private Semaphore sem;
	//
	private static class ResourceHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	//constructor
	private ResourcesHolder(){
		avialbleVehicleQueue=new ConcurrentLinkedQueue();
		occupiedVehicleQueue=new ConcurrentLinkedQueue();
		futureQueue=new ConcurrentLinkedQueue<>();
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return ResourceHolder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> futureVehicle=new Future<>();

		if(sem.tryAcquire()) {
				DeliveryVehicle vehicle = avialbleVehicleQueue.remove();
				occupiedVehicleQueue.add(vehicle);// maybe should me lower???
				futureVehicle.resolve(vehicle);
		}
		else
				futureQueue.add(futureVehicle);
		return futureVehicle;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {

		if(futureQueue.size()>0){
			futureQueue.remove().resolve(vehicle);
		}
		else {
			occupiedVehicleQueue.remove(vehicle);
			avialbleVehicleQueue.add(vehicle);
			sem.release();
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		sem=new Semaphore(vehicles.length);
		for (DeliveryVehicle d:vehicles) {
			avialbleVehicleQueue.add(d); //can we add it this way??
		}
	}

}

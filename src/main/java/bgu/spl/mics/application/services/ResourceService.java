package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireEvent;
import bgu.spl.mics.application.messages.LastTickBrodcast;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	//fields
	private  int currentTick;
	private ConcurrentLinkedQueue<Future> doubleFutures;//for the double futures in the logistic service

	public ResourceService(int i) {
		super("ResourceService"+i);
		this.currentTick=0;
		this.doubleFutures=new ConcurrentLinkedQueue<>();
	}

	@Override
	protected void initialize() {
		/**
		 * terminates at the last tick
		 * resolves all the future in the doubleFutures list
		 * (those futures are created in the resource service and are not stored in the message bus)
		 */
		subscribeBroadcast(LastTickBrodcast.class, lastTick->{
			terminate();
			for (Future f:doubleFutures) {//resolves all the futures that the resource holder created
				f.resolve(null);
			}
		});

		/**
		 * tries to get the a vehicle from the resource holder
		 * stores a future in the doubleFutures list( so they can be resolved when the service is terminated)
		 * resolves the acquire event
		 */
		subscribeEvent(AcquireEvent.class, tryToAcquire->{
			Future<DeliveryVehicle> futureVehicle=ResourcesHolder.getInstance().acquireVehicle();
			doubleFutures.add(futureVehicle); //adds to the list of the double futures
			complete(tryToAcquire,futureVehicle);
		});

		/**
		 * releases the vehicle after it finished the delivery
		 */
		subscribeEvent(ReleaseVehicleEvent.class, tryToRelease->{
			ResourcesHolder.getInstance().releaseVehicle(tryToRelease.getVehicle());
			complete(tryToRelease,true);
		});
		ThreadCounter.getInstance().increase(); //for time service initialize

	}

}

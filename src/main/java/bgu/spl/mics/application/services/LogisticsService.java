package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	//fields
	private  int currentTick;

	public LogisticsService(int i) {
		super("LogisticsService"+i);
		currentTick=0;
	}

	@Override
	protected void initialize() {

		//terminates at last tick
		subscribeBroadcast(LastTickBrodcast.class, lastTick->{
			terminate();
		});

		/**
		 *  tries to get a vehicle from the resource service
		 *  if it does, it makes the delivery and then releases it
		 *  resolves the delivery event with true if it got the car, else with false
		 */
		subscribeEvent(DeliveryEvent.class, AcquiringAppeal->{
			Event newMessage=new AcquireEvent(AcquiringAppeal.getAddress(),AcquiringAppeal.getDistance());
			Future<Future<DeliveryVehicle>> acquiredVehicle=sendEvent(newMessage);
			if(acquiredVehicle.get()!=null) {
				if (acquiredVehicle.get().get() != null) {
					DeliveryVehicle acqVehicle = acquiredVehicle.get().get(); //gets the vehicle
					acqVehicle.deliver(AcquiringAppeal.getAddress(), AcquiringAppeal.getDistance()); //waits the delivery time
					complete(AcquiringAppeal, true);
					Event newReleaseMessage = new ReleaseVehicleEvent(acqVehicle);
					sendEvent(newReleaseMessage); //releases the vehicle who made the delivery
				}
			}
			else
				complete(AcquiringAppeal, false);
		});
		ThreadCounter.getInstance().increase(); //for time service initialize

	}

}

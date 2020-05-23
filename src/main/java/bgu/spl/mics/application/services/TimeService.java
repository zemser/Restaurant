package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.LastTickBrodcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.Inventory;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

	//fields
	private int speed;
	private int duration;
	private int currTick;


	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed=speed;
		this.duration=duration;
		this.currTick=1;  /////// set to 1?
	}


	@Override
	protected  void initialize() {
		//terminates at the time service
		subscribeBroadcast(LastTickBrodcast.class,last->{
			this.terminate();
		});

		/**
		 * for each passing tick sends a TickBroadcast to all the subscribes micro services
		 * waits between each tick
		 * when it gets to the last one sends a LastTickBroadcast and terminates
		 */
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (currTick < duration) { //sends a Tick Broadcast to all the subscribed MicroServices
					Broadcast b = new TickBroadcast(currTick);
					sendBroadcast(b);
					currTick++;
				} else { //sends a LastTickBrodcast to all the subscribed MicroServices to let them know to terminate
					Broadcast b = new LastTickBrodcast();
					sendBroadcast(b);
					timer.cancel();
					timer.purge();
				}
			}
		};

		timer.scheduleAtFixedRate(task, 0, speed);

	}

}

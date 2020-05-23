package bgu.spl.mics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	//fields
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> messageQueueByService; //list of events for each micro service
	private ConcurrentHashMap<Class<? extends Event>,ConcurrentLinkedQueue<MicroService>> serviceQueueByEvent; //list of subscribed services for  event
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> serviceQueueByBroadcast;
	private ConcurrentHashMap<Event,Future> futures;// list of all the events and their future
	private Object locker;
	private static class MessageBusHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}


	//constructor
	private MessageBusImpl(){
		messageQueueByService=new ConcurrentHashMap<>();
		serviceQueueByEvent=new ConcurrentHashMap<>();
		serviceQueueByBroadcast=new ConcurrentHashMap<>();
		futures=new ConcurrentHashMap<>();
		locker=new Object();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusHolder.instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		serviceQueueByEvent.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		serviceQueueByEvent.get(type).add(m);

	}

	@Override
	public  void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		serviceQueueByBroadcast.putIfAbsent(type,new ConcurrentLinkedQueue<>());
		serviceQueueByBroadcast.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future t=futures.get(e);
		t.resolve(result);

	}


	@Override
	public void sendBroadcast(Broadcast b) {
			if (serviceQueueByBroadcast.containsKey(b.getClass())) {
				ConcurrentLinkedQueue<MicroService> v = serviceQueueByBroadcast.get(b.getClass());
				for (MicroService m : v) {
					try {
						messageQueueByService.get(m).put(b);
					} catch (InterruptedException e) {

					}
				}
			}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future madeMission=new Future();
		futures.put(e,madeMission);
		ConcurrentLinkedQueue<MicroService> serviceQueue=serviceQueueByEvent.get(e.getClass());
		if(serviceQueue!=null) {
			synchronized (serviceQueue) {
				if (!serviceQueue.isEmpty()) {
					MicroService tmp = serviceQueue.poll();
					messageQueueByService.get(tmp).add(e);
					serviceQueue.add(tmp);
					return madeMission;
				}
			}
		}
		//for case a event was sent and there is no one who can recive it
		complete(e,null);
		return madeMission;
	}

	@Override
	public void register(MicroService m) {
		messageQueueByService.putIfAbsent(m, new LinkedBlockingQueue<>()); ///////////////////changed from put
	}

	@Override
	public void unregister(MicroService m) {
		unSubscribe(m);
			if (messageQueueByService.containsKey(m)) {
				for (Message e : messageQueueByService.get(m)) { //go through all the massages, and resolve each  event
					if (e instanceof Event) {
						futures.get(e).resolve(null);
					}

				}
			}
	}


	@Override
	public  Message awaitMessage(MicroService m) throws InterruptedException {

		if(messageQueueByService.get(m)==null)
			throw new IllegalStateException("MicroService was never registered");
		return  messageQueueByService.get(m).take();

	}


	/**
	 * unsubscribe function: removes the micro service m from the SerivceQueues that are waiting to receive an event or a broadcast
	 * @param m
	 */
	public void unSubscribe(MicroService m) {
		for (Class<? extends Event> e : serviceQueueByEvent.keySet()) {
			ConcurrentLinkedQueue<MicroService> listToDelete = serviceQueueByEvent.get(e);
			synchronized (listToDelete) {
				listToDelete.remove(m);
			}
		}

		for (Class<? extends Broadcast> b : serviceQueueByBroadcast.keySet()) {
			serviceQueueByBroadcast.get(b).remove(m);

		}
	}

}

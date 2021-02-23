/**
 * 
 */
package io.spielo.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.spielo.client.events.ClientEventHandler;
import io.spielo.client.tasks.ClientReadMessageTask;
import io.spielo.client.tasks.SendHeartbeatTask;

public class Client extends BaseClient {
	
	private final int HEARTBEAT_DELAY = 1000;

	private int id;
	private final Thread readThread;
	private final ClientReadMessageTask readMessageTask;
	private final ScheduledExecutorService executor;
		
	public Client(final String serverIP) {
		super(serverIP);
		
		readMessageTask = new ClientReadMessageTask(this.socket);
		
		readThread = new Thread(readMessageTask, "Read messages thread");
		readThread.start();
		
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(new SendHeartbeatTask(this), HEARTBEAT_DELAY, HEARTBEAT_DELAY, TimeUnit.MILLISECONDS);
	}
	
	public void subscribe(final ClientEventHandler subscriber) {
		readMessageTask.subscribe(subscriber);
	}
	
	public void unsubscribe(final ClientEventHandler subscriber) {
		readMessageTask.unsubscribe(subscriber);
	}
	
	public final void setID(final int id) {
		this.id = id;
	}

	public final int getID() {
		return id;
	}
	
	@Override
	public final void close() {
		readMessageTask.shutdown();
		executor.shutdownNow();
		super.close();
	}
}

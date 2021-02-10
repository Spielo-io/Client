/**
 * 
 */
package io.spielo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.spielo.tasks.SendHeartbeatTask;

public class Client extends BaseClient {
	
	private final int HEARTBEAT_DELAY = 1000;

	private int id;
	private final ScheduledExecutorService executor;
	
	public Client(String serverIP) {
		super(serverIP);
		
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(new SendHeartbeatTask(this), HEARTBEAT_DELAY, HEARTBEAT_DELAY, TimeUnit.MILLISECONDS);
	}
	
	public final void setID(final int id) {
		this.id = id;
	}

	public final int getID() {
		return id;
	}
}


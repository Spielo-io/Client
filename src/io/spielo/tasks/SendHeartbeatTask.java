package io.spielo.tasks;

import io.spielo.Client;
import io.spielo.HeartbeatMessage;

public class SendHeartbeatTask implements Runnable {
	
	private final Client client;
	
	public SendHeartbeatTask(final Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		client.send(new HeartbeatMessage(client.getID(), System.currentTimeMillis()));
	}
}
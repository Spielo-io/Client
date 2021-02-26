package io.spielo.client.tasks;

import io.spielo.client.Client;

public class SendHeartbeatTask implements Runnable {
	
	private final Client client;
	
	public SendHeartbeatTask(final Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		client.sendHeartbeat();
	}
}
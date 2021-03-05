package io.spielo.client.events;

import io.spielo.messages.Message;

public interface ClientEventSubscriber {
	
	public void onMessageReceived(final Message message);
	
	public void onDisconnect();
}

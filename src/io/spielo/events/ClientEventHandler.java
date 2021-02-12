package io.spielo.events;

import io.spielo.Message;

public interface ClientEventHandler {
	
	public void onMessageReceived(final Message message);
	
	public void onDisconnect();
}

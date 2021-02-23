package io.spielo.client.tasks;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.spielo.client.BaseClient;
import io.spielo.client.events.ClientEventHandler;
import io.spielo.messages.Message;
import io.spielo.messages.MessageFactory;

public class ClientReadMessageTask implements Runnable {

	private Boolean isRunning;
	
	private final BaseClient client;
	private final List<ClientEventHandler> subscribers;

	public ClientReadMessageTask(final BaseClient client) {
		this.client = client;
		subscribers = Collections.synchronizedList(new ArrayList<>());
	}
	
	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			try {
				byte[] buffer = client.readByteBuffer();
				Message message = getMessageFromBuffer(buffer);
				notifyMessageReceivec(message);
			} catch (SocketException e) {
				if (e.getMessage().equals("Socket closed"))
					; // Do nothing
				else {
					e.printStackTrace();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void subscribe(final ClientEventHandler subscriber) {
		subscribers.add(subscriber);
	}
	
	public void unsubscribe(final ClientEventHandler subscriber) {
		subscribers.remove(subscriber);
	}
	
	public void shutdown() {
		isRunning = false;
	}
	
	private Message getMessageFromBuffer(final byte[] buffer) {
		MessageFactory factory = new MessageFactory();
		
		return factory.getMessage(buffer);
	}
	
	private void notifyMessageReceivec(final Message message) {
		for (ClientEventHandler subscriber : subscribers) {
			subscriber.onMessageReceived(message);
		}
	}
	
	private void notifyDisconnect() {
		for (ClientEventHandler subscriber : subscribers) {
			subscriber.onDisconnect();
		}
	}
}

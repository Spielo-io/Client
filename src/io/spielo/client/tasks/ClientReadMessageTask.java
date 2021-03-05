package io.spielo.client.tasks;

import java.io.IOException;
import java.net.SocketException;

import io.spielo.client.BaseClient;
import io.spielo.client.events.ClientEventPublisher;
import io.spielo.messages.Message;
import io.spielo.messages.MessageFactory;

public class ClientReadMessageTask implements Runnable {

	private Boolean isRunning;
	
	private final BaseClient client;
	private final ClientEventPublisher publisher;

	public ClientReadMessageTask(final BaseClient client, final ClientEventPublisher publisher) {
		this.client = client;
		this.publisher = publisher;
	}
	
	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			try {
				receiveMessage();
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
	
	private final void receiveMessage() throws SocketException, IOException {
		byte[] buffer = client.readByteBuffer();
		
		MessageFactory factory = new MessageFactory();
		Message message = factory.getMessage(buffer);
		
		publisher.notifyMessageReceived(message);
	}

	public void shutdown() {
		isRunning = false;
	}
}

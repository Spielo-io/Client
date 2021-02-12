package io.spielo.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import io.spielo.Message;
import io.spielo.MessageFactory;
import io.spielo.events.ClientEventHandler;
import io.spielo.util.BufferHelper;

public class ClientReadMessageTask implements Runnable {

	private final Socket socket;
	private final List<ClientEventHandler> subscribers;

	public ClientReadMessageTask(final Socket socket) {
		this.socket = socket;
		subscribers = new ArrayList<>();
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				byte[] buffer = readByteBuffer();
				Message message = getMessageFromBuffer(buffer);
				notifyMessageReceivec(message);
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
	
	private byte[] readByteBuffer() throws IOException {
		InputStream in = socket.getInputStream();		
		byte[] buffer = in.readNBytes(2);
		short length = BufferHelper.fromBufferIntoShort(buffer, 0);
		buffer = in.readNBytes(length);
		
		return buffer;
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

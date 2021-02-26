package io.spielo.client.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.spielo.messages.Message;

public class ClientEventPublisher {

	private final List<ClientEventSubscriber> subscribers;

	public ClientEventPublisher() {
		subscribers = Collections.synchronizedList(new ArrayList<>());
	}
	
	public final void subscribe(final ClientEventSubscriber subscriber) {
		subscribers.add(subscriber);
	}
	
	public final void unsubscribe(final ClientEventSubscriber subscriber) {
		subscribers.remove(subscriber);
	}
	
	public final void notifyMessageReceived(final Message message) {
		for (ClientEventSubscriber subscriber : subscribers) {
			subscriber.onMessageReceived(message);
		}
	}
	
	public final void notifyDisconnect() {
		for (ClientEventSubscriber subscriber : subscribers) {
			subscriber.onDisconnect();
		}
	}
}

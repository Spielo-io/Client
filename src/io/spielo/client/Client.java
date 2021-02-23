/**
 * 
 */
package io.spielo.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.spielo.client.events.ClientEventPublisher;
import io.spielo.client.events.ClientEventSubscriber;
import io.spielo.client.tasks.ClientReadMessageTask;
import io.spielo.client.tasks.SendHeartbeatTask;
import io.spielo.messages.ConnectMessage;
import io.spielo.messages.CreateLobbyMessage;
import io.spielo.messages.HeartbeatMessage;
import io.spielo.messages.Message;
import io.spielo.messages.MessageHeader;
import io.spielo.messages.types.MessageType1;
import io.spielo.messages.types.MessageType2;
import io.spielo.messages.types.MessageType2Lobby;

public class Client extends BaseClient implements ClientEventSubscriber{
	
	private final int HEARTBEAT_DELAY = 1000;

	private int id;
	private final ClientEventPublisher publisher;
	private final Thread readThread;
	private final ClientReadMessageTask readMessageTask;
	private final ScheduledExecutorService executor;
		
	public Client() {
		publisher = new ClientEventPublisher();
		publisher.subscribe(this);
		
		readMessageTask = new ClientReadMessageTask(this, publisher);
		
		readThread = new Thread(readMessageTask, "Read messages thread");
		
		executor = Executors.newSingleThreadScheduledExecutor();
	}
	
	@Override
	public void connect(String ip) {
		super.connect(ip);
		
		readThread.start();
		executor.scheduleWithFixedDelay(new SendHeartbeatTask(this), HEARTBEAT_DELAY, HEARTBEAT_DELAY, TimeUnit.MILLISECONDS);

		send(new ConnectMessage(System.currentTimeMillis()));
	}
	
	public void sendHeartbeat() {
		send(new HeartbeatMessage(id, System.currentTimeMillis()));
	}
	
	public void createLobby(final Boolean isPublic) {
		MessageHeader header = generateHeader(MessageType1.LOBBY, MessageType2Lobby.CREATE);
		send(new CreateLobbyMessage(header, isPublic, (byte) 0, (byte) 0, (byte) 0));
	}
	
	public void subscribe(final ClientEventSubscriber subscriber) {
		publisher.subscribe(subscriber);
	}
	
	public void unsubscribe(final ClientEventSubscriber subscriber) {
		publisher.unsubscribe(subscriber);
	}

	@Override
	public final void close() {
		readMessageTask.shutdown();
		executor.shutdownNow();
		super.close();
	}
	
	public final MessageHeader generateHeader(final MessageType1 type1, final MessageType2 type2) {
		return generateHeader((short) 0, type1, type2);
	}
	
	public final MessageHeader generateHeader(final short receiverID, final MessageType1 type1, final MessageType2 type2) {
		return new MessageHeader(id, receiverID, type1, type2, System.currentTimeMillis());
	}

	@Override
	public void onMessageReceived(Message message) {
		if (message instanceof ConnectMessage) {
			id = message.getHeader().getReceiverID();
		}
	}

	@Override
	public void onDisconnect() {
		
	}
}

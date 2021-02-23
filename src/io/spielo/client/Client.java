/**
 * 
 */
package io.spielo.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.spielo.client.events.ClientEventHandler;
import io.spielo.client.tasks.ClientReadMessageTask;
import io.spielo.client.tasks.SendHeartbeatTask;
import io.spielo.messages.ConnectMessage;
import io.spielo.messages.CreateLobbyMessage;
import io.spielo.messages.MessageHeader;
import io.spielo.messages.types.MessageType1;
import io.spielo.messages.types.MessageType2;
import io.spielo.messages.types.MessageType2Lobby;
import io.spielo.messages.types.MessageType2Server;

public class Client extends BaseClient {
	
	private final int HEARTBEAT_DELAY = 1000;

	private int id;
	private final Thread readThread;
	private final ClientReadMessageTask readMessageTask;
	private final ScheduledExecutorService executor;
		
	public Client() {
		readMessageTask = new ClientReadMessageTask(this);
		
		readThread = new Thread(readMessageTask, "Read messages thread");
		readThread.start();
		
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(new SendHeartbeatTask(this), HEARTBEAT_DELAY, HEARTBEAT_DELAY, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void connect(String ip) {
		super.connect(ip);
		
		send(new ConnectMessage(System.currentTimeMillis()));
	}
	
	public void createLobby(final Boolean isPublic) {
		MessageHeader header = generateHeader(MessageType1.LOBBY, MessageType2Lobby.CREATE);
		send(new CreateLobbyMessage(header, isPublic, (byte) 0, (byte) 0, (byte) 0));
	}
	
	public void subscribe(final ClientEventHandler subscriber) {
		readMessageTask.subscribe(subscriber);
	}
	
	public void unsubscribe(final ClientEventHandler subscriber) {
		readMessageTask.unsubscribe(subscriber);
	}
	
	public final void setID(final int id) {
		this.id = id;
	}

	public final int getID() {
		return id;
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
		return new MessageHeader(getID(), receiverID, type1, type2, System.currentTimeMillis());
	}
}

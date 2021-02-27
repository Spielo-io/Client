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
import io.spielo.messages.lobby.CreateLobbyMessage;
import io.spielo.messages.HeartbeatMessage;
import io.spielo.messages.Message;
import io.spielo.messages.MessageHeader;
import io.spielo.messages.lobbysettings.LobbyBestOf;
import io.spielo.messages.lobbysettings.LobbyGame;
import io.spielo.messages.lobbysettings.LobbyTimer;
import io.spielo.messages.types.*;

public class Client extends BaseClient implements ClientEventSubscriber {
	
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
	
	public void subscribe(final ClientEventSubscriber subscriber) {
		publisher.subscribe(subscriber);
	}
	
	public void unsubscribe(final ClientEventSubscriber subscriber) {
		publisher.unsubscribe(subscriber);
	}

	public void game4Win(final int value) {
		//MessageHeader header = generateHeader(MessageType1.GAME, MessageType2Game.);
		// TODO
	}
	
	public void gameTicTacToe(final int value) {
		// TODO
	}

	public void createLobby(final Boolean isPublic, final LobbyGame game, final LobbyBestOf bestOf, final LobbyTimer timer, final String username) {
		MessageHeader header = generateHeader(MessageType1.LOBBY, MessageType2Lobby.CREATE);
		send(new CreateLobbyMessage(header, isPublic, game, timer, bestOf));
	}
	
	public void lobbySettings(final Boolean isPublic, final LobbyGame game, final LobbyBestOf bestOf, final LobbyTimer timer) {
		// TODO
	}
	
	public void joinRandomLobby(final String username) {
		// TODO
	}
	
	public void joinLobby(final String username, final String lobbycode) {
		// TODO
	}
	
	public void readyToPlay(final Boolean isReady) {
		// TODO
	}	
	
	@Override
	public final void close() {
		readMessageTask.shutdown();
		executor.shutdownNow();
		super.close();
	}
	
	public final MessageHeader generateHeader(final MessageType1 type1, final ByteEnum type2) {
		return generateHeader((short) 0, type1, type2);
	}
	
	public final MessageHeader generateHeader(final short receiverID, final MessageType1 type1, final ByteEnum type2) {
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

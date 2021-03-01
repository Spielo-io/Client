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
import io.spielo.messages.Message;
import io.spielo.messages.MessageHeader;
import io.spielo.messages.games.TicTacToeMessage;
import io.spielo.messages.games.Win4Message;
import io.spielo.messages.lobby.CreateLobbyMessage;
import io.spielo.messages.lobby.JoinLobbyMessage;
import io.spielo.messages.lobby.LobbyListRequestMessage;
import io.spielo.messages.lobby.LobbySettingsMessage;
import io.spielo.messages.lobby.ReadyToPlayMessage;
import io.spielo.messages.lobbysettings.LobbyBestOf;
import io.spielo.messages.lobbysettings.LobbyGame;
import io.spielo.messages.lobbysettings.LobbySettings;
import io.spielo.messages.lobbysettings.LobbyTimer;
import io.spielo.messages.server.ConnectMessage;
import io.spielo.messages.server.HeartbeatMessage;
import io.spielo.messages.types.ByteEnum;
import io.spielo.messages.types.MessageType1;
import io.spielo.messages.types.MessageType2Game;
import io.spielo.messages.types.MessageType2Lobby;

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
	
	public void subscribe(final ClientEventSubscriber subscriber) {
		publisher.subscribe(subscriber);
	}
	
	public void unsubscribe(final ClientEventSubscriber subscriber) {
		publisher.unsubscribe(subscriber);
	}

	public void gameTicTacToe(final int value) {
		MessageHeader header = generateHeader(MessageType1.GAME, MessageType2Game.TicTacToe);
		TicTacToeMessage message = new TicTacToeMessage(header, (byte) value);
	
		send(message);
	}
	
	public void game4Win(final int value) {
		MessageHeader header = generateHeader(MessageType1.GAME, MessageType2Game.Win4);
		Win4Message message = new Win4Message(header, (byte) value);
	
		send(message);
	}
	
	public void createLobby(final Boolean isPublic, final LobbyGame game, final LobbyBestOf bestOf, final LobbyTimer timer, String username) {
		MessageHeader header = generateHeader(MessageType1.LOBBY, MessageType2Lobby.CREATE);
		LobbySettings settings = new LobbySettings(isPublic, game, timer, bestOf);
		send(new CreateLobbyMessage(header, settings, username));
	}

	public void lobbySettings(final Boolean isPublic, final LobbyGame game, final LobbyBestOf bestOf, final LobbyTimer timer) {
		MessageHeader header = generateHeader(MessageType1.LOBBY, MessageType2Lobby.SETTINGS);
		LobbySettings settings = new LobbySettings(isPublic, game, timer, bestOf);
		LobbySettingsMessage message = new LobbySettingsMessage(header, settings);
		
		send(message);
	}
	
	public void joinRandomLobby(final String username) {
		joinLobby(username, "");
	}
	
	public void joinLobby(final String username, final String lobbycode) {
		MessageHeader header = generateHeader(MessageType1.LOBBY, MessageType2Lobby.JOIN);
		JoinLobbyMessage message = new JoinLobbyMessage(header, lobbycode, username);
		
		send(message);
	}
	
	public void refreshLobbyList() {
		MessageHeader header = generateHeader(MessageType1.LOBBY, MessageType2Lobby.LOBBY_LIST_REQUEST);
		LobbyListRequestMessage message = new LobbyListRequestMessage(header);
		
		send(message);
	}
	
	public void readyToPlay(final Boolean isReady) {
		MessageHeader header = generateHeader(MessageType1.LOBBY, MessageType2Lobby.LOBBY_IS_READY);
		ReadyToPlayMessage message = new ReadyToPlayMessage(header, isReady);
		send(message);
	}	

	public void sendHeartbeat() {
		send(new HeartbeatMessage(id, System.currentTimeMillis()));
	}
		
	@Override
	public final void close() {
		readMessageTask.shutdown();
		executor.shutdownNow();
		super.close();
	}
	
	private final MessageHeader generateHeader(final MessageType1 type1, final ByteEnum type2) {
		return generateHeader((short) 0, type1, type2);
	}
	
	private final MessageHeader generateHeader(final short receiverID, final MessageType1 type1, final ByteEnum type2) {
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

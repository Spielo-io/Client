package io.spielo.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ServerClient extends BaseClient {

	private final short id;

	private long lastHeartbeat;
	
	public ServerClient(final Socket socket, final short id) {
		super(socket);
		this.id = id;
	}
	
	public InputStream getInputStream() {
		try {
			return socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public final short getID() {
		return id;
	}

	public void setLastHeatbeat(final long currentTimeMillis) {
		lastHeartbeat = currentTimeMillis;
	}
}

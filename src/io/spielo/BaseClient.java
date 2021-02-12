package io.spielo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class BaseClient {
	private final static int PORT = 8123;

	protected final Socket socket;
	
	public BaseClient(final String ip) {
		socket = connectSocket(ip);
	}

	protected BaseClient(final Socket socket) {
		this.socket = socket;
	}

	public final void send(Message message) {
		try {
			OutputStream out = socket.getOutputStream();
			out.write(message.toByteArray());
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private final Socket connectSocket(final String ip) {
		try {
			return new Socket(ip, PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

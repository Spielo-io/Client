package io.spielo.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import io.spielo.messages.Message;
import io.spielo.messages.util.BufferHelper;

public class BaseClient {
	private final static int PORT = 8123;

	protected Socket socket;
	
	public BaseClient() {
		socket = null;
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

	public void connect(final String ip) {
		try {
			socket = new Socket(ip, PORT);
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

	public byte[] readByteBuffer() throws SocketException, IOException {
		InputStream in = socket.getInputStream();		
		byte[] buffer = in.readNBytes(2);

		short length = BufferHelper.fromBufferIntoShort(buffer, 0);
		buffer = in.readNBytes(length);
		return buffer;
	}
}

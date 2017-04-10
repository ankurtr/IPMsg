package listeners;

import ipmsg.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer extends Thread {

	private ServerSocket socket = null;

	public FileServer() {
		try {
			socket = new ServerSocket(Utils.tcp_port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		try {

			System.out.println("request received");
			new FileThread(socket.accept()).start();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}

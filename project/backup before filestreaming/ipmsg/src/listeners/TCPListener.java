package listeners;

import ipmsg.Utils;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPListener implements Runnable {

	private ServerSocket server;
	private boolean listening=true;
	
	
	
	public TCPListener() {
	//	super();
		// TODO Auto-generated constructor stub
		
		try {
			server=new ServerSocket(Utils.tcp_port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	@Override
	public void run() {
		
			
		// TODO Auto-generated method stub
		while(listening)
		{
			try {
				new WorkerThread(server.accept()).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//				try {
//					//server.close();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
			}
		}
		
		
		
	}

}

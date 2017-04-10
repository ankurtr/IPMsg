package listeners;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FileThread extends Thread {

	private Socket socket;
	private String filePath;
	
	

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public FileThread(Socket socket) {
		this.socket = socket;
		System.out.println("server thread" + this.socket.isConnected());
		//this.filePath = filePath;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try

		{
			ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
			try {
				filePath=(String) ois.readObject();				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			File f = new File(this.filePath);
			
			byte[] buf = new byte[8192];

			InputStream is = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(is);

			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			int c = 0;

			while ((c = bis.read(buf, 0, buf.length)) > 0) {
				oos.write(buf, 0, c);
				oos.flush();
				// buf=new byte[8192];
			}

			oos.close();
			//socket.shutdownOutput();
			// client.shutdownOutput();
			System.out.println("stop");
			// client.shutdownOutput();
			ois.close();
//			Thread.sleep(500);
			
			is.close();
			bis.close();
			socket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}

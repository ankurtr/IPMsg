package listeners;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileReceiveThread extends Thread {

	private String fileStorePath;
	private String sourceFile;
	private Socket socket = null;

	public FileReceiveThread(String ip, int port, String fileStorePath,
			String sourceFile) {
		this.fileStorePath = fileStorePath;
		this.sourceFile = sourceFile;
		try {
			socket = new Socket(ip, port);
			System.out.println("receive file thread " + socket.isConnected());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			oos.writeObject(sourceFile);
			oos.flush();
			// oos.close();
			File f = new File(fileStorePath);

			OutputStream os = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(os);

			byte[] buf = new byte[8192];
			int c = 0;

			//************ NOTE
			
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());

			while ((c = ois.read(buf, 0, buf.length)) > 0) {
				// ois.read(buf);
				bos.write(buf, 0, c);
				bos.flush();
				// buf = new byte[8192];
			}

			ois.close();
			oos.close();
			//
			os.close();
			bos.close();

			 socket.close();
			//Thread.sleep(500);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}

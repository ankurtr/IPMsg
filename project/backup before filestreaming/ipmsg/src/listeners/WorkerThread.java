package listeners;

import ipmsg.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.swt.widgets.Display;

import ui.MessageWindow;

class WorkerThread extends Thread {

	private Socket client;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	private Message message;

	WorkerThread(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {

		try {

			ois = new ObjectInputStream(client.getInputStream());
			Object o = ois.readObject();
			message=(Message) o;
			
			if (o instanceof Message) {

				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						MessageWindow tw = new MessageWindow();
						tw.setMessage(message);
						tw.open();
					}
				});

			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			// try {
			// ois.close();
			// client.close();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}
}
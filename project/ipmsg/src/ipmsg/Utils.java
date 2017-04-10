package ipmsg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.ResourceBundle;

import listeners.FileServer;
import listeners.UDPListener;
import ui.IpMsg;

// enum FileTypes{VOB,WMV,PDF,DOC,TXT,PPT,ZIP,RAR};

public class Utils {

	public static ResourceBundle rb;
	public static final int MAX_IPS = 1;
	public static int udp_port1;
	public static int udp_port2;
	public static int tcp_port;
	public static PeerStatus peerStatus;
	private static String settingsFilePath;
	private static String[] ips;
	public static String soundFile;

	public Utils() {
		rb = ResourceBundle.getBundle("props.IpMsg");

		udp_port1 = Integer.parseInt(rb.getString("udp_port1"));
		udp_port2 = Integer.parseInt(rb.getString("udp_port2"));

		tcp_port = Integer.parseInt(rb.getString("tcp_port"));
		settingsFilePath = System.getProperty("user.home")
				+ System.getProperty("file.separator")
				+ rb.getString("settingsFilePath");

		soundFile = rb.getString("ring_file");
		// obtain application ip broadcast ranges
		ips = new String[MAX_IPS];
		int count = 0;

		Enumeration<String> keys = rb.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (key.contains("broadcast_ip")) {
				ips[count++] = rb.getString(key);
			}
		}

	}

	// send UDP message
	// broadcast message in the network for the connection or disconnection
	public static void sendMessageUDP(Message message, boolean broadcast,
			InetAddress... ipaddress) {

		DatagramSocket socket = null;
		DatagramPacket[] packets = new DatagramPacket[ipaddress.length];

		byte[] buf = new byte[5000];
		ByteArrayOutputStream baos;
		ObjectOutputStream oos;

		try {
			socket = new DatagramSocket();

			baos = new ByteArrayOutputStream();
			try {

				oos = new ObjectOutputStream(baos);
				oos.writeObject(message);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			buf = baos.toByteArray();

			if (!broadcast) {
				for (int i = 0; i < ipaddress.length; i++) {

					packets[i] = new DatagramPacket(buf, buf.length,
							ipaddress[i], udp_port1);

					try {
						socket.send(packets[i]);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else {
				// multicast packet to all range of ips
				for (int i = 0; i < ips.length; i++) {
					try {
						DatagramPacket dp = new DatagramPacket(buf, buf.length,
								InetAddress.getByName(ips[i]), Utils.udp_port1);
						socket.send(dp);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			socket.close();
		}

	}

	// send message using TCP
	public static void sendMessageTCP(Message message, InetAddress... ipaddress) {
		ObjectOutputStream oos = null;
		Socket[] socket = new Socket[ipaddress.length];
		try {

			for (int i = 0; i < ipaddress.length; i++) {
				socket[i] = new Socket(ipaddress[i], tcp_port);

				if (socket[i].isConnected()) {
					oos = new ObjectOutputStream(socket[i].getOutputStream());
					oos.writeObject(message);
					oos.flush();
					oos.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			System.out.println(e.getClass());
		}
	}

	// write settings into the file
	public static void writeSettings(PeerInfo peer) {
		// System.out.println(encrypter.decrypt(encrypted_password));
		File file = new File(settingsFilePath);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(peer);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// read settings of the peer from the file
	public static PeerInfo readSettings() {
		PeerInfo peer = null;
		File file = new File(settingsFilePath);
		if (file.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(file));
				peer = (PeerInfo) ois.readObject();
				// System.out.println("deserializing :-  " + peer);
			} catch (ClassNotFoundException | IOException ex) {
				ex.printStackTrace();
			}
		}
		return peer;
	}

	// generate auto settings
	public static PeerInfo generateAutoSettings() {

		PeerInfo peer;
		String default_group = rb.getString("default_group");

		InetAddress addr = null;
		String username = null, ipaddress = null, host = null;
		try {
			addr = InetAddress.getLocalHost();
			username = host = addr.getHostName();
			// ipaddress = addr.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		peer = new PeerInfo(username, default_group, host, null);
		// System.out.println(peer);

		File file = new File(settingsFilePath);
		if (file.exists())
			file.delete();
		try {
			file.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(peer);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return peer;
	}

	// start of the app
	public static void initializeApp() {

		try {

			// construct PeerInfo...
			IpMsg.myInfo = Utils.readSettings();
			if (IpMsg.myInfo == null) {
				IpMsg.myInfo = Utils.generateAutoSettings();
			}
			InetAddress addr = InetAddress.getLocalHost();
			IpMsg.myInfo.setIpAddress(addr.getHostAddress());

			// start UDP listener thread
			new Thread(new UDPListener()).start();

			// start TCP listenet thread
			// new Thread(new TCPListener()).start();
			new FileServer().start();

			// Start file server

			// peer in connecting in the network so let others know about it.
			Message message = new Message();
			message.setPeerInfo(IpMsg.myInfo);
			message.setStatus(PeerStatus.CONNECTION_REQUEST.ordinal());

			Utils.sendMessageUDP(message, true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// stop the app
	public static void stopApp() {
		Message message = new Message();
		message.setStatus(PeerStatus.DISCONNECTION_REQUEST.ordinal());
		message.setPeerInfo(IpMsg.myInfo);
		Utils.sendMessageUDP(message, true);
		System.exit(0);
	}

	// select the group members to whom send the message
	/*
	 * public static Table selectGroupUsers(Shell shell,int table_style,int
	 * tableitem_style,int selectedIndex, Table table) { Table retval = new
	 * Table(shell, table_style);
	 * 
	 * TableItem[] items=table.getItems();
	 * 
	 * for(int i=0;i<items.length;i++) {
	 * 
	 * TableItem item=new TableItem(retval, tableitem_style); item.setText(new
	 * String
	 * []{items[i].getText(0),items[i].getText(1),items[i].getText(2),items
	 * [i].getText(3)});
	 * 
	 * }
	 * 
	 * TableItem item = table.getItem(selectedIndex);
	 * 
	 * String group_name = item.getText(1);
	 * 
	 * for (int j = 0; j < retval.getItemCount(); j++) {
	 * 
	 * TableItem dummyItem = table.getItem(j); if
	 * (dummyItem.getText(1).equals(group_name)) { retval.select(j); } } return
	 * retval; }
	 */

	// end call
	public static void endCall() {
		Message message = new Message();

		message.setPeerInfo(IpMsg.myInfo);

		message.setStatus(PeerStatus.AUDIO_CALL_END.ordinal());

		message.setMessage("I am ending the call");

		VoiceChat.userBusy = false;

		try {

			VoiceChat.recFlag = false;
			VoiceChat.sendflag = false;

			Utils.sendMessageUDP(message, false,
					InetAddress.getByName(VoiceChat.callerInfo.getIpAddress()));

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// refresh the list of users
	public static void refreshUserList() {

		Message message = new Message();
		message.setPeerInfo(IpMsg.myInfo);
		message.setStatus(PeerStatus.CONNECTION_REQUEST.ordinal());
		Utils.sendMessageUDP(message, true);

		refreshPeers();

	}

	// main window call button
	public static void enableCallButton() {
		IpMsg.btnCall.setEnabled(true);
	}

	// main window call button
	public static void disableCallButton() {
		IpMsg.btnCall.setEnabled(false);
	}

	// main window call button
	public static void enableEndCallButton() {
		IpMsg.btnEndCall.setEnabled(true);
	}

	// main window call button
	public static void disableEndCallButton() {
		IpMsg.btnEndCall.setEnabled(false);
	}

	public static void refreshPeers() {
		if (IpMsg.connectedPeers.size() > 0) {

			// System.out.println(IpMsg.connectedPeers);
			IpMsg.fillData();

			// Display.getDefault().asyncExec(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			//
			// Utils.refreshPeers();
			// }
			// });

			System.out.println(IpMsg.lblMembers);
			IpMsg.lblMembers.setText("Member\n" + IpMsg.connectedPeers.size());

		}
	}
}

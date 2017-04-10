package listeners;

import ipmsg.Message;
import ipmsg.PeerInfo;
import ipmsg.PeerStatus;
import ipmsg.Utils;
import ipmsg.VoiceChat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import ui.CallRequest;
import ui.IpMsg;
import ui.MessageWindow;

public class UDPListener implements Runnable {

	private DatagramSocket receivingSocket;
	public static boolean listening = true;
	private ByteArrayInputStream bais;
	private ObjectInputStream ois;
	private ByteArrayOutputStream baos;
	private ObjectOutputStream oos;
	private Message message;
	private PeerInfo peer;
	private DatagramPacket packet;
	private String sender;
	private VoiceChat vc;

	public UDPListener() {
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			receivingSocket = new DatagramSocket(Utils.udp_port1);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (listening) {

			byte[] buf = new byte[5000];
			packet = new DatagramPacket(buf, buf.length);
			// synchronized (socket) {
			try {
				receivingSocket.receive(packet);

				bais = new ByteArrayInputStream(buf);
				ois = new ObjectInputStream(bais);

				sender = packet.getAddress().getHostAddress();

				Object o = ois.readObject();

				// System.out.println(o);

				if (o instanceof Message) {
					message = (Message) o;

					// System.out
					// .println(PeerStatus.values()[message.getStatus()]);

					Hashtable<String, PeerInfo> connectedPeers = IpMsg.connectedPeers;
					peer = message.getPeerInfo();

					// System.out.println(PeerStatus.values()[message.getStatus()]);
					// System.out.println(message.getPeerInfo());

					if (PeerStatus.values()[message.getStatus()]
							.equals(PeerStatus.CONNECTION_ACKNOWLEDGEMENT)) {

						if (!connectedPeers.containsKey(peer.getIpAddress())) {
							connectedPeers.put(peer.getIpAddress(), peer);

						}
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								Utils.refreshPeers();
							}
						});
					} else if (PeerStatus.values()[message.getStatus()]
							.equals(PeerStatus.CONNECTION_REQUEST)) {

						if (!connectedPeers.containsKey(peer.getIpAddress())) {
							connectedPeers.put(peer.getIpAddress(), peer);

							message = new Message();

							message.setPeerInfo(IpMsg.myInfo);
							message.setStatus(PeerStatus.CONNECTION_ACKNOWLEDGEMENT
									.ordinal());

							Utils.sendMessageUDP(message, false,
									InetAddress.getByName(sender));

							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub

									Utils.refreshPeers();
								}
							});

						}

					} else if (PeerStatus.values()[message.getStatus()]
							.equals(PeerStatus.MESSAGE)) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								MessageWindow tw = new MessageWindow();
								tw.setMessage(message);
								tw.open();
							}
						});

					} else if (PeerStatus.values()[message.getStatus()]
							.equals(PeerStatus.DISCONNECTION_REQUEST)) {
						if (connectedPeers.containsKey(peer.getIpAddress())) {
							connectedPeers.remove(peer.getIpAddress());

							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub

									Utils.refreshPeers();
								}
							});
						}
					} else if (PeerStatus.values()[message.getStatus()]
							.equals(PeerStatus.AUDIO_CALL_REQUEST)) {

						if (VoiceChat.userBusy == true) {
							Message responseMessage = new Message();
							responseMessage
									.setStatus(PeerStatus.AUDIO_CALL_BUSY
											.ordinal());
							responseMessage.setPeerInfo(IpMsg.myInfo);

							Utils.sendMessageUDP(responseMessage, false,
									InetAddress.getByName(sender));
							return;
						}

						VoiceChat.userBusy = true;
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								CallRequest cr = new CallRequest();
								cr.setMessage(message);

								// System.out.println("call"
								// + message.getPeerInfo());

								VoiceChat.callerInfo = message.getPeerInfo();

								cr.open();

							}
						});
						VoiceChat.startTimer();
						VoiceChat.startRing();

					} else if ((PeerStatus.values()[message.getStatus()]
							.equals(PeerStatus.AUDIO_CALL_ACCEPT))) {

						VoiceChat.stopTimer();
						VoiceChat.stopRing();

						new Thread(new StartCall(message.getPeerInfo()
								.getIpAddress())).start();

						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								Utils.enableEndCallButton();
							}
						});

					} else if ((PeerStatus.values()[message.getStatus()]
							.equals(PeerStatus.AUDIO_CALL_REJECT))) {

						VoiceChat.stopRing();
						VoiceChat.stopTimer();

						VoiceChat.userBusy = false;

						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								Utils.enableCallButton();
								Utils.disableEndCallButton();
							}
						});

					} else if ((PeerStatus.values()[message.getStatus()]
							.equals(PeerStatus.AUDIO_CALL_END))) {
						VoiceChat.recFlag = false;
						VoiceChat.sendflag = false;
						VoiceChat.userBusy = false;

						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								Utils.enableCallButton();
								Utils.disableEndCallButton();
							}
						});

					}

					else if ((PeerStatus.values()[message.getStatus()]
							.equals(PeerStatus.MESSAGE_ACKNOWLEDGEMENT))) {
						System.out.println("Ack received");
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								IpMsg.btnToolTip.notifyListeners(SWT.Selection,
										null);
								// IpMsg.btnToolTip.notifyListeners(SWT.MouseDown,
								// null);
							}
						});
					} else if ((PeerStatus.values()[message.getStatus()]
							.equals(PeerStatus.AUDIO_CALL_BUSY))) {
						VoiceChat.userBusy = false;
						JOptionPane.showMessageDialog(null, message
								.getPeerInfo().getUsername()
								+ " is busy on other call");
						VoiceChat.stopRing();
						VoiceChat.stopTimer();

						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Utils.enableCallButton();
								Utils.disableEndCallButton();
							}
						});

					} else if (message.getStatus() == PeerStatus.MESSAGE_WITH_FILES
							.ordinal()) {
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								MessageWindow mw = new MessageWindow();
								mw.setMessage(message);
								mw.open();
							}

						});
					} else if (message.getStatus() == PeerStatus.FILE_DOWNLOAD_REQUEST
							.ordinal()) {
						
						Message m = new Message();
						m.setStatus(PeerStatus.FILE_DOWNLOAD_RESPONSE.ordinal());
						m.setPeerInfo(IpMsg.myInfo);
						m.setFileStorePath(message.getFileStorePath());
						m.setRequestedFile(message.getRequestedFile());

						System.out.println("dw req:-" + message);
						
						Utils.sendMessageUDP(m, false, InetAddress.getByName(message.getPeerInfo().getIpAddress()));
					}
					else if(message.getStatus()==PeerStatus.FILE_DOWNLOAD_RESPONSE.ordinal())
					{
						System.out.println("dw res:-" + message);
						
						new FileReceiveThread(message.getPeerInfo().getIpAddress(), Utils.tcp_port,message.getFileStorePath(),message.getRequestedFile()).start();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
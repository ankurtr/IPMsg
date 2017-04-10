package ui;

import ipmsg.Message;
import ipmsg.PeerStatus;
import ipmsg.Utils;
import ipmsg.VoiceChat;

import java.net.InetAddress;
import java.net.UnknownHostException;

import listeners.StartCall;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CallRequest {

	protected Shell shell;
	private Message message;
	Display display = null;

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {

		this.message = message;
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(341, 213);
		shell.setText("Call Request");

		Group grpCallFrom = new Group(shell, SWT.NONE);
		grpCallFrom.setText("Call From :");
		grpCallFrom.setBounds(10, 18, 305, 116);

		String message = this.message.getMessage();
		Label lblMessage = new Label(grpCallFrom, SWT.NONE);
		lblMessage.setBounds(10, 63, 285, 43);
		lblMessage.setText(message);

		String from = this.message.getPeerInfo().getGroupName() + "/"
				+ this.message.getPeerInfo().getHost() + "/"
				+ this.message.getPeerInfo().getIpAddress();

		Label lblFrom = new Label(grpCallFrom, SWT.NONE);
		lblFrom.setAlignment(SWT.CENTER);
		lblFrom.setBounds(10, 22, 285, 28);
		lblFrom.setText(from);

		Button btnAcceptCall = new Button(shell, SWT.NONE);
		btnAcceptCall.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

				Message message = new Message();
				message.setStatus(PeerStatus.AUDIO_CALL_ACCEPT.ordinal());
				message.setPeerInfo(IpMsg.myInfo);

				try {

					System.out.println("ip"
							+ VoiceChat.callerInfo.getIpAddress());

					Utils.sendMessageUDP(message, false, InetAddress
							.getByName(VoiceChat.callerInfo.getIpAddress()));

					VoiceChat.stopRing();
					VoiceChat.stopTimer();
					new Thread(new StartCall(VoiceChat.callerInfo
							.getIpAddress())).start();
					// System.out.println("Window close");
					shell.close();
					// display.dispose();
					// closeWindow();
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Utils.disableCallButton();
						}
					});

				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		btnAcceptCall.setBounds(72, 140, 75, 25);
		btnAcceptCall.setText("Accept Call");

		Button btnRejectCall = new Button(shell, SWT.NONE);
		btnRejectCall.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Message message = new Message();
				message.setStatus(PeerStatus.AUDIO_CALL_REJECT.ordinal());
				message.setPeerInfo(IpMsg.myInfo);

				try {
					Utils.sendMessageUDP(message, false, InetAddress
							.getByName(VoiceChat.callerInfo.getIpAddress()));
					VoiceChat.stopRing();
					VoiceChat.stopTimer();

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Utils.enableCallButton();
						}
					});

					shell.close();

				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnRejectCall.setBounds(181, 140, 75, 25);
		btnRejectCall.setText("Reject Call");

	}

	public void closeWindow() {

		try {

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

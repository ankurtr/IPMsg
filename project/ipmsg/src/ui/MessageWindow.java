package ui;

import ipmsg.Message;
import ipmsg.PeerStatus;
import ipmsg.Utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map;

import listeners.FileServer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class MessageWindow {

	protected Shell shell;
	private Message message;
	private String senderInfo;
	private Table table;
	private Hashtable<String, String> files;
	private String fileNames = null;
	private String fnames[] = null;
	private static int currentFileProcessing = 0;

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
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
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(281, 324);
		shell.setText("IPMsg");

		final Button btnFiles = new Button(shell, SWT.NONE);

		files = message.getFiles();

		System.out.println(files);

		if (files != null) {
			// System.out.println(files.size() + files.toString());

			for (Map.Entry<String, String> element : files.entrySet()) {
				String key = element.getKey();

				if (fileNames != null) {
					fileNames = fileNames + ":" + key;
				} else {
					fileNames = key;
				}
			}

			// fnames = fileNames.split("[a-zA-Z0-9\\s\\-\\_]+\\.([a-z])+\\s");
			fnames = fileNames.split(":");

			// System.out.println(fnames.length);

			fileNames = fileNames.replace(":", " ");

			/*
			 * for(int j=0;j<fnames.length;j++) { System.out.println("fname[] "
			 * +fnames[j]); }
			 */
			// System.out.println("total filename" + fileNames);

			btnFiles.setText(fileNames);

		}
		final Button btnSealed = new Button(shell, SWT.TOGGLE);
		btnSealed.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

				btnSealed.setVisible(false);

				Message ackMessage = new Message();
				ackMessage.setStatus(PeerStatus.MESSAGE_ACKNOWLEDGEMENT
						.ordinal());
				ackMessage.setPeerInfo(message.getPeerInfo());

				// System.out.println(message.getPeerInfo().getIpAddress());

				try {
					Utils.sendMessageUDP(ackMessage, false, InetAddress
							.getByName(message.getPeerInfo().getIpAddress()));
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnSealed.setBounds(10, 135, 245, 145);
		btnSealed.setText("Sealed");
		btnSealed.setVisible(message.isSealed());

		Group grpMessageFrom = new Group(shell, SWT.NONE);
		grpMessageFrom.setText("Message from :");
		grpMessageFrom.setBounds(10, 10, 245, 82);

		Label lblMessageFrom = new Label(grpMessageFrom, SWT.NONE);
		lblMessageFrom.setAlignment(SWT.CENTER);
		lblMessageFrom.setBounds(10, 27, 225, 45);

		senderInfo = message.getPeerInfo().getGroupName() + "/"
				+ message.getPeerInfo().getHost() + ":"
				+ message.getTimestamp();
		lblMessageFrom.setText(senderInfo);

		Label lblMessage = new Label(shell, SWT.NONE);
		lblMessage.setBounds(10, 135, 245, 145);
		lblMessage.setText(message.getMessage());

		btnFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

				if (fileNames != null) {
					boolean done = false;

					FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);

					fileDialog.setFilterPath("C://Users//SiyaRam//Desktop");

					String filePath = fileDialog.open();

					if (filePath != null) {
						File f = new File(filePath);

						if (f.exists()) {
							MessageBox mb = new MessageBox(fileDialog
									.getParent(), SWT.ICON_WARNING | SWT.YES
									| SWT.NO);
							mb.setMessage(filePath
									+ "already exists. Do you want to replace it?");

							done = (mb.open() == SWT.YES);
						} else {
							done = true;
						}

						if (done) {

							Message m = new Message();
							m.setStatus(PeerStatus.FILE_DOWNLOAD_REQUEST
									.ordinal());
							m.setRequestedFile(files
									.get(fnames[currentFileProcessing]));
							m.setFileStorePath(filePath);
							m.setPeerInfo(IpMsg.myInfo);

							System.out.println("file message"
									+ fnames[currentFileProcessing]);

							try {
								// new FileThread(message.getPeerInfo()
								// .getIpAddress(), Utils.tcp_port,
								// filePath).start();

								Utils.sendMessageUDP(m, false, InetAddress
										.getByName(message.getPeerInfo()
												.getIpAddress()));
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							fileNames = fileNames
									.substring(currentFileProcessing < fnames.length - 1 ? fnames[currentFileProcessing]
											.length() + 1 : 0);
							currentFileProcessing++;

							if (currentFileProcessing >= fnames.length) {
								fileNames = null;
							}

							btnFiles.setText(fileNames != null ? fileNames : "");

						}
					}
				}
			}
		});
		btnFiles.setBounds(10, 98, 245, 25);

		Label lblCover = new Label(shell, SWT.NONE);
		lblCover.setBounds(10, 109, 245, 25);

	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
}

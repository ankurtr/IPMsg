package ui;

import ipmsg.Message;
import ipmsg.PeerInfo;
import ipmsg.PeerStatus;
import ipmsg.Utils;
import ipmsg.VoiceChat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class IpMsg {

	protected Shell shell;
	public static Table table;
	private Text text;

	// app variables
	public static Hashtable<String, PeerInfo> connectedPeers = new Hashtable<String, PeerInfo>();
	public static PeerInfo myInfo;
	private Hashtable<String, byte[]> files = new Hashtable<String, byte[]>();
	private Message message;
	private Button btnCheckButton;
	public static Button btnCall;
	public static Button btnEndCall;
	private Label lblBack;
	private String fileNames = null;
	public static Button btnToolTip;
	private Display display;
	public static Label lblMembers;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			// create UI
			IpMsg window = new IpMsg();
			window.open();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();

		// fill data into grid

		// initiate Utils class because it has to load all the application
		// settings
		Utils u = new Utils();
		Utils.initializeApp();

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
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {

				Utils.stopApp();

			}
		});
		shell.setSize(522, 486);
		shell.setText("IPMsg");

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {

				int i = table.getSelectionIndex();
				TableItem item = table.getItem(i);

				String group_name = item.getText(1);

				for (int j = 0; j < table.getItemCount(); j++) {

					TableItem dummyItem = table.getItem(j);
					if (dummyItem.getText(1).equals(group_name)) {
						table.select(j);
					}

				}

			}
		});
		table.setHeaderVisible(true);
		table.setBounds(0, 10, 402, 242);
		table.setLinesVisible(true);

		TableColumn tblclmnUer = new TableColumn(table, SWT.NONE);
		tblclmnUer.setText("User");
		tblclmnUer.setWidth(100);

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("Group");

		TableColumn tblclmnHost = new TableColumn(table, SWT.NONE);
		tblclmnHost.setWidth(100);
		tblclmnHost.setText("Host");

		TableColumn tblclmnIpAddress = new TableColumn(table, SWT.NONE);
		tblclmnIpAddress.setWidth(100);
		tblclmnIpAddress.setText("IP Address");

		text = new Text(shell, SWT.BORDER);
		text.setBounds(10, 292, 392, 115);

		Button btnSend = new Button(shell, SWT.NONE);
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

				sendMessage();

			}
		});
		btnSend.setBounds(157, 413, 75, 25);
		btnSend.setText("Send");

		btnCheckButton = new Button(shell, SWT.CHECK);
		btnCheckButton.setBounds(269, 417, 93, 16);
		btnCheckButton.setText("sealed");

		lblMembers = new Label(shell, SWT.BORDER);
		lblMembers.setAlignment(SWT.CENTER);
		lblMembers.setBounds(417, 40, 52, 44);
		lblMembers.setText("Member\n"+ connectedPeers.size());		

		Button btnRefresh = new Button(shell, SWT.NONE);
		btnRefresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

				table.removeAll();
				fillData();
			}
		});

		btnRefresh.setBounds(408, 90, 75, 25);
		btnRefresh.setText("Refresh");

		// table_1 = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		// table_1.setBounds(10, 256, 392, 30);
		// table_1.setHeaderVisible(true);
		// table_1.setLinesVisible(true);

		btnCall = new Button(shell, SWT.NONE);

		btnCall.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// JOptionPane.showMessageDialog(null, "Message sent");
				Message callRequestMessage = new Message();

				callRequestMessage.setStatus(PeerStatus.AUDIO_CALL_REQUEST
						.ordinal());
				callRequestMessage.setPeerInfo(myInfo);
				callRequestMessage.setMessage(text.getText());

				if (table.getSelectionCount() == 1) {

					TableItem selectedUser = table.getItem(table
							.getSelectionIndex());

					PeerInfo toWhomIAmCalling = new PeerInfo(selectedUser
							.getText(0), selectedUser.getText(1), selectedUser
							.getText(2), selectedUser.getText(3));
					VoiceChat.callerInfo = toWhomIAmCalling;
					VoiceChat.userBusy=true;

					try {
						Utils.sendMessageUDP(callRequestMessage, false,
								InetAddress.getByName(selectedUser.getText(3)));

						VoiceChat.startTimer();

						Utils.disableCallButton();

						VoiceChat.startRing();

						Utils.enableEndCallButton();

					} catch (Exception error) {
						JOptionPane.showMessageDialog(null,
								"Error in cooncetion");
					}

				} else {
					JOptionPane.showMessageDialog(null, "Select only one user");
				}
			}
		});

		btnCall.setBounds(418, 121, 59, 44);
		btnCall.setText("Call");

		btnEndCall = new Button(shell, SWT.NONE);
		btnEndCall.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub

				Utils.endCall();

				Utils.enableCallButton();

				Utils.disableEndCallButton();

			}

		});
		btnEndCall.setBounds(418, 171, 59, 44);
		btnEndCall.setText("End Call");
		btnEndCall.setEnabled(false);

		DropTarget dropTarget = new DropTarget(shell, DND.DROP_MOVE);

		dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });

		final Button btnFiles = new Button(shell, SWT.NONE);
		btnFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {

			}
		});
		btnFiles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnFiles.setBounds(20, 261, 375, 25);

		lblBack = new Label(shell, SWT.BORDER);
		lblBack.setBounds(10, 258, 392, 32);

		dropTarget.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				String fileList[] = null;

				FileTransfer ft = FileTransfer.getInstance();
				if (ft.isSupportedType(event.currentDataType)) {
					fileList = (String[]) event.data;

					for (int i = 0; i < fileList.length; i++) {
						// TableColumn col1 = new TableColumn(table_1,
						// SWT.Activate);
						// col1.setWidth(100);
						// System.out.println(fileList[i]);

						// btnFiles.setVisible(true);

						File f = new File(fileList[i]);

						// JOptionPane.showMessageDialog(null, fileList[i]);

						if (fileNames != null
								&& !fileNames.contains(f.getName())) {
							fileNames = fileNames + " " + f.getName();
						} else {
							fileNames = f.getName();
						}

						// System.out.println(fileNames);

						btnFiles.setText(fileNames);

						// col1.setText(f.getName());

						byte[] f_bytes = new byte[(int) f.length()];

						BufferedInputStream br;
						try {
							br = new BufferedInputStream(new FileInputStream(f));
							br.read(f_bytes);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						files.put(f.getName(), f_bytes);
					}
				}
			}
		});

		final ToolTip tip = new ToolTip(shell, SWT.BALLOON
				| SWT.ICON_INFORMATION);

		Tray tray = display.getSystemTray();
		tip.setMessage("Here is a message for the user. When the message is too long it wraps. I should say something cool but nothing comes to my mind.");
		if (tray != null) {
			TrayItem item = new TrayItem(tray, SWT.NONE);
			item.setImage(new Image(display, "Sound/type_mode.gif"));
			tip.setText("Notification from a tray item");
			item.setToolTip(tip);
		} else {
			tip.setText("Notification from anywhere");
			tip.setLocation(400, 400);
		}
		btnToolTip = new Button(shell, SWT.PUSH);
		btnToolTip.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		btnToolTip.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(null, "tooltip jjjj");
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						tip.setMessage("Message received");
						tip.setVisible(true);
					}
				});
			}
		});

	}

	public static void fillData() {
		Hashtable<String, PeerInfo> connectedPeers = IpMsg.connectedPeers;

		// System.out.println(connectedPeers);
		table.removeAll();
		int i = 0;
		for (Map.Entry<String, PeerInfo> peerEntry : connectedPeers.entrySet()) {
			PeerInfo peer = peerEntry.getValue();

			TableItem item = new TableItem(table, SWT.NONE);

			item.setText(new String[] { peer.getUsername(),
					peer.getGroupName(), peer.getHost(), peer.getIpAddress() });

			// System.out.println(i++);
		}
	}

	private void sendMessage() {
		message = new Message();
		message.setMessage(text.getText());
		message.setStatus(PeerStatus.MESSAGE.ordinal());
		message.setPeerInfo(myInfo);
		// message.setTimestamp(new Date());
		message.setSealed(btnCheckButton.getSelection());
		message.setFiles(files);

		TableItem[] selectedItems = table.getSelection();

		if (selectedItems.length > 0) {

			InetAddress[] addresses = new InetAddress[selectedItems.length];
			for (int i = 0; i < selectedItems.length; i++) {
				try {
					addresses[i] = InetAddress.getByName(selectedItems[i]
							.getText(3));

					// System.out.println(selectedItems[i].getText(3));
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			// System.out.println(files);
			if (files != null) {
				message.setFiles(files);
				Utils.sendMessageTCP(message, addresses);
			} else {
				Utils.sendMessageUDP(message, false, addresses);
			}

			// System.out.println(message.getMessage());

			files = null;
		} else {
			MessageBox m = new MessageBox(shell);
			m.setMessage("Please Select Atleast 1 person to send");
			int i = m.open();
		}
	}

}

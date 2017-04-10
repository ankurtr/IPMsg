



import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;



public class VideoChat {

	protected Shell shell;
	private Text text;
	private Button btnNewButton_1;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			VideoChat window = new VideoChat();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
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
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(53, 69, 161, 46);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				//JOptionPane.showMessageDialog(null, text.getText());
				Thread th=new Thread(new voiceChat(text.getText()));
				th.start();
			}
		});
		btnNewButton.setBounds(72, 163, 108, 40);
		btnNewButton.setText("start chat");
		
		btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				VoiceChat.flag=false;
				VoiceChat.recFlag=false;
			}
		});
		btnNewButton_1.setBounds(220, 167, 75, 25);
		btnNewButton_1.setText("Stop");

	}
	
	
	
	public class voiceChat extends Thread{
		String ipAddress;
		public voiceChat(String address) {
			// TODO Auto-generated constructor stub
			ipAddress=address;
		}
		
		public void run(){
			VoiceChat obj=new VoiceChat();
			obj.startChat(ipAddress);
			
		}
	}
}

package listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class FileListener implements Listener {

	private int i;
	private Shell shell;
	private byte[] file;

	public FileListener(byte[] file) {
		// super();
		this.i = i;
		this.file = file;
	}

	@Override
	public void handleEvent(Event arg0) {

		boolean done = false;

		// TODO Auto-generated method stub
		Display.getDefault();
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		String fileName = fileDialog.getFileName();

		File f = new File(fileName);

		if (f.exists()) {
			MessageBox mb = new MessageBox(fileDialog.getParent(),
					SWT.ICON_WARNING | SWT.YES | SWT.NO);
			mb.setMessage(fileName
					+ "already exists. Do you want to replace it?");

			done = (mb.open() == SWT.YES);
		} else {
			done = true;
		}

		if (done) {

			FileOutputStream fos = null;
			ObjectOutputStream oos = null;

			try {
				fos = new FileOutputStream(f);
				fos.write(file);
				fos.flush();
				fos.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

}

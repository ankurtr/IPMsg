package ipmsg;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;

	private Hashtable<String, byte[]> files;

	private PeerInfo peerInfo;

	private Date timestamp;

	private int status;
	
	private boolean isSealed;

	public PeerInfo getPeerInfo() {
		return peerInfo;
	}

	public boolean isSealed() {
		return isSealed;
	}

	public void setSealed(boolean isSealed) {
		this.isSealed = isSealed;
	}

	public void setPeerInfo(PeerInfo peerInfo) {
		this.peerInfo = peerInfo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	private boolean isRead;

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Hashtable<String, byte[]> getFiles() {
		return files;
	}

	public void setFiles(Hashtable<String, byte[]> files) {
		this.files = files;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Message() {
		super();
		this.timestamp=new Date();		
	}
	
	

}

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

	private Hashtable<String, String> files;

	private PeerInfo peerInfo;

	private Date timestamp;

	private int status;

	private boolean isSealed;

	private String requestedFile;

	private String fileStorePath;

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

	public Hashtable<String, String> getFiles() {
		return files;
	}

	public void setFiles(Hashtable<String, String> files) {
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
		this.timestamp = new Date();
	}

	public String getRequestedFile() {
		return requestedFile;
	}

	public void setRequestedFile(String requestedFile) {
		this.requestedFile = requestedFile;
	}

	public String getFileStorePath() {
		return fileStorePath;
	}

	public void setFileStorePath(String fileStorePath) {
		this.fileStorePath = fileStorePath;
	}

	@Override
	public String toString() {
		return "Message [message=" + message + ", files=" + files
				+ ", peerInfo=" + peerInfo + ", timestamp=" + timestamp
				+ ", status=" + status + ", isSealed=" + isSealed
				+ ", requestedFile=" + requestedFile + ", fileStorePath="
				+ fileStorePath + ", isRead=" + isRead + "]";
	}

}

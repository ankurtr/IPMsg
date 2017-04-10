package ipmsg;

import java.io.Serializable;

public class PeerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String groupName;
	private String host;
	private String ipAddress;

	public PeerInfo() {
	}

	public PeerInfo(String username, String groupName, String host,
			String ipAddress) {
		super();
		this.username = username;
		this.groupName = groupName;
		this.host = host;
		this.ipAddress = ipAddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public String toString() {
		return "PeerInfo [username=" + username + ", groupName=" + groupName
				+ ", host=" + host + ", ipAddress=" + ipAddress + "]";
	}
	

}

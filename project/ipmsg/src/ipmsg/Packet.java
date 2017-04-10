package ipmsg;

import java.util.Arrays;

public class Packet {
	
	private byte[] data;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Packet [data=" + Arrays.toString(data) + "]";
	}

}

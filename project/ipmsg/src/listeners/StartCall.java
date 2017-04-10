package listeners;

import ipmsg.VoiceChat;


public class StartCall extends Thread {
		String ipAddress;

		public StartCall(String add) {
			// TODO Auto-generated constructor stub
			ipAddress = add;
		}

		public void run() {
			VoiceChat vc = new VoiceChat();
			vc.startChat(ipAddress);
		}
	}
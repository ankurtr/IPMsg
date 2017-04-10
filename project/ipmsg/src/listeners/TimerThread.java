package listeners;

import ipmsg.Utils;
import ipmsg.VoiceChat;

import java.util.TimerTask;

public class TimerThread extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			VoiceChat.userBusy=false;
			
			Utils.enableCallButton();
			
			VoiceChat.timer.cancel();
			
			VoiceChat.stopRing();
			
		}
		
	}
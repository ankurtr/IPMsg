package ipmsg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import listeners.TimerThread;

/**
 * 
 * @author Meherzad
 */

public class VoiceChat {

	public static Boolean userBusy = false;
	public static Timer timer = null;
	public static PeerInfo callerInfo;

	public static Boolean sendflag = true;
	public static Boolean recFlag = true;
	DatagramSocket UDPSocket = null;
	AudioFormat format = null;
	TargetDataLine microphone = null;
	byte[] buffer = null;
	DatagramPacket UDPPacket = null;
	private static Player ringPlayer;

	// TargetDataLine mic=null;

	public VoiceChat()
	{
		sendflag=true;
		recFlag=true;
	}
	
	public void startChat(String ipAddress) {
		try {

			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			buffer = new byte[1000];

			UDPSocket = new DatagramSocket(1987);
			System.out.println(" aaaaaaaa");
			Thread th = new Thread(new Listener());
			th.start();
			microphone = AudioSystem.getTargetDataLine(format);
			format = new AudioFormat(8000.0f, 16, 1, true, true);
			microphone.open(format);
			microphone.start();
			// System.out.println(" ddddddddd");
			while (sendflag) {
				microphone.read(buffer, 0, buffer.length);
				// JOptionPane.showMessageDialog(null,
				// InetAddress.getByName(ipAddress));
				UDPPacket = new DatagramPacket(buffer, buffer.length,
						InetAddress.getByName(ipAddress), 1988);
				UDPSocket.send(UDPPacket);
				// System.out.println("cccccccccccc");
			}

			microphone.close();
			UDPSocket.close();
		} catch (Exception e) {
			System.out.println(" ssss " + e.getMessage());

		}
	}

	public static void stopTimer() {
		timer.cancel();
		timer=null;
	}

	public static void startTimer() {
		timer = new Timer();
		timer.schedule(new TimerThread(), 60000);
	}

	public static void startRing() {
		FileInputStream fileIS;
		try {
			fileIS = new FileInputStream(new File(Utils.soundFile));
			ringPlayer = new Player(fileIS);
			ringPlayer.play();

		} catch (JavaLayerException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void stopRing() {
		ringPlayer.close();
	}

	public class Listener extends Thread {
		byte[] buff = new byte[1000];
		DatagramSocket UDPSocket1 = null;
		DatagramPacket recPacket = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine line = null;

		// Clip soundPlay=null;
		@Override
		public void run() {

			try {
				UDPSocket1 = new DatagramSocket(1988);
				format = new AudioFormat(8000.0f, 16, 1, true, true);

				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(format);
				line.start();
				// System.out.println("bbbbbbbbbbbb");				
			} catch (Exception e) {
				System.out.println("list " + e.getMessage());
			}

			recPacket = new DatagramPacket(buff, buff.length);
			while (recFlag) {
				try {
					UDPSocket1.receive(recPacket);
					buff = (byte[]) recPacket.getData();
					line.write(buff, 0, buff.length);
					// System.out.println(" -----aaaaaaaa");

					// soundPlay.open(format, buff, 0, buff.length);
				} catch (Exception e) {
					System.out.println("errr " + e.getMessage());

				}

			}
			UDPSocket1.close();
			line.drain();
			line.close();

		}
	}
}

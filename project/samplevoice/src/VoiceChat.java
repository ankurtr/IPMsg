

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

/**
 *
 * @author Meherzad
 */



public class VoiceChat {
    public static Boolean flag= true;
    public static Boolean recFlag=true;
    DatagramSocket UDPSocket=null;
     AudioFormat format = null;
     TargetDataLine microphone=null;
     byte[] buffer=null;
     DatagramPacket UDPPacket=null;
     //TargetDataLine mic=null;
     public static boolean f=false;
     
     
     
     public void startChat(String ipAddress){
        try{
            buffer = new byte[1000];
            
            UDPSocket=new DatagramSocket(1987);
            System.out.println(" aaaaaaaa");
            if (f==false){
            	Thread th=new Thread(new Listener());
            	th.start();
            }
            f=true;
            microphone = AudioSystem.getTargetDataLine(format);
            format=  new AudioFormat(8000.0f, 16, 1, true, true);
            microphone.open(format);
            microphone.start();
            System.out.println(" ddddddddd");
            while (flag) {                
                microphone.read(buffer, 0, buffer.length);
               // JOptionPane.showMessageDialog(null, InetAddress.getByName(ipAddress));
                UDPPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ipAddress), 1988);
                UDPSocket.send(UDPPacket);
                System.out.println("cccccccccccc");
            }
             
        }
        catch(Exception e){
            System.out.println(" ssss "+e.getMessage());
                    
        }
     }
     
     
    
     
     
     public class Listener extends Thread{
       byte[] buff=new byte[1000];
       DatagramSocket UDPSocket1=null;
       DatagramPacket recPacket=null;
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
       SourceDataLine line=null;
       
       //Clip soundPlay=null;
         @Override
         public void run(){
        
             try{
            	 UDPSocket1=new DatagramSocket(1988);
                format=  new AudioFormat(8000.0f, 16, 1, true, true);

                line = (SourceDataLine)   AudioSystem.getLine(info);
                line.open(format);
                line.start();
                System.out.println("bbbbbbbbbbbb");
            }
            catch(Exception e){
                System.out.println("list "+ e.getMessage());
            }
      
             recPacket=new DatagramPacket(buff, buff.length);
             while(recFlag){
                 try{
                    UDPSocket1.receive(recPacket);
                    buff = (byte[])recPacket.getData();
                    line.write(buff, 0, buff.length);
                    System.out.println(" -----aaaaaaaa");

                    //soundPlay.open(format, buff, 0, buff.length);
                 }
                 catch(Exception e){
                     System.out.println("errr "+e.getMessage());
                             
                 }
                 
              }
                line.drain();
                 line.close();
             
         }
     }
}

import java.net.DatagramPacket;
import edu.utulsa.unet.UDPSocket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class udpsocketdemo {
	public static void main(String[] args)
	{	
	    	int localPort=Integer.parseInt(args[0]);
	        String serverName=args[1];
		int serverPort=Integer.parseInt(args[2]);
		int numpackets=100;
		try {
			UDPSocket socket = new UDPSocket(localPort);
			//DatagramSocket socket = new DatagramSocket(localPort);
			for (int i=0; i<numpackets; i++){
				
			byte [] buffer = (i+" Hello World").getBytes();
			socket.send(new DatagramPacket(buffer, buffer.length,
 				InetAddress.getByName(serverName), serverPort));
			}
		}
		catch(Exception e){ e.printStackTrace(); }
	}
}

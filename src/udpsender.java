import java.net.DatagramPacket;
import edu.utulsa.unet.UDPSocket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class udpsender {

	public static void main(String[] args)
	{
	  int localPort=Integer.parseInt(args[0]);
	  String serverName=args[1];
	  int serverPort=Integer.parseInt(args[2]);
		try {

			byte [] buffer = ("Hello World- or rather Mauricio saying hello through UDP").getBytes();
			UDPSocket socket = new UDPSocket(localPort);
			//DatagramSocket socket = new DatagramSocket(23456);
			socket.send(new DatagramPacket(buffer, buffer.length,
 				InetAddress.getByName(serverName), serverPort));
		}
		catch(Exception e){ e.printStackTrace(); }
	}
}

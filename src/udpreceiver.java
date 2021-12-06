import java.io.DataInputStream;
import java.net.DatagramPacket;
import edu.utulsa.unet.UDPSocket; //import java.net.DatagramSocket;
import java.net.InetAddress;

public class udpreceiver {
	public static void main(String[] args){
	    int localPort=Integer.parseInt(args[0]);
		try{
	 		UDPSocket socket = new UDPSocket(localPort);
		    while(true){
			byte [] buffer = new byte[80];
			DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
			socket.receive(packet);
			InetAddress client = packet.getAddress();
			System.out.println(" Received'"+new String(buffer)+"' from " 
			+client.getHostAddress()+" with sender port "+packet.getPort());
			socket.close();
			}
		}
		catch(Exception e){ e.printStackTrace(); }
	}
}

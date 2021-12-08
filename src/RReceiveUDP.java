import edu.utulsa.unet.RReceiveUDPI;
import edu.utulsa.unet.UDPSocket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

public class RReceiveUDP implements RReceiveUDPI{
    private String filename;
    private int sequence;
    private int mode; // zero: stop-and-wait, nonzero: slide window 
    private UDPSocket socket; // socket of receiver
    private int window; // size of window
    private InetAddress address;
    private Hashtable<Integer,RPacket> unanswer;
    private int remotePort;
    private int file_length;
    private ArrayList<RPacket> file_list;

    public RReceiveUDP(short port) throws SocketException
    {
        filename=null;
        sequence=0;
        mode=0;
        socket=new UDPSocket(port);
        window=256;
        address=null;
        unanswer=new Hashtable<Integer,RPacket>();
        file_list=new ArrayList<RPacket>();
    }
    @Override
    public String getFilename() {
        // TODO Auto-generated method stub
        return filename;
    }

    @Override
    public int getLocalPort() {
        // TODO Auto-generated method stub
        return socket.getLocalPort();
    }

    @Override
    public int getMode() {
        // TODO Auto-generated method stub
        return mode;
    }

    @Override
    public long getModeParameter() {
        // TODO Auto-generated method stub
        return window;
    }
    public RPacket receive(){
        RPacket rpacket=null;
        byte[] header_buffer=new byte[RPacket.HEADER_LENGTH];
        DatagramPacket header_packet=new DatagramPacket(header_buffer,header_buffer.length);
        try {
            socket.receive(header_packet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
        rpacket=new RPacket(header_buffer);
        address=header_packet.getAddress();
        remotePort=header_packet.getPort();
        if(rpacket.length>0){
            byte[] data_buffer=new byte[rpacket.length];
            DatagramPacket data_packet=new DatagramPacket(data_buffer, data_buffer.length)
            socket.receive(data_packet);
            rpacket.data=data_packet.getData();
        }
        return rpacket;
    }
    public void send(RPacket rpacket){
        byte[] bytes=rpacket.getBytes();
        DatagramPacket packet=new DatagramPacket(bytes, bytes.length,address,remotePort);
        try {
            socket.send(packet);
            sequence++;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public boolean handle(RPacket rpacket){
        RPacket send_rpacket=null;
        for(Map.Entry<Integer,RPacket> entry:unanswer.entrySet()){
            if(entry.getValue().timeout<=0){
                send(entry.getValue());
            }
            else{
                entry.getValue().timeout--;
            }
        }
        switch(rpacket.type)
        {
            case RPacket.TYPE_SYN:
                file_length=rpacket.file_length;
                send_rpacket=new RPacket(sequence, 0, 0, 0, 0, RPacket.TYPE_SYN_ACK);
                send(send_rpacket);
                unanswer.put(sequence,send_rpacket);
                break;
            case RPacket.TYPE_ACK:
                if(unanswer.containsKey(rpacket.sequence)){
                    unanswer.remove(rpacket.sequence);
                }
                break;
            case RPacket.TYPE_TRSP:
                file_list.add(rpacket);
                send_rpacket=new RPacket(sequence, rpacket.sequence, 0, 0, 0, RPacket.TYPE_ACK);
                send(send_rpacket);
                break;
            case RPacket.TYPE_TERMI:
                socket.close();
            default:
                return false;
        }
        if(send_rpacket!=null){
            send(send_rpacket);
        }
        return true;
    }
    @Override
    public boolean receiveFile() {
        // TODO Auto-generated method stu
        RPacket rpacket;
        boolean flag=true;
        while((rpacket=receive())!=null && flag){
            flag=handle(rpacket);
        }
        return true;
    }

    @Override
    public void setFilename(String filename) {
        // TODO Auto-generated method stub
        this.filename=filename;
    }

    @Override
    public boolean setLocalPort(int port) {
        // TODO Auto-generated method stub
        socket.close();
        try {
            socket=new UDPSocket(port);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            return false;
        }
        return true;
    }

    @Override
    public boolean setMode(int mode) {
        // TODO Auto-generated method stub
        this.mode=mode;
        return true;
    }

    @Override
    public boolean setModeParameter(long widow) {
        // TODO Auto-generated method stub
        this.window=(int)window;
        return true;
    }
}

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.net.InetAddress;
import edu.utulsa.unet.UDPSocket;
import edu.utulsa.unet.RSendUDPI;

class RSendUDP implements RSendUDPI {
    public String filename;
    public int localPort;
    public int serverPort;
    public String serverName;
    public int mode;
    public int window=4;
    public InetSocketAddress receiver;
    public int timeout;
    public int remotePort;
    public InetAddress remoteAddress;
    public UDPSocket socket;
    public int sequence=0;
    public int offset=0;
    public Hashtable<Integer,RPacket> unanswer=new Hashtable<Integer,RPacket>();
    public RSendUDP(String serverName,int serverPort){
        this.serverName=serverName;
        this.serverPort=serverPort;
    }
    @Override
    public String getFilename() {
        // TODO Auto-generated method stub
        return filename;
    }

    @Override
    public int getLocalPort() {
        // TODO Auto-generated method stub
        return localPort;
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

    @Override
    public InetSocketAddress getReceiver() {
        // TODO Auto-generated method stub
        return receiver;
    }

    @Override
    public long getTimeout() {
        // TODO Auto-generated method stub
        return timeout;
    }
    public RPacket receive() throws IOException{
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
        remoteAddress=header_packet.getAddress();
        remotePort=header_packet.getPort();
        if(rpacket.length>0){
            byte[] data_buffer=new byte[rpacket.length];
            DatagramPacket data_packet=new DatagramPacket(data_buffer, data_buffer.length);
            socket.receive(data_packet);
            rpacket.data=data_packet.getData();
        }
        return rpacket;
    }
    public void send(RPacket rpacket){
        byte[] bytes=rpacket.getBytes();
        DatagramPacket packet=new DatagramPacket(bytes, bytes.length,remoteAddress,remotePort);
        try {
            socket.send(packet);
            sequence++;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public boolean sendFile() {
        // TODO Auto-generated method stub
        File file=new File(filename);
        byte[] fileBytes=new byte[(int)file.length()];
        try {
            FileInputStream input=new FileInputStream(file);
            input.read(fileBytes);
            input.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
        if(!file.canRead()){
            return false;
        }

            RPacket syn=new RPacket(sequence, 0, (int)file.length(), 0, 0, RPacket.TYPE_SYN);
            send(syn);
            RPacket syn_ack;
            try {
                syn_ack = receive();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            while(syn_ack.type!=RPacket.TYPE_SYN_ACK || syn_ack.acknowledge!=syn.sequence){
                send(syn);
                try {
                    syn_ack=receive();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.exit(1);
                }
            RPacket syn_ack_ack=new RPacket(sequence, syn_ack.sequence, 0, 0, 0, RPacket.TYPE_ACK);
            send(syn_ack_ack);
            if (mode == 0) {
                while(offset<file.length()){
                    int length=Math.min(RPacket.MTU,(int)file.length()-offset);
                    RPacket fileData=new RPacket(sequence, 0, 0, offset, length, RPacket.TYPE_TRSP);
                    fileData.data=new byte[length];
                    for(int i=0;i<file.length();i++){
                        fileData.data[i]=fileBytes[offset+i];
                    }
                    send(fileData);
                    try {
                        RPacket fileAck=receive();
                        if(fileAck.type!=RPacket.TYPE_ACK || fileAck.acknowledge!=fileData.sequence){
                            sequence--;
                            continue;
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        sequence--;
                        continue;
                    }
                    offset+=length;
                }
            }else{
                while(offset<file.length()){
                    int length=Math.min(RPacket.MTU,(int)file.length()-offset);
                    RPacket fileData=new RPacket(sequence, 0, 0, offset, length, RPacket.TYPE_TRSP);
                    fileData.data=new byte[length];
                    for(int i=0;i<file.length();i++){
                        fileData.data[i]=fileBytes[offset+i];
                    }
                    send(fileData);
                    unanswer.put(fileData.sequence,fileData);
                    while(unanswer.size()>=window){
                        try {
                            RPacket rpacket=receive();
                            if(unanswer.containsKey(rpacket.acknowledge) && rpacket.type==RPacket.TYPE_ACK){
                                unanswer.remove(rpacket.acknowledge);
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            System.exit(1);
                        }

                    }
                    offset+=length;
                }
            }
        }
        return true;
    }

    @Override
    public void setFilename(String fileName) {
        // TODO Auto-generated method stub
        this.filename=fileName;
    }

    @Override
    public boolean setLocalPort(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setMode(int mode) {
        // TODO Auto-generated method stub
        this.mode=mode;
        return true;
    }

    @Override
    public boolean setModeParameter(long parameter) {
        // TODO Auto-generated method stub
        window=(int)parameter;
        return true;
    }

    @Override
    public boolean setReceiver(InetSocketAddress receiver) {
        // TODO Auto-generated method stub
        this.receiver=receiver;
        return true;
    }

    @Override
    public boolean setTimeout(long timeout) {
        // TODO Auto-generated method stub
        this.timeout=(int)timeout;
        return true;
    }

}
import java.net.InetAddress;

public class RPacket {
    public final static int HEADER_LENGTH=24;
    public final static int MTU=300;
    public final static int TYPE_SYN=0;
    public final static int TYPE_SYN_ACK=1;
    public final static int TYPE_ACK=2;
    public final static int TYPE_TRSP=3;
    public final static int TYPE_TERMI=4;
    public int sequence; // sequence number
    public int acknowledge; // acknowledge number
    public int file_length; // length of file 
    public int offset; // file offset
    public int length; // length of data
    public int type; // type of packet. 0:SYN, 1: SYN_ACK, 2:ACK, 3: TRSP, 4: TERMI
    public byte[] data;
    public int timeout=10;
    public InetAddress hostAddress;
    public RPacket(byte[] header)
    {
        RBytes header_bytes=new RBytes(header);
        sequence=header_bytes.popInt();
        acknowledge=header_bytes.popInt();
        file_length=header_bytes.popInt();
        offset=header_bytes.popInt();
        length=header_bytes.popInt();
        type=header_bytes.popInt();
    }
    public RPacket(int sequence,int acknowledge,int file_length,int offset,int length,int type){
        this.sequence=sequence;
        this.acknowledge=acknowledge;
        this.file_length=file_length;
        this.offset=offset;
        this.length=length;
        this.type=type;
    }
    public byte[] getBytes(){
        RBytes bytes;
        if(data==null){
            bytes=new RBytes(HEADER_LENGTH);
        }
        else{
            bytes=new RBytes(HEADER_LENGTH+data.length);
        }
        bytes.push(sequence);
        bytes.push(acknowledge);
        bytes.push(file_length);
        bytes.push(offset);
        bytes.push(length);
        bytes.push(type);
        if(data!=null){
            bytes.push(data);
        }
        return bytes.getBytes();
    }
}

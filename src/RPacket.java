public class RPacket {
    public short sport; // source port
    public short dport; // destination port
    public int sequence; // sequence number
    public int ackownledge; // ackownledge number
    public int window; // size of window
    public int offset; // file offset
    public int length; // length of data
    public short type; // type of packet
    public byte[] data;
    public byte[] getBytes(){
        
    }
}

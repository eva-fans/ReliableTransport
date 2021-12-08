public class RBytes {
    public int offset;
    public byte[] bytes;
    public RBytes(int size) // size: size of bytes
    {
        offset=0;
        bytes=new byte[size];
    }
    public RBytes(byte[] bytes){
        offset=0;
        this.bytes=bytes;
    }
    public byte[] getBytes()
    {
        return bytes;
    }
    public void push(byte fill)
    {
        bytes[offset]=fill;
        offset++;
    }
    public byte popByte()
    {
        byte temp=bytes[offset];
        offset++;
        return temp;
    }
    public void push(int fill)
    {
        push((byte)(fill >> 24 & 0xff));
        push((byte)(fill >> 16 & 0xff));
        push((byte)(fill >> 8 & 0xff));
        push((byte)(fill & 0xff));
    }
    public int popInt()
    {
        byte b3=popByte();
        byte b2=popByte();
        byte b1=popByte();
        byte b0=popByte();
        return b3 << 24 |
         b2 << 16 |
          b1 << 8 |
           b0;
    }
    public void push(short fill)
    {
        push((byte)(fill >> 8 & 0xff));
        push((byte)(fill & 0xff));
    }
    public short popShort()
    {
        byte b1=popByte();
        byte b0=popByte();
        return (short)
        ((b1 << 8) |
        (b0));
    }
    public void push(byte[] fill)
    {
        for(int i=0;i<fill.length;i++)
        {
            push(fill[i]);
        }
    }
    public byte[] popBytes(int size){
        byte[] bytes=new byte[size];
        for(int i=0;i<size;i++){
            bytes[i]=popByte();
        }
        return bytes;
    }
}

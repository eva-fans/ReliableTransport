import edu.utulsa.unet.RReceiveUDPI;
import edu.utulsa.unet.UDPSocket;
import java.net.DatagramPacket;

public class RReceiveUDP implements RReceiveUDPI{
    private String filename=null;
    private int mode=0;
    private UDPSocket socket;
    @Override
    public String getFilename() {
        // TODO Auto-generated method stub
        
        return filename;
    }

    @Override
    public int getLocalPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getModeParameter() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean receiveFile() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setFilename(String filename) {
        // TODO Auto-generated method stub
        this.filename=filename;
    }

    @Override
    public boolean setLocalPort(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setMode(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setModeParameter(long arg0) {
        // TODO Auto-generated method stub
        return false;
    }
    
}

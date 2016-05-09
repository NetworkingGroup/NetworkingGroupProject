package networking.hw3;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class DataPacket extends Thread{

    private DatagramSocket socket;
    private DatagramPacket packet;
    private int chunkIndex;

    public DataPacket(DatagramSocket socket, DatagramPacket packet, int chunkIndex){
        this.socket = socket;

        this.packet = packet;

        this.chunkIndex = chunkIndex;
    }

    public void run(){

        for (;;) {
            try {
                byte [] b = new byte[4];

                DatagramPacket ack = new DatagramPacket(b, b.length);

                socket.setSoTimeout(1000);

                socket.send(packet);

                socket.receive(ack);

                b = ack.getData();

                int index = java.nio.ByteBuffer.wrap(b).getInt();

                if (index == chunkIndex){
                    break;
                }

            } catch (SocketTimeoutException ste) {

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}

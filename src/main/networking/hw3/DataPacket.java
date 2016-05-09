package networking.hw3;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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

                socket.send(packet);

                Thread.sleep(750);

                socket.receive(ack);

                b = ack.getData();

                int index = java.nio.ByteBuffer.wrap(b).getInt();

                if (index == chunkIndex){
                    break;
                }

            } catch (InterruptedException IE) {
                break;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}

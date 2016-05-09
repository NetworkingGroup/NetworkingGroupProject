package networking.hw3;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

/**
 * The class takes in a packet, which is sent indefinitely until an ack
 * is received and compared to the data that was sent. This uses a thread
 * in order to run continuously while not blocking any other code.
 */
public class DataPacket extends Thread{

    private DatagramSocket socket;
    private DatagramPacket packet;
    private int chunkIndex;

    /**
     * initializes the variables accordingly
     * @param socket Datagram socket assigned to send to the graph server
     * @param packet Pre-made data packet packed with index number and data
     * @param chunkIndex Index of the chunk that has been worked on
     */
    public DataPacket(DatagramSocket socket, DatagramPacket packet, int chunkIndex){
        this.socket = socket;

        this.packet = packet;

        this.chunkIndex = chunkIndex;
    }

    /**
     * Thread sends the packet that it was initialized with and starts a timeout.
     * The socket will then wait to receive a packet, which will be parsed
     * and compared to the chunk index that was sent and then break if they match.
     */
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

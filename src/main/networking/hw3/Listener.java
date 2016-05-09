package networking.hw3;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * A thread that it constantly listening on a given port. Depending
 * on what opp code is received in the packet, a certain action will occur.
 */
public class Listener extends Thread {

    private MulticastSocket socket;
    private final int MAX_PACKET_SIZE = 161;
    private int port;
    private InetAddress ip ;

    /**
     * Initialize variables sent in from Main
     * @param s Reference to a multicast socket that was initialized
     * @param port Port number the multicast socket is on
     * @param ip InetAddress the multicast socket is bind to
     */
    public Listener(MulticastSocket s, int port, InetAddress ip){
        this.socket = s;

        this.port = port;

        this.ip = ip;

        this.start();
    }

    /**
     * The thread will continuously listen to a multicast socket.
     * Once a packet is received, the mac address will be checked
     * in to determine if the packet was not sent from itself.
     * The opp code is then read and will perform different
     * actions based on the opp code
     * opp code keys:
     * 1 - This code will be run when a node wants to join the group. A packet
     * is sent out to join and receives back a packet containing the set of
     * data that has been already worked on
     * 3 - This code is a response to a code 1. It will receive a request, wrap up
     * the data in a byte array and send it back to the node that just joined
     * 4 - A packet was received telling the node that the chunk it has picked
     * to work on is already being worked on, and it should pick a new chunk
     * 5 - This packet is a completion notice that a chunk has been completed,
     * so the set should be updated and this chunk should not be returned to
     */
    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {

            try {

                byte[] b = new byte[MAX_PACKET_SIZE];

                DatagramPacket packet = new DatagramPacket(b, b.length);

                DatagramPacket respPacket;
                socket.receive(packet);
                b = packet.getData();
                int opCode = java.nio.ByteBuffer.wrap(Arrays.copyOfRange(packet.getData(), 0, 1)).get();
                long address = ByteBuffer.wrap(b).getLong(1);
                if(Main.macAddress != address){
                    if (opCode == 1) {
                        byte[] resp = PacketHandler.respondToJoin();

                        respPacket = new DatagramPacket(resp, resp.length, ip, port);

                        socket.send(respPacket);
                    } else if (opCode == 3) {
                        ByteBuffer bb = Proposition.recvProp(ByteBuffer.wrap(b));

                        if (bb != null) {
                            byte[] resp = bb.array();

                            respPacket = new DatagramPacket(resp, resp.length, ip, port);

                            socket.send(respPacket);
                        }
                    } else if (opCode == 4) {
                        int resp = Proposition.recvPropNack(ByteBuffer.wrap(b));
                        if (resp == 0) {
                            //stop working and pick a new chunk, but ignore if some condition is met
                            System.out.println("Canceling");
                            Analytics.future.cancel(true);
                        }
                    } else if (opCode == 5) {
                        int resp = Proposition.recvComp(ByteBuffer.wrap(b));
                        if (resp == 0) {
                            System.out.println("Canceling");
                            //stop working and pick a new chunk, but ignore if some condition is met
                            Analytics.future.cancel(true);
                        }
                    }
                }
            } catch (IOException e) {

            }
        }

    }

    /**
     * A method called at the end of the program to safely stop the thread from looping.
     */
    public void stopThread(){
        this.interrupt();
    }

}

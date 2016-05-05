package networking.hw3;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Listener extends Thread {

    private MulticastSocket socket;
    private final int MAX_PACKET_SIZE = 153;
    private int port;
    private InetAddress ip ;

    public Listener(MulticastSocket s, int port, InetAddress ip){
        this.socket = s;

        this.port = port;

        this.ip = ip;

        this.start();
    }

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
                if(opCode == 1){
                    byte[] resp = PacketHandler.respondToJoin();

                    respPacket = new DatagramPacket(resp, resp.length, ip, port);

                    socket.send(respPacket);
                } else if(opCode == 3){
                    ByteBuffer bb = Proposition.recvProp(ByteBuffer.wrap(b));

                    if (bb != null){
                        byte [] resp = bb.array();

                        respPacket = new DatagramPacket(resp, resp.length, ip, port);

                        socket.send(respPacket);
                    }
                } else if(opCode == 4){
                    int resp = Proposition.recvPropNack(ByteBuffer.wrap(b));
                    if(resp == 0){
                        //stop working and pick a new chunk, but ignore if some condition is met
                        Analytics.future.cancel(true);
                    }
                } else if(opCode == 5){
                    int resp = Proposition.recvComp(ByteBuffer.wrap(b));
                    if(resp == 0){
                        //stop working and pick a new chunk, but ignore if some condition is met
                        Analytics.future.cancel(true);
                    }
                }
            } catch (IOException e) {

            }
        }

    }

    public void stopThread(){
        this.interrupt();
    }

}

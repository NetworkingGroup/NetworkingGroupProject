package networking.hw3;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Listener extends Thread {

    private MulticastSocket socket;
    private final int MAX_PACKET_SIZE = 1472;

    public Listener(MulticastSocket s){
        this.socket = s;

        this.start();
    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {

            try {

                byte[] b = new byte[MAX_PACKET_SIZE];

                DatagramPacket packet = new DatagramPacket(b, b.length);

                socket.receive(packet);
                b = packet.getData();
                int opCode = b[0];
                if(opCode == 1){
                    byte[] resp = PacketHandler.respondToJoin();
                    //TODO send resp
                } else if(opCode == 3){
                    byte[] resp = Proposition.recvProp(ByteBuffer.wrap(b)).array();
                    //TODO send resp
                } else if(opCode == 4){
                    int resp = Proposition.recvPropNack(ByteBuffer.wrap(b));
                    if(resp == 0){
                        //stop working and pick a new chunk, but ignore if some condition is met
                        //TODO make a method for this
                    }
                } else if(opCode == 5){
                    int resp = Proposition.recvComp(ByteBuffer.wrap(b));
                    if(resp == 0){
                        //TODO ""
                    }
                }
            } catch (IOException e) {
                System.out.println("ending");
            }
        }

    }

    public void stopThread(){
        this.interrupt();
    }

}

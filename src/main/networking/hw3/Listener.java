package networking.hw3;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
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

                System.out.println(Arrays.toString(packet.getData()));
                // dissect packet and perform an action
            } catch (IOException e) {
                System.out.println("ending");
            }
        }

    }

    public void stopThread(){
        this.interrupt();
    }

}

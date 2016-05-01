package networking.hw3;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.ByteBuffer;

public class Client {


    final static int PORT = 2706;

    public static void main(String[] args) {

        MulticastSocket socket = null;

        InetAddress host = null;

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try {
            host = Inet4Address.getByName("239.0.0.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            socket = new MulticastSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line ="";

        try {
            socket.joinGroup(host);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        do{

            // for sending
            byte[] sequenceNumber = ByteBuffer.allocate(4).putInt(Integer.parseInt(line)).array();
            DatagramPacket packet = new DatagramPacket(sequenceNumber, 4, host, PORT);

            // for receiving
//        byte [] b = new byte[4];
//        DatagramPacket p = new DatagramPacket(b,4,host,PORT);


            try {
                // for receiving
//            socket.receive(p);
//            System.out.println(Arrays.toString(p.getData()));

                // for sending
                socket.send(packet);
                line = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while(!line.equals("q"));
    }

}

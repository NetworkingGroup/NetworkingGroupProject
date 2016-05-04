package networking.hw3;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class ClientTest {

    static final int PORT = 2706;

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

        try {
            socket.joinGroup(host);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Listener listener = new Listener(socket,0,null);

        try {
            String line;

            do{
                line = in.readLine();
            }while(!line.equals("q"));

            socket.leaveGroup(host);

            socket.close();

            listener.stopThread();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

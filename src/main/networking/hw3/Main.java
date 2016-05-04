package networking.hw3;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    static final int CHUNK_SIZE = 12;//random size for each chunk
    static ArrayList<Integer> log;
    static int currentChunk = -1;
    static long[] timers;
    public static void main(String[] args) {
        //each value result will go in here, -1 means the chunk is being worked on, -2 means it is free to be worked on
        log = new ArrayList<>();
        int host = 0;//host provided by the user, if there is none this machine is the first node in the cluster
        host = Integer.parseInt(args[1]);
        String region = args[2];
        int month = Integer.parseInt(args[3]);
        int year = Integer.parseInt(args[4]);
        MulticastSocket socket = null;
        InetAddress ip = null;

        try {
            ip = InetAddress.getByName("273.1.1.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            socket = new MulticastSocket(2706);

            socket.joinGroup(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String baseUrl = "someUrl"; //user provided url for getting data
        int maxIndex =  20; //user provided limit to the amount of data they want
        timers = new long[maxIndex];
        for (int i = 0; i < maxIndex; i++) {
            log.add(-2);//initialize every index to "ready to be worked on"
            timers[i] = 0;
        }
        if(host != 0){
            byte[] requestBytes = PacketHandler.requestToJoin();

            byte [] receiveBytes = new byte[153];

            DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length);

            DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);

            for (;;){
                try{
                    socket.send(requestPacket);

                    socket.setSoTimeout(1000);

                    socket.receive(receivePacket);

                    int opCode = receivePacket.getData()[0];

                    if (opCode == 2){
                        PacketHandler.takeInResponse(ByteBuffer.wrap(receivePacket.getData()));

                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Analytics a = new Analytics(baseUrl);

        Listener listener = new Listener(socket);
        //only stop if all the pending chunks are gone, all of the ready chunks are done
        while (log.contains(-1) && log.contains(-2)) {

            int toWorkOn = -1;
            //if there is a waiting chunk analyze it
            Random ran = new Random();
            //pick a random index
            toWorkOn = ran.nextInt(log.size());
            while(log.get(toWorkOn) != -2){
                //if it is pending check that it has not been more than ten seconds
                if(log.get(toWorkOn) == -1){
                    if(System.currentTimeMillis() - timers[toWorkOn] > 10){
                        log.set(toWorkOn, -2);
                        break;
                    }
                }
                //check that there are still things left to work on
                if (!log.contains(-1) && !log.contains(-2)) {
                    toWorkOn = -1;
                    break;
                }
                //pick a new random index
                toWorkOn = ran.nextInt(log.size());
            }
            if(toWorkOn != -1){
                Proposition.sendProp(toWorkOn);//send this
                currentChunk = toWorkOn;
                log.set(toWorkOn, -1);
                int result = a.analyze(toWorkOn*CHUNK_SIZE, (1+toWorkOn)*CHUNK_SIZE);//analyze chunk
                if (result != -1){ //if it returned a -1 that means it was terminated early
                    log.set(toWorkOn, result);
                    Proposition.sendComp(toWorkOn, log.get(toWorkOn));//send this
                }
            }
        }
    }
}

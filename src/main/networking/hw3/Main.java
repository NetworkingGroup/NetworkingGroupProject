package networking.hw3;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {


    static final int CHUNK_SIZE = 12;//random size for each chunk
    static ArrayList<Long> log;
    static int currentChunk = -1;
    static long[] timers;
    static int maxIndex =  19; //user provided limit to the amount of data they want
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        int port = 2706;
        //each value result will go in here, -1 means the chunk is being worked on, -2 means it is free to be worked on
        log = new ArrayList<>();
        int host = 0;//host provided by the user, if there is none this machine is the first node in the cluster
//        host = Integer.parseInt(args[0]);
        host = sc.nextInt();
        MulticastSocket socket = null;
        InetAddress ip = null;

        try {
            ip = InetAddress.getByName("239.0.0.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            socket = new MulticastSocket(port);

            socket.joinGroup(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String baseUrl = "someUrl"; //user provided url for getting data

        timers = new long[maxIndex];
        for (int i = 0; i < maxIndex; i++) {
            log.add(-2l);//initialize every index to "ready to be worked on"
            timers[i] = 0;
        }
        if(host != 0){
            byte[] requestBytes = PacketHandler.requestToJoin();

            byte [] receiveBytes = new byte[153];

            DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length,ip, port);

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

                }
            }
        }
        Analytics a = new Analytics();

        Listener listener = new Listener(socket,port, ip);
        //only stop if all the pending chunks are gone, all of the ready chunks are done
        while (log.contains(-1l) || log.contains(-2l)) {
            int toWorkOn = -1;
            //if there is a waiting chunk analyze it
            Random ran = new Random();
            //pick a random index
            toWorkOn = ran.nextInt(log.size());
            while(log.get(toWorkOn) != -2l){
                //if it is pending check that it has not been more than ten seconds
                if(log.get(toWorkOn) == -1l){
                    if(System.currentTimeMillis() - timers[toWorkOn] > 10){
                        log.set(toWorkOn, -2l);
                        break;
                    }
                }
                //check that there are still things left to work on
                if (!log.contains(-1l) || !log.contains(-2l)) {
                    toWorkOn = -1;
                    break;
                }
                //pick a new random index
                toWorkOn = ran.nextInt(log.size());
            }
            if(toWorkOn != -1l){
                System.out.println( "working on " + log.get(toWorkOn));
                byte [] propByte = Proposition.sendProp(toWorkOn).array();

                DatagramPacket prop = new DatagramPacket(propByte,propByte.length,ip,port);

                try {
                    socket.send(prop);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentChunk = toWorkOn;

                long result = a.analyze(toWorkOn);//analyze chunk
                if (result != -1){ //if it returned a -1 that means it was terminated early
                    log.set(toWorkOn, result);
                    byte [] compByte = Proposition.sendComp(toWorkOn, log.get(toWorkOn)).array();

                    DatagramPacket comp = new DatagramPacket(compByte, compByte.length, ip, port);

                    try {
                        socket.send(comp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for(int i = 0;i<log.size();i++){
            System.out.println(log.get(i));
        }

        try {
            socket.leaveGroup(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }

        socket.close();

        listener.stopThread();
    }

}

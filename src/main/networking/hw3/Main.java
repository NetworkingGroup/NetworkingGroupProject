package networking.hw3;


import java.io.IOException;
import java.net.*;
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
    static long macAddress;
    static int count = 0;

    public static void main(String[] args) throws ExecutionException, InterruptedException, UnknownHostException, SocketException {
        Scanner sc = new Scanner(System.in);

        int multiPort = 2706;
        int singlePort = 7002;

        //each value result will go in here, -1 means the chunk is being worked on, -2 means it is free to be worked on
        log = new ArrayList<>();
        int host = 0;//host provided by the user, if there is none this machine is the first node in the cluster
//        host = Integer.parseInt(args[0]);
        host = sc.nextInt();
        MulticastSocket multiSocket = null;
        InetAddress multiIP = null;
        InetAddress singleIP = null;
        DatagramSocket singleSocket = null;


        try {
            multiIP = InetAddress.getByName("238.0.0.0");

            singleIP = InetAddress.getByName("pi.cs.oswego.edu");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            multiSocket = new MulticastSocket(multiPort);

            singleSocket = new DatagramSocket();

            multiSocket.joinGroup(multiIP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String baseUrl = "someUrl"; //user provided url for getting data

        InetAddress address = InetAddress.getLocalHost();
        NetworkInterface nwi = NetworkInterface.getByInetAddress(address);
        byte mac[] = nwi.getHardwareAddress();
        macAddress = ByteBuffer.wrap(mac).getInt() + ByteBuffer.wrap(mac).getShort(4) + host;

        timers = new long[maxIndex];
        for (int i = 0; i < maxIndex; i++) {
            log.add(-2l);//initialize every index to "ready to be worked on"
            timers[i] = 0;
        }
        if(host != 0){
            byte[] requestBytes = PacketHandler.requestToJoin();

            byte [] receiveBytes = new byte[161];

            DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length,multiIP, multiPort);

            DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);

            for (;;){
                try{
                    multiSocket.send(requestPacket);

                    multiSocket.setSoTimeout(1000);

                    multiSocket.receive(receivePacket);

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

        Listener listener = new Listener(multiSocket,multiPort, multiIP);
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
                System.out.println( "working on " + toWorkOn);
                byte [] propByte = Proposition.sendProp(toWorkOn).array();

                DatagramPacket prop = new DatagramPacket(propByte,propByte.length,multiIP,multiPort);

                try {
                    multiSocket.send(prop);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentChunk = toWorkOn;

                long result = a.analyze(toWorkOn);//analyze chunk
                System.out.println( "Result " + result);
                if (result != -1){ //if it returned a -1 that means it was terminated early
                    log.set(toWorkOn, result);

                    byte [] sendByte = Proposition.sendData(toWorkOn,log.get(toWorkOn)).array();

                    DatagramPacket packet = new DatagramPacket(sendByte, sendByte.length, singleIP, singlePort);

                    DataPacket dataPacket = new DataPacket(singleSocket, packet, toWorkOn);

                    dataPacket.start();

                    byte [] compByte = Proposition.sendComp(toWorkOn, log.get(toWorkOn)).array();

                    DatagramPacket comp = new DatagramPacket(compByte, compByte.length, multiIP, multiPort);
                    count++;
                    try {
                        multiSocket.send(comp);
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
            multiSocket.leaveGroup(multiIP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        multiSocket.close();

        singleSocket.close();

        listener.stopThread();
    }

}

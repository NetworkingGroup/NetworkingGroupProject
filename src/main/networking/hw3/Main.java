package networking.hw3;


import java.util.ArrayList;

public class Main {

    static final int CHUNK_SIZE = 12;//random size for each chunk
    public static void main(String[] args) {
        //each value result will go in here, -1 means the chunk is being worked on, -2 means it is free to be worked on
        ArrayList<Integer> log = new ArrayList<>();
        int host = 0;//host provided by the user, if there is none this machine is the first node in the cluster


        String baseUrl = "someUrl"; //user provided url for getting data
        int maxIndex =  20; //user provided limit to the amount of data they want
        if(host != 0){
            //send join request, keep sending until there is a response
            //copy response to log
        } else {
            for (int i = 0; i < maxIndex; i++) {
                log.add(-2);//initialize every index to "ready to be worked on"
            }
        }
        Analytics a = new Analytics(baseUrl);
        //only stop if all the pending chunks are gone, all of the ready chunks are done
        while (log.contains(-1) && log.contains(-2)) {
            int toWorkOn = -1;
            //if there is a waiting chunk analyze it
            toWorkOn = log.indexOf(-2);
            if(toWorkOn != -1){
                Proposition.sendProp(toWorkOn);//send this
                log.set(toWorkOn, -1);
                log.set(toWorkOn, a.analyze(toWorkOn*CHUNK_SIZE, (1+toWorkOn)*CHUNK_SIZE)); //analyze chunk
                Proposition.sendComp(toWorkOn, log.get(toWorkOn));//send this
            }
        }
    }
}

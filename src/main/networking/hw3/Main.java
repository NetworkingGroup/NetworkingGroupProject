package networking.hw3;


import java.util.ArrayList;

public class Main {
    static final int CHUNK_SIZE = 12;//random size for each chunk
    public static void main(String[] args) {
        //each value result will go in here, -1 means the chunk is being worked on, -2 means it is free to be worked on
        ArrayList<Integer> log = new ArrayList<>();

        String baseUrl = "someUrl"; //user provided url for getting data
        int maxIndex =  20; //user provided limit to the amount of data they want
        for(int i = 0; i < maxIndex; i++){
            log.add(-2);//initilize every index to "ready to be worked on"
        }
        Anylitics a = new Anylitics(baseUrl);
        //only stop if all the pending chunks are gone, all of the ready chunks are done
        while (log.contains(-1) && log.contains(-2)) {
            int toWorkOn = -1;
            toWorkOn = log.indexOf(0);
            a.anylize(toWorkOn*CHUNK_SIZE, (1+toWorkOn)*CHUNK_SIZE); //analyze chunk, run in background if possible
            //send proposition
            //check for join requests
            //check for response to propositions
            //check of other propositions
            //check timers for indexes with -1, if it's timer is up change value to zero
        }
    }
}

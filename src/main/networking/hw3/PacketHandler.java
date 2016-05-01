package networking.hw3;

import java.util.ArrayList;

/**
 * Created by keithmartin on 4/28/16.
 */
public class PacketHandler {


    public byte[] requestToJoin() {
        byte[] requestJoin = new byte[1];
        requestJoin[0] = 1;
        return requestJoin;
    }

    public byte[] respondToJoin(ArrayList<Integer> localLog) {
        byte[] respondJoin = new byte[localLog.size() + 1];
        respondJoin[0] = 2;

        for (int i = 0; i < localLog.size(); i++) {
            respondJoin[i+1] = localLog.get(i).byteValue();
        }

        return respondJoin;
    }

}

package networking.hw3;

import java.nio.ByteBuffer;
import java.util.ArrayList;


public class PacketHandler {


    public static byte[] requestToJoin() {
        byte[] requestJoin = new byte[1];
        requestJoin[0] = 1;
        return requestJoin;
    }

    public static byte[] respondToJoin() {
        byte[] respondJoin = new byte[Main.log.size()*8 + 1];
        respondJoin[0] = 2;

        for (int i = 0; i < Main.log.size(); i++) {
            respondJoin[(i*8)+1] = Main.log.get(i).byteValue();
        }

        return respondJoin;
    }

    public static void takeInResponse(ByteBuffer responseToJoin) {
        for (int i = 0; i < responseToJoin.limit() ; i++) {
            Main.log.set(i,responseToJoin.getLong(1+(i*8)));
        }
    }

}

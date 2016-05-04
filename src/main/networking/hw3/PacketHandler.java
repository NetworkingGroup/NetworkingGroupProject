package networking.hw3;

import java.nio.ByteBuffer;


public class PacketHandler {


    public static byte[] requestToJoin() {
        byte[] requestJoin = new byte[1];
        requestJoin[0] = 1;
        return requestJoin;
    }

    public static byte[] respondToJoin() {
        ByteBuffer respondJoin = ByteBuffer.allocate(153);
        respondJoin.put((byte)2);
        for (int i = 0; i < Main.log.size(); i++) {
            respondJoin.putLong((i*8)+1, Main.log.get(i));
        }

        return respondJoin.array();
    }

    public static void takeInResponse(ByteBuffer responseToJoin) {
        for (int i = 0; i < Main.maxIndex ; i++) {
            Main.log.set(i,responseToJoin.getLong(1+(i*8)));
        }
    }

}

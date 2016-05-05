package networking.hw3;

import java.nio.ByteBuffer;


public class PacketHandler {


    public static byte[] requestToJoin() {

        return ByteBuffer.allocate(9).put((byte)1).putLong(Main.macAddress).array();
    }

    public static byte[] respondToJoin() {
        ByteBuffer respondJoin = ByteBuffer.allocate(161);
        respondJoin.put((byte)2);
        respondJoin.putLong(Main.macAddress);
        for (int i = 0; i < Main.log.size(); i++) {
            respondJoin.putLong((i*8)+9, Main.log.get(i));
        }

        return respondJoin.array();
    }

    public static void takeInResponse(ByteBuffer responseToJoin) {
        for (int i = 0; i < Main.maxIndex ; i++) {
            Main.log.set(i,responseToJoin.getLong(9+(i*8)));
        }
    }

}

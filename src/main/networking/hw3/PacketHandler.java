package networking.hw3;

import java.nio.ByteBuffer;

/**
 * This class handles creating and responding to requests to join the cluster
 */
public class PacketHandler {


    /**
     * This creates a byte array to send as a request to join the cluster
     *
     * @return a byte buffer including the one byte opcode and the mac address for this machine so it can ignore
     * its own messages
     */
    public static byte[] requestToJoin() {

        return ByteBuffer.allocate(9).put((byte)1).putLong(Main.macAddress).array();
    }

    /**
     * This creates a response to a request to join
     *
     * @return a byte buffer including the one byte opcode, the mac address for this machine and all of the data
     * gathered so far
     */
    public static byte[] respondToJoin() {
        ByteBuffer respondJoin = ByteBuffer.allocate(161);
        respondJoin.put((byte)2);
        respondJoin.putLong(Main.macAddress);
        for (int i = 0; i < Main.log.size(); i++) {
            respondJoin.putLong((i*8)+9, Main.log.get(i));
        }

        return respondJoin.array();
    }

    /**
     * This accepts infromation from the response to join and adds it to the current empty log
     *
     * @param responseToJoin this is the data that was received
     */
    public static void takeInResponse(ByteBuffer responseToJoin) {
        for (int i = 0; i < Main.maxIndex ; i++) {
            Main.log.set(i,responseToJoin.getLong(9+(i*8)));
        }
    }

}

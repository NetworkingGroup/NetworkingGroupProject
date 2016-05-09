package networking.hw3;

import java.nio.ByteBuffer;

/**
 * This class handles creating and receiving proposition and completion packets
 */
public class Proposition {
    static final byte PROP_CODE = 3;
    static final byte PROP_NACK_CODE = 4;
    static final byte COMP_CODE = 5;

    /**
     * Creates a bytebuffer to send as a request to work on a given chunk
     *
     * @param chunkIndex the index of the chunk that this node wants to work on
     * @return a bytebuffer of the message to be sent as a proposition to the other nodes,
     * includes one byte for the op code, a long for the address of this machine so it can ignore its own messages
     * and an int indicating the chunk it wants to work on
     */
    public static ByteBuffer sendProp(int chunkIndex){
        return ByteBuffer.allocate(13).put(PROP_CODE).putLong(Main.macAddress).putInt(chunkIndex);
    }

    /**
     * determines reaction to reciving a proposition packet
     *
     * @param prop the proposition message that this node has recived
     * @return a bytebuffer of the message to send in response to the proposition,  if this node is not currently
     * working on the data and does not already have the data the node will note that the chunk that is being worked on
     * and will return null since nothing needs to be sent, otherwise it will either send a complition packet including
     * one byte for the op code, a long for the address of this machine so it can ignore its own messages
     * and an int indicating the chunk it has the data for and a long for the data itself, or it will send a
     * negative ack stating that this node is already working on the chunk, it will include one byte for the op code,
     * a long for the address of this machine so it can ignore its own messages
     * and an int indicating the chunk that this node is already working on
     */
    public static ByteBuffer recvProp(ByteBuffer prop){
        int chunkIndex = prop.getInt(9);
        if(Main.log.get(chunkIndex) == -2l){
            //this means the chunk is free to be worked on
            Main.log.set(chunkIndex, -1l);
            Main.timers[chunkIndex] = System.currentTimeMillis();
            //should set timer for it
            return null;
        } else if (Main.log.get(chunkIndex) == -1l){
            //this means it is already being worked on
            if(chunkIndex == Main.currentChunk){
                //this means the chunk is already being worked on by you
                return ByteBuffer.allocate(13).put(PROP_NACK_CODE).putLong(Main.macAddress).putInt(chunkIndex);
            }
            return null;
        } else {
            //this means there is already data for this chunk
            return ByteBuffer.allocate(21).put(COMP_CODE).putLong(Main.macAddress).putInt(chunkIndex).putLong(Main.log.get(chunkIndex));
        }
    }

    /**
     * determines reaction to reciving a negative ack packet
     *
     * @param nack the negative ack message that this node has recived
     * @return an integer indicating if the nack that was recived pertains to the chunk that this node is
     * currently working on, returns a non-zero number if it is safe to ignore the nack
     */
    public static int recvPropNack(ByteBuffer nack){
        if(Main.maxIndex - Main.count <= 2) return 1;
        int result = Integer.compare(nack.getInt(9), Main.currentChunk);
        return result;
    }

    /**
     * Creates a byte buffer to send when a chunk is completed
     *
     * @param chunkIndex the chunk index of the chunk that was just completed
     * @param data the infromation that has been gathered
     * @return a bytebuffer of the message to be sent with the recently completed data,
     * includes one byte for the op code, a long for the address of this machine so it can ignore its own messages
     * an int indicating the chunk it is sending and a long for the data itself
     */
    public static ByteBuffer sendComp(int chunkIndex, long data){
        return ByteBuffer.allocate(21).put(COMP_CODE).putLong(Main.macAddress).putInt(chunkIndex).putLong(data);
    }

    /**
     * determines reaction to reciving a comp packet
     *
     * @param comp the completion message that this node has recived
     * @return an integer indicating if the comp that was recived pertains to the chunk that this node is
     * currently working on, returns a non-zero number if it is safe to continue working on the chunk, or 0 if
     * it should pick a new node, this also adds the data to the log
     */
    public static int recvComp(ByteBuffer comp){
        int chunkIndex = comp.getInt(9);
        Main.log.set(chunkIndex, comp.getLong(13));
        Main.count++;
        return Integer.compare(chunkIndex, Main.currentChunk);
    }

    /**
     * comp packet to send to the graph server
     *
     * @param chunkIndex the chunk index of the chunk that was just completed
     * @param data the infromation that has been gathered
     * a bytebuffer of the message to be sent with the recently completed data,
     * includes an int indicating the chunk it is sending and a long for the data itself
     */
    public static ByteBuffer sendData(int chunkIndex, long data){
        return ByteBuffer.allocate(12).putInt(chunkIndex).putLong(data);
    }
}

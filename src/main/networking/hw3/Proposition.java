package networking.hw3;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Nicholas on 4/30/2016.
 */
public class Proposition {
    static final short PROP_CODE = 3;
    static final short PROP_NACK_CODE = 4;
    static final short COMP_CODE = 5;
    //most recent chunk you received approval for working on
    static int acks = -1;
    public static ByteBuffer sendProp(int chunkIndex){
        return ByteBuffer.allocate(6).putShort(PROP_CODE).putInt(chunkIndex);
    }
    //returns the byte buffer that shold be sent, null means nothing should be sent
    public static ByteBuffer recvProp(ByteBuffer prop, ArrayList<Integer> log, int currentChunk){
        int chunkIndex = prop.getInt(2);
        if(log.get(chunkIndex) == -2){
            //this means the chunk is free to be worked on
            log.set(chunkIndex, -1);
            //should set timer for it
            return null;
        } else if (log.get(chunkIndex) == -1){
            //this means it is already being worked on
            if(chunkIndex == currentChunk){
                //this means the chunk is already being worked on by you
                return ByteBuffer.allocate(6).putShort(PROP_NACK_CODE).putInt(chunkIndex);
            }
            return null;
        } else {
            //this means there is already data for this chunk
            return ByteBuffer.allocate(10).putShort(COMP_CODE).putInt(chunkIndex).putInt(log.get(chunkIndex));
        }
    }
    //0 means to stop working ont he current chunk, otherwise continue
    public static int recvPropNack(ByteBuffer nack, int currentChunk){
        int result = Integer.compare(nack.getInt(2), currentChunk);
        return result;
    }
    public static ByteBuffer sendComp(int chunkIndex, int data){
        return ByteBuffer.allocate(10).putShort(COMP_CODE).putInt(chunkIndex).putInt(data);
    }
    //0 means to stop working ont he current chunk, otherwise continue
    public static int recvComp(ByteBuffer comp, int currentChunk, ArrayList<Integer> log){
        int chunkIndex = comp.getInt(2);
        log.set(chunkIndex, comp.getInt(6));
        return Integer.compare(chunkIndex, currentChunk);
    }
}

package networking.hw3;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Nicholas on 4/30/2016.
 */
public class Proposition {
    static final byte PROP_CODE = 3;
    static final byte PROP_NACK_CODE = 4;
    static final byte COMP_CODE = 5;
    public static ByteBuffer sendProp(int chunkIndex){
        return ByteBuffer.allocate(5).put(PROP_CODE).putInt(chunkIndex);
    }
    //returns the byte buffer that shold be sent, null means nothing should be sent
    public static ByteBuffer recvProp(ByteBuffer prop){
        int chunkIndex = prop.getInt(1);
        if(Main.log.get(chunkIndex) == -2){
            //this means the chunk is free to be worked on
            Main.log.set(chunkIndex, -1);
            //should set timer for it
            return null;
        } else if (Main.log.get(chunkIndex) == -1){
            //this means it is already being worked on
            if(chunkIndex == Main.currentChunk){
                //this means the chunk is already being worked on by you
                return ByteBuffer.allocate(5).put(PROP_NACK_CODE).putInt(chunkIndex);
            }
            return null;
        } else {
            //this means there is already data for this chunk
            return ByteBuffer.allocate(9).put(COMP_CODE).putInt(chunkIndex).putInt(Main.log.get(chunkIndex));
        }
    }
    //0 means to stop working ont he current chunk, otherwise continue
    public static int recvPropNack(ByteBuffer nack){
        int result = Integer.compare(nack.getInt(1), Main.currentChunk);
        return result;
    }
    public static ByteBuffer sendComp(int chunkIndex, int data){
        return ByteBuffer.allocate(9).put(COMP_CODE).putInt(chunkIndex).putInt(data);
    }
    //0 means to stop working ont he current chunk, otherwise continue
    public static int recvComp(ByteBuffer comp){
        int chunkIndex = comp.getInt(1);
        Main.log.set(chunkIndex, comp.getInt(5));
        return Integer.compare(chunkIndex, Main.currentChunk);
    }
}

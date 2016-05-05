package networking.hw3;

import java.nio.ByteBuffer;


public class Proposition {
    static final byte PROP_CODE = 3;
    static final byte PROP_NACK_CODE = 4;
    static final byte COMP_CODE = 5;
    public static ByteBuffer sendProp(int chunkIndex){
        return ByteBuffer.allocate(13).put(PROP_CODE).putLong(Main.macAddress).putInt(chunkIndex);
    }
    //returns the byte buffer that shold be sent, null means nothing should be sent
    public static ByteBuffer recvProp(ByteBuffer prop){
        int chunkIndex = prop.getInt(9);
        if(Main.log.get(chunkIndex) == -2l){
            //this means the chunk is free to be worked on
            Main.log.set(chunkIndex, -1l);
            // // TODO look at this 
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
    //0 means to stop working ont he current chunk, otherwise continue
    public static int recvPropNack(ByteBuffer nack){
        if(Main.maxIndex - Main.count <= 2) return 1;
        int result = Integer.compare(nack.getInt(9), Main.currentChunk);
        return result;
    }
    public static ByteBuffer sendComp(int chunkIndex, long data){
        return ByteBuffer.allocate(21).put(COMP_CODE).putLong(Main.macAddress).putInt(chunkIndex).putLong(data);
    }
    //0 means to stop working ont he current chunk, otherwise continue
    public static int recvComp(ByteBuffer comp){
        int chunkIndex = comp.getInt(9);
        Main.log.set(chunkIndex, comp.getLong(13));
        Main.count++;
        return Integer.compare(chunkIndex, Main.currentChunk);
    }
}

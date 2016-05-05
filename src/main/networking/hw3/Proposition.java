package networking.hw3;

import java.nio.ByteBuffer;


public class Proposition {
    static final byte PROP_CODE = 3;
    static final byte PROP_NACK_CODE = 4;
    static final byte COMP_CODE = 5;
    public static ByteBuffer sendProp(int chunkIndex){
        return ByteBuffer.allocate(5).put(PROP_CODE).putInt(chunkIndex);
    }
    //returns the byte buffer that shold be sent, null means nothing should be sent
    public static ByteBuffer recvProp(ByteBuffer prop){
        System.out.println("Got prop");
        int chunkIndex = prop.getInt(1);
        if(Main.log.get(chunkIndex) == -2l){
            //this means the chunk is free to be worked on
            Main.log.set(chunkIndex, -1l);
            // // TODO look at this 
            Main.timers[chunkIndex] = System.currentTimeMillis();
            //should set timer for it
            System.out.println("Got prop for: " + chunkIndex);
            return null;
        } else if (Main.log.get(chunkIndex) == -1l){
            //this means it is already being worked on
            System.out.println("Sending Nack for: " + chunkIndex);
            if(chunkIndex == Main.currentChunk){
                //this means the chunk is already being worked on by you
                return ByteBuffer.allocate(5).put(PROP_NACK_CODE).putInt(chunkIndex);
            }
            return null;
        } else {
            //this means there is already data for this chunk
            System.out.println("Sending comp Nack for: " + chunkIndex);
            return ByteBuffer.allocate(13).put(COMP_CODE).putInt(chunkIndex).putLong(Main.log.get(chunkIndex));
        }
    }
    //0 means to stop working ont he current chunk, otherwise continue
    public static int recvPropNack(ByteBuffer nack){
        System.out.println("Got Nack for: " + nack.getInt(1));
        if(Main.maxIndex - Main.count <= 2) return 1;
        int result = Integer.compare(nack.getInt(1), Main.currentChunk);
        return result;
    }
    public static ByteBuffer sendComp(int chunkIndex, long data){
        return ByteBuffer.allocate(13).put(COMP_CODE).putInt(chunkIndex).putLong(data);
    }
    //0 means to stop working ont he current chunk, otherwise continue
    public static int recvComp(ByteBuffer comp){
        int chunkIndex = comp.getInt(1);
        Main.log.set(chunkIndex, comp.getLong(5));
        System.out.println("Got comp for: " + chunkIndex);
        Main.count++;
        return Integer.compare(chunkIndex, Main.currentChunk);
    }
}

package networking.hw3;


import com.google.common.collect.ImmutableMap;
import javafx.util.Pair;

import java.util.Map;

public class CategoryMap {

    public static final Map<Integer, Pair<String, String>> VALUES = new ImmutableMap.Builder<Integer, Pair<String, String>>()
            .put(0, new Pair<>("GCQmVzdCBvZiBZb3VUdWJl", "Best of YouTube"))
            .put(1, new Pair<>("GCUGFpZCBDaGFubmVscw", "Paid Channels"))
            .put(2, new Pair<>("GCTXVzaWM", "Music"))
            .put(3, new Pair<>("GCQ29tZWR5", "Comedy"))
            .put(4, new Pair<>("GCRmlsbSAmIEVudGVydGFpbm1lbnQ", "Film & Entertainment"))
            .put(5, new Pair<>("GCR2FtaW5n", "Gaming"))
            .put(6, new Pair<>("GCQmVhdXR5ICYgRmFzaGlvbg", "Beauty & Fashion"))
            .put(7, new Pair<>("GCRnJvbSBUVg", "From TV"))
            .put(8, new Pair<>("GCQXV0b21vdGl2ZQ", "Automotive"))
            .put(9, new Pair<>("GCQW5pbWF0aW9u", "Animation"))
            .put(10, new Pair<>("GCVG9wIFlvdVR1YmUgQ29sbGVjdGlvbnM", "Top YouTube Collections"))
            .put(11, new Pair<>("GCU3BvcnRz", "Sports"))
            .put(12, new Pair<>("GCSG93LXRvICYgRElZ", "How-to & DIY"))
            .put(13, new Pair<>("GCVGVjaA", "Tech"))
            .put(14, new Pair<>("GCU2NpZW5jZSAmIEVkdWNhdGlvbg", "Science & Education"))
            .put(15, new Pair<>("GCQ29va2luZyAmIEhlYWx0aA", "Cooking & Health"))
            .put(16, new Pair<>("GCQ2F1c2VzICYgTm9uLXByb2ZpdHM", "Causes & Non-profits"))
            .put(17, new Pair<>("GCTmV3cyAmIFBvbGl0aWNz", "News & Politics"))
            .put(18, new Pair<>("GCTGlmZXN0eWxl", "Lifestyle"))
            .build();

}

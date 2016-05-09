package networking.hw3;


import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import javafx.util.Pair;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

/**
 * Class to handle querying.
 */
public class Query {
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
    private static final String BASE_URL = "http://pi.cs.oswego.edu:7000/channels?category=";
    private static final String PAGE_PARAM = "&page=";
    private static final String GET = "GET";
    private static final String USER_AGENT = "User-Agent";
    private static final String MOZILLA = "Mozilla/5.0";
    private static final String ENCODING = "UTF-8";

    private class Keys {
        static final String NEXT_PAGE_TOKEN = "nextPageToken";
        static final String ITEMS = "items";
        static final String STATISTICS = "statistics";
        static final String VIEW_COUNT = "viewCount";
    }

    /**
     * Method to count views for a specific category.
     *
     * @param categoryIndex The category index.
     * @return The view count.
     */
    public long countViews(int categoryIndex) {
        Pair<String, String> pair = VALUES.get(categoryIndex);

        if (pair == null) {
            throw new IllegalArgumentException(String.format("Given Category ID:[%s] does not map to a category", categoryIndex));
        }

        String id = pair.getKey();
        Optional<String> nextPageToken = Optional.empty();
        long total = 0;

        do {
            JsonObject object = grabData(id, nextPageToken);
            nextPageToken = getNextPageToken(object);

            JsonArray items = object.get(Keys.ITEMS).getAsJsonArray();
            for (JsonElement channelElement : items) {
                JsonObject channel = channelElement.getAsJsonObject();
                JsonObject statistics = channel.get(Keys.STATISTICS).getAsJsonObject();
                total += statistics.get(Keys.VIEW_COUNT).getAsLong();
            }


        } while (nextPageToken.isPresent());

        return total;
    }

    /**
     * Method to execute to grab the next page token from a response json object.
     *
     * @param object The json object to get data from.
     * @return An optional next page token.
     */
    private Optional<String> getNextPageToken(JsonObject object) {
        return object.has(Keys.NEXT_PAGE_TOKEN) ?
                Optional.of(object.get(Keys.NEXT_PAGE_TOKEN).getAsString())
                : Optional.empty();
    }

    /**
     * Method to get and convert data while avoiding throwing the checked exceptions if any of them happen.
     *
     * @param categoryId The category id.
     * @param pageToken  The page token.
     * @return The output data.
     */
    private JsonObject grabData(String categoryId, Optional<String> pageToken) {
        String raw = null;
        try {
            raw = queryServer(categoryId, pageToken);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        if (raw == null) {
            throw new NullPointerException("Error");
        }

        return convert(raw);
    }

    /**
     * Method to query the youtube query server for data.
     *
     * @param categoryId The category id to query with.
     * @param pageToken  The optional page token for the desired page.
     * @return The data received from the server.
     * @throws IOException Thrown if url is malformed or if opening connection failed.
     */
    private String queryServer(String categoryId, Optional<String> pageToken) throws IOException {
        String stringUrl = BASE_URL + categoryId;
        if (pageToken.isPresent()) {
            stringUrl = stringUrl.concat(String.format("%s%s", PAGE_PARAM, pageToken.get()));
        }

        URL url = new URL(stringUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(GET);
        connection.setRequestProperty(USER_AGENT, MOZILLA);

        InputStream stream = connection.getInputStream();

        return IOUtils.toString(stream, ENCODING);
    }

    /**
     * Method to convert a raw string to json object.
     *
     * @param raw The raw json.
     * @return The json object conversion.
     * @throws IllegalArgumentException Thrown if an error occurs in conversion.
     * @throws JsonSyntaxException      Thrown if an error occurs in conversion.
     */
    private JsonObject convert(String raw) {
        JsonParser parser = new JsonParser();
        JsonObject object;
        try {
            object = parser.parse(raw).getAsJsonObject();
        } catch (IllegalArgumentException | JsonSyntaxException ex) {
            System.err.println("Exception Occurred during conversion of server output data.Raw data is below");
            System.err.println(raw);
            throw ex;
        }
        return object;
    }

}

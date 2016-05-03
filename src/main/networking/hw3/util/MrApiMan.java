package networking.hw3.util;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtubeAnalytics.YouTubeAnalytics;
import com.google.api.services.youtubeAnalytics.YouTubeAnalyticsScopes;
import com.google.api.services.youtubeAnalytics.model.ResultTable;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;

//import com.google.api.services.

public class MrApiMan {

    private final HttpTransport transport = new NetHttpTransport();
    private final JsonFactory factory = new JacksonFactory();
    private final YouTubeAnalytics analytics;

    public MrApiMan() throws IOException {
        List<String> scopes = ImmutableList.of(
                YouTubeAnalyticsScopes.YT_ANALYTICS_READONLY,
                YouTubeAnalyticsScopes.YOUTUBEPARTNER,
                YouTubeAnalyticsScopes.YT_ANALYTICS_MONETARY_READONLY,
                YouTubeAnalyticsScopes.YOUTUBE
        );

        Credential credential = Auth.authorize(scopes, 8080);



        analytics = new YouTubeAnalytics.Builder(transport, factory, credential)
                .setApplicationName("networkinggroupproject")
                .build();

        ResultTable table = test();

        if (table.getRows() == null || table.getRows().isEmpty()) {
            System.out.println(table);
            throw new NullPointerException("No data");
        } else {
            for (List<Object> row : table.getRows()) {
                for (Object column : row) {
                    System.out.print(column.toString().concat(","));
                }
                System.out.println("");
            }
        }
    }

    public ResultTable test() throws IOException {
        return analytics.reports()
                .query("channel==UCxaVOVnhmT0-HCUv72jtOTA",
                        "2015-01-10",
                        "2015-02-10",
                        "views")
                .execute();
    }

    public static void main(String[] args) {
        try {
            new MrApiMan();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package networking.hw3.twitapiman;


import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

public class MrTwitMan {

    public static void main(String[] args) throws TwitterException {

        TwitterFactory factory = new TwitterFactory(
                new ConfigurationBuilder()
                        .setDebugEnabled(true)
                        .setOAuthConsumerKey("QJDH23r8EIVJDJxvlrzr6Qh5E")
                        .setOAuthConsumerSecret("5LMK8o5gT1jwnbZZK6VFk4UceCss4TaCi9m5Mkp9QObzy7eN3v")
                        .setOAuthAccessToken("727487326109229057-9WzGcA4HtsF88J1ApdHrdkYqYRGoJP9")
                        .setOAuthAccessTokenSecret("yBXbJRNbUtoYnx41V6wNg7VeQ3Fy8gySsraEYxuKWeUEO")
                        .build()
        );

        Twitter twitter = factory.getInstance();

        Query query = new Query("#MakeAmericaGreatAgain")
                .geoCode(new GeoLocation(42.057951, -100.257649),
                3000, Query.Unit.mi.name())
                .count(1000000);

        QueryResult result = twitter.search(query);

        System.out.println(result.getCount());


    }

}

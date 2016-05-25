package com.example.truongngoc.newsradio.model;

/**
 * Created by TruongNgoc on 17/11/2015.
 * class to hold the url news feeds of application
 */
public class RssUrlFeedProvider {
    private static final String[] LAST_NEWS_URLS = new String[]{
            "http://www.wcyb.com/14591268?format=rss_2.0&view=feed"
    };

    private static final String[] POLITICS_URLS = new String[]{
            "http://www.wcyb.com/news/politics/14590796?format=rss_2.0&view=feed"
    };

    private static final String[] HEALTH_URLS = new String[]{
            "http://www.wcyb.com/14591274?format=rss_2.0&view=feed"
    };

    private static final String[] MONEY_URLS = new String[]{
            "http://www.wcyb.com/14591272?format=rss_2.0&view=feed"
    };

    private static final String[] JOBS_URLS = new String[]{
            "http://www.wcyb.com/15209992?format=rss_2.0&view=feed"
    };

    private static final String[] TECHNOLOGIES_URLS = new String[]{
            "http://www.wcyb.com/14590836?format=rss_2.0&view=feed"
    };

    private static final String[] SPORTS_URLS = new String[]{
            "http://www.wcyb.com/14591270?format=rss_2.0&view=feed"
    };

    private static final String[] IRRESTIBLE_URLS = new String[]{
            "http://www.wcyb.com/14591276?format=rss_2.0&view=feed"
    };


    public static String[] provideNationalNews() {
        return LAST_NEWS_URLS;
    }

    public static String[] provideSportsNews() {
        return SPORTS_URLS;
    }

    public static String[] provideHealthNews() {
        return HEALTH_URLS;
    }

    public static String[] provideMoneyNews() {
        return MONEY_URLS;
    }

    public static String[] provideJobsNews() {
        return JOBS_URLS;
    }


    public static String[] provideTechnologiesNews() {
        return TECHNOLOGIES_URLS;
    }

    public static String[] providePoliticsNews() {
        return POLITICS_URLS;
    }
    public static String[] provideIrrestisbleNews() {
        return IRRESTIBLE_URLS;
    }
}

package com.example.truongngoc.newsradio.xmlfactory;

import android.text.Html;
import android.util.Xml;

import com.example.truongngoc.newsradio.model.NewsFeed;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by TruongNgoc on 06/10/2015.
 * class to pars rss feed for application
 */
public class XmlParserFactory {
    public static final String NAMESPACE = null;

    public static ArrayList<NewsFeed> parse(InputStream inputStream) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readYahooNewsFeeds(parser);
        } finally {
            inputStream.close();
        }
    }


    private static ArrayList<NewsFeed> readYahooNewsFeeds(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<NewsFeed> feedItemList = new ArrayList<>();
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            // get current element
            String name = parser.getName();
            if (name.equals("item")) {
                feedItemList.add(readFeedItem(parser));
            } else {
                skip(parser);
            }
        }
        return feedItemList;
    }


    private static NewsFeed readFeedItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        // start tag require
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "item");
        String title = null;
        String shortDescription = null;
        String link = null;
        String imageUrl = null;
        String publicTime = null;
        // to be continued
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            // get current element to process data
            String name = parser.getName();
            if (name.equals("title")) {
                title = readYahooNewsTitle(parser);
            } else if (name.equals("description")) {
                shortDescription = Html.fromHtml(readYahooNewsShortDescription(parser)).toString();
            } else if (name.equals("link")) {
                link = readYahooNewsLink(parser);
            } else if (name.equals("enclosure")) {
                imageUrl = readYahooNewsImageLink(parser);
            } else if (name.equals("pubDate")) {
                publicTime = readNewsPublicTime(parser);
            } else {
                skip(parser);
            }
        }
        return new NewsFeed(title, shortDescription, imageUrl, link, publicTime);

    }


    private static String readYahooNewsTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, NAMESPACE, "title");
        return title;
    }

    private static String readYahooNewsShortDescription(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "description");
        String shortDescription = readText(parser);
        parser.require(XmlPullParser.END_TAG, NAMESPACE, "description");
        return shortDescription;
    }

    private static String readYahooNewsLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, NAMESPACE, "link");
        return link;
    }

    private static String readYahooNewsImageLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        String imageUrl;
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "enclosure");
        imageUrl = parser.getAttributeValue(null, "url");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, NAMESPACE, "enclosure");
        return imageUrl;
    }

    private static String readNewsPublicTime(XmlPullParser parser) throws XmlPullParserException, IOException {
        String imageUrl;
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "pubDate");
        imageUrl = readText(parser);
        parser.require(XmlPullParser.END_TAG, NAMESPACE, "pubDate");
        return imageUrl;
    }


    private static String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String returnedText = "";
        if (parser.next() == XmlPullParser.TEXT) {
            returnedText = parser.getText();
            parser.nextTag();
        }
        return returnedText;
    }


    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int dept = 1;
        while (dept != 0) {
            // analyze the next tag
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    dept--;
                    break;
                case XmlPullParser.START_TAG:
                    dept++;
                    break;
            }
        }
    }


    // set up connection . a string presentation of url , return a input stream
    private static InputStream downloadUrl(final String url) throws IOException {
        URL connectionUrl = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) connectionUrl.openConnection();
        /*
            set up some properties
         */
        httpURLConnection.setReadTimeout(15000);
        httpURLConnection.setConnectTimeout(10000);
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setDoInput(true);
        // connect
        httpURLConnection.connect();
        return httpURLConnection.getInputStream();

    }

    public static ArrayList<NewsFeed> getFeedItemlistFromNetWork(final String url) throws XmlPullParserException, IOException {
        ArrayList<NewsFeed> returnedList = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = downloadUrl(url);
            returnedList = XmlParserFactory.parse(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        if (returnedList.size() > 0) {
            return returnedList;
        } else return null;
    }

}

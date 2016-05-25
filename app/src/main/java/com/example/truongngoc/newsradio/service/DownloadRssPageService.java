package com.example.truongngoc.newsradio.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.truongngoc.newsradio.model.NewsFeed;
import com.example.truongngoc.newsradio.xmlfactory.XmlParserFactory;

import java.util.ArrayList;

/**
 * Created by TruongNgoc on 16/11/2015.
 */
public class DownloadRssPageService extends IntentService {
    // TAG
    public static final String TAG = DownloadRssPageService.class.getSimpleName();
    public static final String RSS_URL = "request_rss_url";
    public static final String RESPONSE_LIST = "response_list";

    public DownloadRssPageService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        final String rssUrl = intent.getStringExtra(RSS_URL);
        ArrayList<NewsFeed> responseNewsFeedsList = null;
        try {
            Log.e(TAG, "service starts with url : " + rssUrl);
            responseNewsFeedsList = XmlParserFactory.getFeedItemlistFromNetWork(rssUrl);
        } catch (Exception e) {
            Log.e(TAG, "Could not download this page : " + rssUrl, e);
        }
        // return the result back to activity
        Intent broadcastIntent = new Intent(TAG);
        broadcastIntent.putParcelableArrayListExtra(RESPONSE_LIST, responseNewsFeedsList);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
}

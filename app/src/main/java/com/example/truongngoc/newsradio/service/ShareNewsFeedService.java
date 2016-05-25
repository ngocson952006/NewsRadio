package com.example.truongngoc.newsradio.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.truongngoc.newsradio.database.helper.helper.DatabaseHelper;
import com.example.truongngoc.newsradio.model.CloudDataAdapter;
import com.example.truongngoc.newsradio.model.NewsFeed;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by TruongNgoc on 21/11/2015.
 */
public class ShareNewsFeedService extends IntentService {
    // TAG
    public static final String TAG = ShareNewsFeedService.class.getSimpleName();
    // keys for intent
    public static final String SHARE_USER_ID = "share_user_id";
    public static final String NEWS_FEED_TO_SHARE = "news_feed_to_share";
    public static final String USER_SHARE_TITLE = "user_share_title";
    public static final String SHARED_FEED_VOTE = "share_feed_vote";

    public ShareNewsFeedService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get data from intent
        Bundle bundle = intent.getBundleExtra(NEWS_FEED_TO_SHARE);
        NewsFeed feedToShare = (NewsFeed) bundle.getParcelable(ShareNewsFeedService.NEWS_FEED_TO_SHARE);
        final String userId = bundle.getString(SHARE_USER_ID); // get user id of this share
        final String shareTitle = bundle.getString(USER_SHARE_TITLE);
        String responseId = CloudDataAdapter.NO_ID_FOUND; // this is the initial value
        JSONObject feedInJSONObject = new JSONObject();
        try {
            Log.e(TAG, userId);
            feedInJSONObject.put(SHARE_USER_ID, userId);
            feedInJSONObject.put(USER_SHARE_TITLE, shareTitle);
            feedInJSONObject.put(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[1], feedToShare.getTitle());
            feedInJSONObject.put(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[2], feedToShare.getContent());
            feedInJSONObject.put(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[3], feedToShare.getIllustrateImageLink());
            feedInJSONObject.put(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[4], feedToShare.getLink());
            String systemCurrentTime = DateFormat.getDateTimeInstance().format(new Date());
            feedInJSONObject.put(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[5], systemCurrentTime);
            feedInJSONObject.put(SHARED_FEED_VOTE, 0);
            // now start to connect to cloud and insert to this
            // first , check that this news was already shared by user
            if (CloudDataAdapter.isNewsAlreadyShared(userId, feedToShare.getLink())) {
                responseId = CloudDataAdapter.ID_ALREADY_SHARED;
            } else {
                // if this share is successful
                if (CloudDataAdapter.shareNewsInCloud(feedInJSONObject)) {
                    // start to get id from cloud
                    responseId = CloudDataAdapter.getShareId(feedToShare.getLink(), userId);
                } else {
                    // otherwise , put a signal that the share is failed
                    responseId = CloudDataAdapter.FAILED_SHARE_ID;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // broadcast to activity
        Intent broadcastIntent = new Intent(TAG);
        // put back the share id
        broadcastIntent.putExtra(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[0], responseId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
}

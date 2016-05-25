package com.example.truongngoc.newsradio.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by TruongNgoc on 21/10/2015.
 */
public class DownloadImageService extends IntentService {

    public static final String TAG = DownloadImageService.class.getName();
    public static final String IMAGE_URL = "image_url";
    public static final String RESPONSE_IMAGE_BITMAP = "response_image_bitmap";

    // constructor
    public DownloadImageService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String imageUrl = intent.getStringExtra(IMAGE_URL);
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            Log.e(TAG, "Starts the service !! " + url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (Exception e) {
            Log.e(TAG, "Could not download image with url : " + imageUrl, e);
            bitmap = null;
        }
        Intent broadcastIntent = new Intent(TAG);
        if (bitmap != null) {
            broadcastIntent.putExtra(DownloadImageService.RESPONSE_IMAGE_BITMAP, bitmap);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
}

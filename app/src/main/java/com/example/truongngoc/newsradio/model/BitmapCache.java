package com.example.truongngoc.newsradio.model;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by TruongNgoc on 19/11/2015.
 * class to save the current bitmap in cache for the later use
 * this will make the app fluid , fast-loading UI and improve performance
 */
public class BitmapCache {

    // TAG
    public static final String TAG = BitmapCache.class.getSimpleName();
    // the LruCache
    private LruCache<String, Bitmap> bitmapLruCache;

    public BitmapCache() {
        // first , initialize lru cache
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // use 1/8th of the available memory for this cache
        final int cacheMemory = maxMemory / 8;
        this.bitmapLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };

        // initialize disk cache in background thread
        // ... this to be developed for later version
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        // make sure that the key is valid
        if (this.getBitmapFromMemoryCache(key) == null) {
            this.bitmapLruCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return this.bitmapLruCache.get(key);
    }

}

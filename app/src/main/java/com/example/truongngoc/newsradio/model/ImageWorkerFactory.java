package com.example.truongngoc.newsradio.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.truongngoc.newsradio.R;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by TruongNgoc on 18/11/2015.
 * class to process all operations related to bitmap such as : laze loading , caching , bitmap task !
 */
public class ImageWorkerFactory {
    // TAG
    private static final String TAG = ImageWorkerFactory.class.getSimpleName();
    private static BitmapCache bitmapCache = new BitmapCache(); // for fast access to downloaded bitmap that users scrolled on

    // the actual load bitmap for image view
    public static void loadBitmap(Context context, String url, ImageView imageView) {
        // check if the key is valid
        if (ImageWorkerFactory.cancelPotentialWork(url, imageView)) {
            final Bitmap bitmap = bitmapCache.getBitmapFromMemoryCache(url); // unique url is also a nice key for in bitmap cache
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                final BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(imageView);
                final ImageWorkerFactory.AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(),
                        BitmapFactory.decodeResource(context.getResources(), R.drawable.news_icon),
                        bitmapWorkerTask);
                imageView.setImageDrawable(asyncDrawable);
                bitmapWorkerTask.execute(url);
            }
        }
    }

    public static class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final String TAG = BitmapWorkerTask.class.getName();
        // explanation : The WeakReference to the ImageView ensures that the AsyncTask does not prevent the ImageView and anything it references from being garbage collected.
        // reference link : http://developer.android.com/training/displaying-bitmaps/process-bitmap.html#BitmapWorkerTaskUpdated
        private WeakReference<ImageView> imageViewWeakReference;
        public String imageUrl;

        public BitmapWorkerTask(ImageView imageView) {
            this.imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }


        @Override
        protected Bitmap doInBackground(String... params) {
            this.imageUrl = params[0];
            // start to download image
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                Log.e(this.TAG, "Starts the service !! " + url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                // add this bitmap into cache for later accesses
                bitmapCache.addBitmapToMemoryCache(this.imageUrl, bitmap);
            } catch (Exception e) {
                Log.e(this.TAG, "Could not download image with url : " + imageUrl, e);
                bitmap = null;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // checks if the task is cancelled and if the current task matches the one associated with the ImageView
            if (isCancelled()) {
                bitmap = null;
            }
            if (this.imageViewWeakReference != null && bitmap != null) {
                final ImageView imageView = this.imageViewWeakReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }

            }
        }


    }


    public static boolean cancelPotentialWork(String url, ImageView imageView) {
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            String data = bitmapWorkerTask.imageUrl;
            if (data == null || !data.equals(url)) {
                // cancel the previous task
                bitmapWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }


    // A helper method, getBitmapWorkerTask(), is used above to retrieve the task associated with a particular ImageView:
    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkTask();
            }
        }
        return null;
    }

    //Create a dedicated Drawable subclass to store a reference back to the worker task.
    // In this case, a BitmapDrawable is used so that a placeholder image can be displayed in the ImageView while the task completes
    public static class AsyncDrawable extends BitmapDrawable {
        private WeakReference<BitmapWorkerTask> bitmapWorkTaskWeakReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkTask) {
            super(res, bitmap);
            this.bitmapWorkTaskWeakReference = new WeakReference<BitmapWorkerTask>(bitmapWorkTask);
        }


        public BitmapWorkerTask getBitmapWorkTask() {
            return this.bitmapWorkTaskWeakReference.get();
        }
    }
}

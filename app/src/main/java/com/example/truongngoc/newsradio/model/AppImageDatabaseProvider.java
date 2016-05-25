package com.example.truongngoc.newsradio.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import com.example.truongngoc.newsradio.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by TruongNgoc on 11/11/2015.
 */
public class AppImageDatabaseProvider {
    public static String PROFILE_ICON_NAME = "user_icon_profile";
    public static String WALLPAPER_PHOTO_NAME = "user_wall_paper";

    private Activity activity;

    public AppImageDatabaseProvider(Activity activity) {
        this.activity = activity;
    }

    public BitmapDrawable getUserProfileIconBitmapDrawable(ContainerSize size) {
        return this.decodedScaling(this.getUserProfilePhotoPath(), size, 0.0f);
    }

    public BitmapDrawable getWallpaperPhotoBitmapDrawable(ContainerSize size) {
        return this.decodedScaling(this.getUserWallpaperPhotoPath(), size, 0.0f);
    }

    // in case the container is limited memory
    // so we have to calculate to get the desired image size for the app bar layout
    private BitmapDrawable decodedScaling(String imagePath, ContainerSize size, float rotateAngle) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / size.getWidth(), photoH / size.getHeight());

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        // make sure that the file path is valid
        // otherwise , bitmap value is from default_user_icon
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(this.activity.getResources(), R.drawable.news_wallpapers, bmOptions);
        }
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.postRotate(rotateAngle);
        Bitmap rotationBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotationMatrix, true);
        return new BitmapDrawable(this.activity.getResources(), rotationBitmap);
    }

    public String getUserProfilePhotoPath() {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getString(PROFILE_ICON_NAME, null);
    }

    public String getUserWallpaperPhotoPath() {
        SharedPreferences sharedPreferences = this.activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getString(WALLPAPER_PHOTO_NAME, null);
    }

    // convert bitmap to byte array
    public static byte[] BitmapToByteArray(Bitmap value) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        value.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // convert byte array to bitmap
    public static Bitmap ByteArrayToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}

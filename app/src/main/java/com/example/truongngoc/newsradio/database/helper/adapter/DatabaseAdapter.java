package com.example.truongngoc.newsradio.database.helper.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.truongngoc.newsradio.R;
import com.example.truongngoc.newsradio.database.helper.helper.DatabaseHelper;
import com.example.truongngoc.newsradio.model.AppImageDatabaseProvider;
import com.example.truongngoc.newsradio.model.NewsFeed;
import com.example.truongngoc.newsradio.model.Profile;

/**
 * Created by TruongNgoc on 07/11/2015.
 */
public class DatabaseAdapter {
    // TAG
    public static final String TAG = DatabaseAdapter.class.getSimpleName();

    public static final int UPDATE_MODE = 1;
    public static final int INSERT_MODE = 2;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    // constructor
    public DatabaseAdapter(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }


    public Profile getUserProfile() {
        this.database = this.dbHelper.getReadableDatabase();
        Cursor cursor = this.database.query(DatabaseHelper.TABLE_USER_PROFILE, DatabaseHelper.TABLE_USER_PROFILE_COLUMNS, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        final Profile userProfile = cursorToProfile(cursor);
        // close the connection
        this.database.close();
        // close the cursor
        cursor.close();
        return userProfile;
    }

    public boolean insertNewUserProfile(Context context, final Profile userProfile) {
        this.database = this.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[0], userProfile.getUserId());
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[1], userProfile.getUserName());
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[2], userProfile.getAddress());
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[3], userProfile.getEmail());
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[4], userProfile.getPhoneNumber());
        // save default image provided by app for the fist time of user
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[5],
                AppImageDatabaseProvider.BitmapToByteArray(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_user_icon)));
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[6],
                AppImageDatabaseProvider.BitmapToByteArray(BitmapFactory.decodeResource(context.getResources(), R.drawable.news_wallpapers)));
        long numberOfInsertedRow = this.database.insert(DatabaseHelper.TABLE_USER_PROFILE, null, contentValues);
        Log.e(TAG, "inserted this profile , number of row :" + numberOfInsertedRow);
        this.database.close();
        return (numberOfInsertedRow > 0) ? true : false;
    }

    public boolean updateUserAddress(String newAddress) {
        this.database = this.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[2], newAddress);
        int numberOEffectRows = this.database.update(DatabaseHelper.TABLE_USER_PROFILE, contentValues, null, null);
        return (numberOEffectRows > 0) ? true : false;
    }

    public boolean updateUserEmailAddress(String newEmailAddress) {
        this.database = this.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[3], newEmailAddress);
        int numberOEffectRows = this.database.update(DatabaseHelper.TABLE_USER_PROFILE, contentValues, null, null);
        return (numberOEffectRows > 0) ? true : false;
    }

    public boolean updateUserProfileIcon(Bitmap bitmap) {
        this.database = this.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[5], AppImageDatabaseProvider.BitmapToByteArray(bitmap));
        int numberOEffectRows = this.database.update(DatabaseHelper.TABLE_USER_PROFILE, contentValues, null, null);
        return (numberOEffectRows > 0) ? true : false;
    }

    public boolean updateUserWallpaperPhoto(Bitmap bitmap) {
        this.database = this.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[6], AppImageDatabaseProvider.BitmapToByteArray(bitmap));
        int numberOEffectRows = this.database.update(DatabaseHelper.TABLE_USER_PROFILE, contentValues, null, null);
        return (numberOEffectRows > 0) ? true : false;
    }

    public boolean updateUserPhoneContact(String newPhoneContact) {
        this.database = this.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[4], newPhoneContact);
        int numberOEffectRows = this.database.update(DatabaseHelper.TABLE_USER_PROFILE, contentValues, null, null);
        this.database.close();
        return (numberOEffectRows > 0) ? true : false;
    }

    private boolean insertNewUserShare(NewsFeed feedToShare, String shareTime) {
        this.database = this.dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[0], "null"); // this field will be updated when this feed  is shared successfully in cloud
        contentValues.put(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[1], feedToShare.getTitle());
        contentValues.put(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[2], feedToShare.getContent());
        contentValues.put(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[3], feedToShare.getIllustrateImageLink());
        contentValues.put(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[4], feedToShare.getLink());
        //String systemTimeNow = DateFormat.getDateTimeInstance().format(new Date()); // get the current time
        contentValues.put(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[5], shareTime);
        // start to insert
        long insertedRowNumber = this.database.insert(DatabaseHelper.TABLE_USER_SHARED_NEWS, null, contentValues);
        // close the database connection
        return (insertedRowNumber > 0) ? true : false;
    }

    // convert cursor to profile
    private static Profile cursorToProfile(Cursor cursor) {
        final Profile profile = new Profile();
        profile.setUserId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[0])));
        profile.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[1])));
        profile.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[2])));
        profile.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[3])));
        profile.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[4])));
        profile.setProfileIconBitmap(AppImageDatabaseProvider.ByteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[5]))));
        profile.setWallpaperPhotoBitmap(AppImageDatabaseProvider.ByteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[6]))));
        //  profile.setIsMale((cursor.getString(cursor.getColumnIndex(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[0])).equals("male") ? true : false));
        return profile;
    }


}

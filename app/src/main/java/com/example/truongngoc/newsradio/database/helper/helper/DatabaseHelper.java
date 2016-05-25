package com.example.truongngoc.newsradio.database.helper.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by TruongNgoc on 07/11/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = DatabaseHelper.class.getSimpleName(); // tag for debugging
    private static final String DATABASE_NAME = "news_radio_database.db";
    private static final int DATABASE_VERSION = 1;
    /*
        about the tables
     */
    public static final String TABLE_USER_PROFILE = "table_user_profile";
    public static final String TABLE_USER_FRIENDS = "table_user_friends";
    public static final String TABLE_USER_SHARED_NEWS = "table_user_shared_news";
    public static final String[] TABLE_USER_PROFILE_COLUMNS = new String[]{
            "user_id", // each user will have unique id for management
            "user_name",
            "address",
            "email",
            "phone_number",
            "profile_icon",
            "wallpaper_photo"
    };
    public static final String[] TABLE_USER_FRIENDS_COLUMNS = new String[]{
            "friend_id", // base on friend id , the device will fetch profile from cloud
            "friend_name"
    };
    public static final String[] TABLE_USER_SHARED_NEWS_COLUMNS = new String[]{
            "share_id",
            "news_feed_title",
            "news_feed_description",
            "news_feed_image_link",
            "news_feed_learn_more_link",
            "time"
    };

    /*
           creation statements
   */

    private final String CREATE_TABLE_USER_PROFILE_STATEMENT = "CREATE TABLE " + TABLE_USER_PROFILE + " ( "
            + TABLE_USER_PROFILE_COLUMNS[0] + " TEXT NOT NULL PRIMARY KEY ,"
            + TABLE_USER_PROFILE_COLUMNS[1] + " TEXT NOT NULL ,"
            + TABLE_USER_PROFILE_COLUMNS[2] + " TEXT NOT NULL ,"
            + TABLE_USER_PROFILE_COLUMNS[3] + " TEXT NOT NULL ,"
            + TABLE_USER_PROFILE_COLUMNS[4] + " TEXT NOT NULL ,"
            + TABLE_USER_PROFILE_COLUMNS[5] + " BLOB NOT NULL ,"
            + TABLE_USER_PROFILE_COLUMNS[6] + " BLOB NOT NULL );";

    private final String CREATE_TABLE_USER_FRIENDS_STATEMENT = "CREATE TABLE " + TABLE_USER_FRIENDS + " ( "
            + TABLE_USER_FRIENDS_COLUMNS[0] + " TEXT NOT NULL PRIMARY KEY ,"
            + TABLE_USER_FRIENDS_COLUMNS[1] + " TEXT NOT NULL );";


    private final String CREATE_TABLE_USER_SHARED_NEWS_STATEMENT = "CREATE TABLE " + TABLE_USER_SHARED_NEWS + " ( "
            + TABLE_USER_SHARED_NEWS_COLUMNS[0] + " TEXT NOT NULL PRIMARY KEY ," +
            TABLE_USER_SHARED_NEWS_COLUMNS[1] + " TEXT NOT NULL ," +
            TABLE_USER_SHARED_NEWS_COLUMNS[2] + " TEXT NOT NULL ," +
            TABLE_USER_SHARED_NEWS_COLUMNS[3] + " TEXT NOT NULL ," +
            TABLE_USER_SHARED_NEWS_COLUMNS[4] + " TEXT NOT NULL ," +
            TABLE_USER_SHARED_NEWS_COLUMNS[5] + " TEXT NOT NULL );";

    // constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER_FRIENDS_STATEMENT);
        db.execSQL(CREATE_TABLE_USER_PROFILE_STATEMENT);
        db.execSQL(CREATE_TABLE_USER_SHARED_NEWS_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "upgrade database from version " + oldVersion + " to " + newVersion); // notify in console
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_SHARED_NEWS);
        // recreate
        this.onCreate(db);
    }
}

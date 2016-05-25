package com.example.truongngoc.newsradio.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.truongngoc.newsradio.database.helper.helper.DatabaseHelper;
import com.example.truongngoc.newsradio.model.CloudDataAdapter;
import com.example.truongngoc.newsradio.model.Profile;

import org.json.JSONObject;

/**
 * Created by TruongNgoc on 07/11/2
 * A service running background to connect to cloud and register new profile to database
 */
//public static final String[] TABLE_USER_PROFILE_COLUMNS = new String[]{
//        "user_id", // each user will have unique id for management
//        "user_name",
//        "birth_day",
//        "address",
//        "email",
//        "phone_number"
//        };
public class RegisterNewUserService extends IntentService {
    // TAG
    public static final String TAG = RegisterNewUserService.class.getSimpleName();
    // identifiers for intent
    public static final String NEW_USER_ID = "new_user_id";
    public static final String PHONE_NUMBER_AVAILABLE_FLAG = "phone_number_available_flag";
    public static final String USER_PROFILE_ICON_BASE64_STRING = "user_profile_icon_base64_string";
    public static final String USER_WALLPAPER_PHOTO_BASE64_STRING = "user_wallpaper_photo_base64_string";

    public RegisterNewUserService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final Profile newUserProfile = (Profile) intent.getParcelableExtra("new_user");
        boolean isNewPhoneContactExisted = false;
        String userId = null;

        Log.e(TAG, newUserProfile.toString());
        try {
            JSONObject jsonObject = new JSONObject();
            Log.e(TAG, jsonObject.toString());
            jsonObject.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[1], newUserProfile.getUserName());
            jsonObject.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[2], newUserProfile.getAddress());
            jsonObject.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[3], newUserProfile.getEmail());
            jsonObject.put(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[4], newUserProfile.getPhoneNumber());
            jsonObject.put(USER_PROFILE_ICON_BASE64_STRING, "null");
            jsonObject.put(USER_WALLPAPER_PHOTO_BASE64_STRING, "null");
            // first , check the phone contact is valid or invalid (in case this phone contact has been registered already)
            isNewPhoneContactExisted = CloudDataAdapter.isNewPhoneContactExisted(newUserProfile.getPhoneNumber());
            if (!isNewPhoneContactExisted) {
                CloudDataAdapter.addNewUserProfileToCloud(jsonObject); // connect to cloud and insert this record
                // get user id that cloud provided
                userId = CloudDataAdapter.getUserRecordIdInCloud(newUserProfile.getPhoneNumber());
            } else {
                userId = CloudDataAdapter.FAILED_REGISTER_ID; // send back a signal telling that the registration is not successful
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // broadcast to corresponding activity
        Intent registerBroadcastIntent = new Intent(TAG);
        registerBroadcastIntent.putExtra(RegisterNewUserService.NEW_USER_ID, userId); // put id here
        LocalBroadcastManager.getInstance(this).sendBroadcast(registerBroadcastIntent);
    }

}

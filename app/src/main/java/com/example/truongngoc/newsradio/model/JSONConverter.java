package com.example.truongngoc.newsradio.model;

import android.util.Log;

import com.example.truongngoc.newsradio.database.helper.helper.DatabaseHelper;
import com.example.truongngoc.newsradio.service.RegisterNewUserService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TruongNgoc on 09/11/2015.
 * class JSONConverter : convert data from cloud to application data structure
 */
public class JSONConverter {
    // TAG
    public static final String TAG = JSONConverter.class.getSimpleName();

    public static final String getShareId(JSONObject resultInObject) {
        // make sure that the response result is available
        if (JSONConverter.isResultAvailable(resultInObject)) {
            try {
                return resultInObject.getJSONArray("results").getJSONObject(0).getString("_id");
            } catch (JSONException e) {
                Log.e(TAG, "Could not get share id", e);
            }
        }
        // otherwise return the failed signal
        return CloudDataAdapter.NO_ID_FOUND;
    }

    public static final Profile jsonObjectToProfile(JSONObject object) {
        Profile convertedProfile = new Profile();
        try {
            JSONObject indexObject = object.getJSONArray("results").getJSONObject(0);
            convertedProfile.setUserId(indexObject.getString("_id"));
            convertedProfile.setUserName(indexObject.getString(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[1]));
            convertedProfile.setAddress(indexObject.getString(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[2]));
            convertedProfile.setEmail(indexObject.getString(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[3]));
            convertedProfile.setPhoneNumber(indexObject.getString(DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[4]));
            convertedProfile.setProfileIconBitmap(AppImageDatabaseProvider.StringToBitMap(indexObject.getString(RegisterNewUserService.USER_PROFILE_ICON_BASE64_STRING)));
            convertedProfile.setWallpaperPhotoBitmap(AppImageDatabaseProvider.StringToBitMap(indexObject.getString(RegisterNewUserService.USER_WALLPAPER_PHOTO_BASE64_STRING)));
        } catch (JSONException e) {
            e.printStackTrace();
            convertedProfile = null;
        }
        return convertedProfile;
    }

    public static boolean isResultAvailable(JSONObject object) {
        int hitsCount = 0; // the result count corresponding to the keyword
        try {
            hitsCount = Integer.valueOf(object.getString("hits"));
        } catch (JSONException e) {
            Log.e(TAG, "convert to JSONObject failed !");
        }
        return (hitsCount != 0) ? true : false;
    }


}

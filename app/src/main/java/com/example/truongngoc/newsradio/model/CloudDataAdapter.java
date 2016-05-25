package com.example.truongngoc.newsradio.model;

import android.util.Base64;
import android.util.Log;

import com.example.truongngoc.newsradio.database.helper.helper.DatabaseHelper;
import com.example.truongngoc.newsradio.service.ShareNewsFeedService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by TruongNgoc on 09/11/2015.
 * class to connect device to cloud and process data
 */
public class CloudDataAdapter {
    // TAG
    public static final String TAG = CloudDataAdapter.class.getSimpleName();
    // static for result from cloud
    public static final String FAILED_REGISTER_ID = "failed_register_id";
    public static final String FAILED_SHARE_ID = "failed_share_id";
    public static final String NO_ID_FOUND = "no_id_found";
    public static final String ID_ALREADY_SHARED = "id_already_shared";
    // about the cloud
    private static final String CLOUD_USERS_DATA_REST_URL = "https://api-eu.clusterpoint.com/v4/3338/news_radio_users/_query";
    private static final String CLOUD_SHARED_NEWS_REST_URL = "https://api-eu.clusterpoint.com/v4/3338/news_radio_shared_news/_query";
    private static final String CLOUD_ACCOUNT_STRING = "sonitdeveloper@gmail.com:sonthuong1995"; // cloud account , includes : user name and password
    private static final String BASIC_AUTHENTICATION_STRING = "Basic " + Base64.encodeToString(CLOUD_ACCOUNT_STRING.getBytes(), 0, CLOUD_ACCOUNT_STRING.length(), Base64.DEFAULT); // authentication string

    private static HttpURLConnection connectToCloudUsersData() {
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(CLOUD_USERS_DATA_REST_URL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Authorization", BASIC_AUTHENTICATION_STRING);
            httpURLConnection.setRequestProperty("accept-charset", "UTF-8");
            httpURLConnection.connect();
        } catch (Exception e) {
            Log.e(TAG, "Error in connecting to cloud", e);
        }
        return httpURLConnection;
    }

    private static HttpURLConnection connectToCloudSharedNews() {
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(CLOUD_SHARED_NEWS_REST_URL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Authorization", BASIC_AUTHENTICATION_STRING);
            httpURLConnection.setRequestProperty("accept-charset", "UTF-8");
            httpURLConnection.connect();
        } catch (Exception e) {
            Log.e(TAG, "Error in connecting to cloud", e);
        }
        return httpURLConnection;
    }


    public static boolean addNewUserProfileToCloud(final JSONObject userProfileInJsonObject) {
        final String commandStatement = "INSERT INTO news_radio_users JSON VALUE " + userProfileInJsonObject.toString();
        HttpURLConnection httpURLConnection = CloudDataAdapter.connectToCloudUsersData();
        if (httpURLConnection == null) {
            return false;
        }
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(commandStatement);
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // hard code ...
        } catch (Exception e) {
            Log.e(TAG, "Could not run this statement in cloud : " + commandStatement, e);
            return false;
        } finally {
            httpURLConnection.disconnect();
        }
        return true;
    }

    public static String getUserRecordIdInCloud(String phoneNumber) {
        final String commandStatement = "SELECT * FROM news_radio_users WHERE " + DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[4] + "==" + phoneNumber;
        String newUserId = null;
        HttpURLConnection httpURLConnection = CloudDataAdapter.connectToCloudUsersData();
        if (httpURLConnection == null) {
            return null;
        }
        try {
            Log.e(TAG, commandStatement);
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(commandStatement);
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // hard code ...
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer(); // to hold the response result from cloud
            String temporaryRead = null;
            while ((temporaryRead = bufferedReader.readLine()) != null) {
                stringBuffer.append(temporaryRead);
            }
            String resultInString = stringBuffer.toString();
            Log.e(TAG, resultInString); // show the result in console for testing
            final Profile newInsertedUserProfile = JSONConverter.jsonObjectToProfile(new JSONObject(resultInString));
            if (newInsertedUserProfile != null) {
                newUserId = newInsertedUserProfile.getUserId();
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not run this statement in cloud : " + commandStatement, e);
            return null;
        } finally {
            httpURLConnection.disconnect();
        }
        return newUserId;
    }


    public static Profile getUserProfileById(String userId) {
        final String commandStatement = "SELECT * FROM news_radio_users WHERE _id ==" + userId;
        Profile userProfile = null;
        HttpURLConnection httpURLConnection = CloudDataAdapter.connectToCloudUsersData();
        if (httpURLConnection == null) {
            return null;
        }
        try {
            Log.e(TAG, commandStatement);
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(commandStatement);
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // hard code ...
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer(); // to hold the response result from cloud
            String temporaryRead = null;
            while ((temporaryRead = bufferedReader.readLine()) != null) {
                stringBuffer.append(temporaryRead);
            }
            String resultInString = stringBuffer.toString();
            Log.e(TAG, resultInString); // show the result in console for testing
            userProfile = JSONConverter.jsonObjectToProfile(new JSONObject(resultInString));
        } catch (Exception e) {
            Log.e(TAG, "Could not run this statement in cloud : " + commandStatement, e);
            return null;
        } finally {
            httpURLConnection.disconnect();
        }
        return userProfile;
    }

    public static boolean updateUserFieldInProfile(String userId, String fieldName, String newValue) {
        HttpURLConnection httpURLConnection = CloudDataAdapter.connectToCloudUsersData();
        if (httpURLConnection == null) {
            return false;
        }
        final String commandStatement = "UPDATE news_radio_users[\"" + userId + "\"] SET " + fieldName + "=\"" + newValue + "\"";
        try {
            Log.e(TAG, commandStatement);
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(commandStatement);
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // hard code ...
        } catch (Exception e) {
            Log.e(TAG, "Could not run this statement in cloud : " + commandStatement, e);
            return false;
        } finally {
            httpURLConnection.disconnect();
        }
        return true;
    }

    // to be implemented !!!!!!!!!
    public static boolean isNewPhoneContactExisted(String phoneNumber) {
        HttpURLConnection httpURLConnection = CloudDataAdapter.connectToCloudUsersData();
        StringBuffer stringBuffer = new StringBuffer(); // to hold the response result
        boolean testedResult = false;
        if (httpURLConnection == null) {
            return false;
        }
        final String commandStatement = "SELECT * FROM news_radio_users WHERE " + DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[4] + "==" + "\"" + phoneNumber + '\"';
        try {
            Log.e(TAG, commandStatement);
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(commandStatement);
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // hard code ...
            String temporaryRead;
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((temporaryRead = bufferedReader.readLine()) != null) {
                stringBuffer.append(temporaryRead);
            }
            String resultInString = stringBuffer.toString();
            Log.e(TAG, resultInString); // show the result in console for testing
        } catch (Exception e) {
            Log.e(TAG, "Could not run this statement in cloud : " + commandStatement, e);
            return false;
        } finally {
            httpURLConnection.disconnect();
        }
        try {
            JSONObject checkedObject = new JSONObject(stringBuffer.toString());
            testedResult = JSONConverter.isResultAvailable(checkedObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testedResult; // return the tested result
    }


    public static boolean shareNewsInCloud(final JSONObject shareNewsFeedInJson) {
        HttpURLConnection shareNewsHttpURLConnection = CloudDataAdapter.connectToCloudSharedNews();
        if (shareNewsHttpURLConnection == null) {
            return false;
        }
        final String commandStatement = "INSERT INTO news_radio_shared_news JSON VALUE " + shareNewsFeedInJson.toString();
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(shareNewsHttpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(commandStatement);
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream = shareNewsHttpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // hard code ...
        } catch (Exception e) {
            Log.e(TAG, "Could not run this statement in cloud : " + commandStatement, e);
            return false;
        } finally {
            shareNewsHttpURLConnection.disconnect();
        }
        return true;
    }

    public static String getShareId(String newsUrl, String userId) {
        HttpURLConnection httpURLConnection = CloudDataAdapter.connectToCloudSharedNews();
        StringBuffer stringBuffer = new StringBuffer(); // to hold the response result
        boolean testedResult = false;
        if (httpURLConnection == null) {
            return CloudDataAdapter.FAILED_SHARE_ID;
        }
        final String commandStatement = "SELECT * FROM news_radio_shared_news WHERE ( " + ShareNewsFeedService.SHARE_USER_ID + "==" + "\"" + userId + "\""
                + " && " + DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[4] + "==" + "\"" + newsUrl + "\" )";
        try {
            Log.e(TAG, commandStatement);
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(commandStatement);
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // hard code ...
            String temporaryRead;
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((temporaryRead = bufferedReader.readLine()) != null) {
                stringBuffer.append(temporaryRead);
            }
            String resultInString = stringBuffer.toString();
            Log.e(TAG, resultInString); // show the result in console for testing
        } catch (Exception e) {
            Log.e(TAG, "Could not run this statement in cloud : " + commandStatement, e);
            return CloudDataAdapter.NO_ID_FOUND;
        } finally {
            httpURLConnection.disconnect();
        }
        try {
            JSONObject checkedObject = new JSONObject(stringBuffer.toString());
            return JSONConverter.getShareId(checkedObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CloudDataAdapter.NO_ID_FOUND; // return the tested result
    }


    public static boolean isNewsAlreadyShared(String userId, String newsUrl) {
        int hitCount = 0;
        HttpURLConnection httpURLConnection = CloudDataAdapter.connectToCloudSharedNews();
        StringBuffer stringBuffer = new StringBuffer(); // to hold the response result
        boolean testedResult = false;
        if (httpURLConnection == null) {
            return true;
        }
        final String commandStatement = "SELECT * FROM news_radio_shared_news WHERE ( " + ShareNewsFeedService.SHARE_USER_ID + "==" + "\"" + userId + "\""
                + " && " + DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[4] + "==" + "\"" + newsUrl + "\" )";
        try {
            Log.e(TAG, commandStatement);
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(commandStatement);
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // hard code ...
            String temporaryRead;
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((temporaryRead = bufferedReader.readLine()) != null) {
                stringBuffer.append(temporaryRead);
            }
            String resultInString = stringBuffer.toString();
            Log.e(TAG, resultInString); // show the result in console for testing
        } catch (Exception e) {
            Log.e(TAG, "Could not run this statement in cloud : " + commandStatement, e);
            return true;
        } finally {
            httpURLConnection.disconnect();
        }
        try {
            JSONObject checkedObject = new JSONObject(stringBuffer.toString());
            hitCount = Integer.valueOf(checkedObject.getString("hits"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (hitCount > 0) ? true : false;

    }
}

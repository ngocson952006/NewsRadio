package com.example.truongngoc.newsradio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.truongngoc.newsradio.database.helper.adapter.DatabaseAdapter;
import com.example.truongngoc.newsradio.model.ApplicationChecker;
import com.example.truongngoc.newsradio.model.CloudDataAdapter;
import com.example.truongngoc.newsradio.model.Profile;
import com.example.truongngoc.newsradio.service.RegisterNewUserService;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * Created by TruongNgoc on 07/11/2015.
 */
public class RegisterActivity extends Activity {
    // TAG
    public static final String TAG = RegisterActivity.class.getSimpleName();
    private DatabaseAdapter databaseAdapter;
    private ProgressDialog progressDialog;
    private TextToSpeech textToSpeech;
    private final Profile userProfile = new Profile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.registration_activity);
        this.databaseAdapter = new DatabaseAdapter(this);
        // get view group components
        final EditText editTextUserName = (EditText) this.findViewById(R.id.editTextUserNameRegister);
        final EditText editTextUserAdders = (EditText) this.findViewById(R.id.editTextUserAddressRegister);
        final EditText editTextUserEmailAddress = (EditText) this.findViewById(R.id.editTextUserEmailRegister);
        final EditText editTextUserPhoneNumber = (EditText) this.findViewById(R.id.editTextUserPhoneNumberRegister);
        this.progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        this.progressDialog.setMessage(getString(R.string.processing));
        this.textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });
        final Button buttonRegister = (Button) this.findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make sure that the device connects to the internet
                if (ApplicationChecker.isInternetAvailable(RegisterActivity.this)) {
                    // start to process this registration async includes : upload this profile to cloud , save this profile into database
                    // first check input data is valid
                    String userName = editTextUserName.getText().toString();
                    String userAddress = editTextUserAdders.getText().toString();
                    String userEmailAddress = editTextUserEmailAddress.getText().toString();
                    String userPhoneNumber = editTextUserPhoneNumber.getText().toString();
                    // make sure that input data is valid
                    if (userName.length() == 0 || userAddress.length() == 0
                            || userPhoneNumber.length() == 0
                            || userEmailAddress.length() == 0) {

                        Snackbar.make(v, getString(R.string.invalid_information), Snackbar.LENGTH_LONG).setAction("Error", null).show();
                        textToSpeech.speak(getString(R.string.invalid_information), TextToSpeech.QUEUE_FLUSH, null);
                        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1000); // vibrate for 1 second

                    } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmailAddress).matches()) { // invalidate email

                        Snackbar.make(v, getString(R.string.invalid_email_address), Snackbar.LENGTH_LONG).setAction("Error", null).show();
                        textToSpeech.speak(getString(R.string.invalid_email_address), TextToSpeech.QUEUE_FLUSH, null);
                        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1000);

                    } else {

                        progressDialog.show();
                        textToSpeech.speak("Hello " + userName.split(" ")[0] + " ." + getString(R.string.please_wait), TextToSpeech.QUEUE_FLUSH, null);

                        userProfile.setUserName(userName);
                        userProfile.setEmail(userEmailAddress);
                        userProfile.setPhoneNumber(userPhoneNumber);
                        userProfile.setAddress(userAddress);
                        userProfile.setProfileIconBitmap(null);
                        userProfile.setWallpaperPhotoBitmap(null);
                        // start service
                        Intent registerNewUserService = new Intent(RegisterActivity.this, RegisterNewUserService.class);
                        registerNewUserService.putExtra("new_user", userProfile);
                        startService(registerNewUserService); // android !!
                    }
                } else {
                    ApplicationChecker.showConnectionFailedDialog(RegisterActivity.this);
                }
            }
        });
        // register a receiver
        IntentFilter intentFilterRegistrationReceiver = new IntentFilter(RegisterNewUserService.TAG);
        RegisterResultReceiver registerResultReceiver = new RegisterResultReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(registerResultReceiver, intentFilterRegistrationReceiver);
    }

    private class RegisterResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RegisterNewUserService.TAG)) {
                // start to insert into database
                String userId;
                // analyze user id , in case error occurred
                if (!(userId = intent.getStringExtra(RegisterNewUserService.NEW_USER_ID)).equals(CloudDataAdapter.FAILED_REGISTER_ID)) {
                    userProfile.setUserId(userId);
                    // notify to user that the registration is successful and change to main screen
                    //textToSpeech.speak(getString(R.string.successful_registration), TextToSpeech.QUEUE_FLUSH, null);
                    // save into device database and move to main activity
                    new RegisterNewUserAsync().execute(userProfile);
                } else {
                    // close the progress dialog
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    // show error dialog to user
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setIcon(R.drawable.error_icon)
                            .setTitle(R.string.invalid_phone_contact_title)
                            .setMessage(getString(R.string.invalid_phone_contact));
                    // show the dialog
                    builder.create().show();
                    // notify to user via text to speech
                    textToSpeech.speak(getString(R.string.invalid_phone_contact), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }


    private class RegisterNewUserAsync extends AsyncTask<Profile, Void, String> {
        private final String TAG = RegisterNewUserAsync.class.getSimpleName();

        // constructor
        public RegisterNewUserAsync() {

        }

        @Override
        protected String doInBackground(Profile... params) {
            databaseAdapter.insertNewUserProfile(RegisterActivity.this, params[0]);
            return "process_ok";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            // end this activity
            Intent mainScreenIntent = new Intent();
            mainScreenIntent.putExtra("new_user_profile", userProfile);
            setResult(RESULT_OK, mainScreenIntent);
            // finish
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.textToSpeech.shutdown();
    }
}

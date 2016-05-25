package com.example.truongngoc.newsradio;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.truongngoc.newsradio.database.helper.adapter.DatabaseAdapter;
import com.example.truongngoc.newsradio.database.helper.helper.DatabaseHelper;
import com.example.truongngoc.newsradio.model.AppImageDatabaseProvider;
import com.example.truongngoc.newsradio.model.CloudDataAdapter;
import com.example.truongngoc.newsradio.model.ContainerSize;
import com.example.truongngoc.newsradio.model.Profile;
import com.example.truongngoc.newsradio.service.RegisterNewUserService;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static com.example.truongngoc.newsradio.model.AppImageDatabaseProvider.WALLPAPER_PHOTO_NAME;

public class UserProfileActivity extends AppCompatActivity {
    // TAG
    public static final String TAG = UserProfileActivity.class.getSimpleName();

    private static final int ADDRESS_EDIT_MODE = 1;
    private static final int EMAIL_ADDRESS_EDIT_MODE = 2;
    private static final int PHONE_CONTACT_EDIT_MODE = 3;
    private static final int UPDATE_ICON_PROFILE_MODE = 4;
    private static final int UPDATE_WALLPAPER_PHOTO_MODE = 5;
    private static final int TAKE_PROFILE_PHOTO_REQUEST_CODE = 1;
    private static final int TAKE_WALLPAPER_PHOTO_REQUEST_CODE = 2;
    // class fields
    private Profile userProfile;
    volatile String temporarySaver;
    private TextToSpeech textToSpeech;
    private AppImageDatabaseProvider imageProvider;
    //private String profileImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/" + FILE_NAME + ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        this.imageProvider = new AppImageDatabaseProvider(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //  CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        //toolBarLayout.setTitle(getTitle());
        this.setTitle("FUCKING BABY");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                // open device camera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(WALLPAPER_PHOTO_NAME);
                    } catch (IOException e) {
                        Log.e(TAG, "failed to create image file", e);
                    }
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, TAKE_WALLPAPER_PHOTO_REQUEST_CODE);
                    }
                }

            }
            // open device
        });

        // load use profile async
        new LoadUserDataAsync().execute();
        final ImageButton imageButtonEditUserAddress = (ImageButton) this.findViewById(R.id.imageButtonEditUserProfileAddress);
        imageButtonEditUserAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditingDialogToUser(ADDRESS_EDIT_MODE);
            }
        });
        final ImageButton imageButtonEditUserPhoneContact = (ImageButton) this.findViewById(R.id.imageButtonEditUserProfilePhoneContact);
        imageButtonEditUserPhoneContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditingDialogToUser(PHONE_CONTACT_EDIT_MODE);
            }
        });
        final ImageButton imageButtonEditUserEmailAddress = (ImageButton) this.findViewById(R.id.imageButtonEditUserEmailAddress);
        imageButtonEditUserEmailAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditingDialogToUser(EMAIL_ADDRESS_EDIT_MODE);
            }
        });
        this.textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        final ImageButton imageButtonEditUserProfileIcon = (ImageButton) this.findViewById(R.id.imageButtonEditUserIconProfile);
        imageButtonEditUserProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open device camera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(AppImageDatabaseProvider.PROFILE_ICON_NAME);
                    } catch (IOException e) {
                        Log.e(TAG, "failed to create image file", e);
                    }
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, TAKE_PROFILE_PHOTO_REQUEST_CODE);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        int width = appBarLayout.getWidth();
        int height = appBarLayout.getHeight();
        ContainerSize containerSize =new ContainerSize(width ,height);
        if (requestCode == TAKE_WALLPAPER_PHOTO_REQUEST_CODE) {
            // Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            // get image path
            this.setAppBarLayoutBackgroundWithScaling();
            // update to cloud
           // String filePath = this.imageProvider.getUserProfilePhotoPath();
            this.temporarySaver = AppImageDatabaseProvider.BitMapToString(this.imageProvider.getWallpaperPhotoBitmapDrawable(containerSize).getBitmap());
            // start to update this image into cloud
            new UpdateUserProfile().execute(UPDATE_ICON_PROFILE_MODE);

        } else if (requestCode == TAKE_PROFILE_PHOTO_REQUEST_CODE) {
            this.setImageViewSourceWithScaling();
            //String filePath = this.imageProvider.getUserWallpaperPhotoPath();
            this.temporarySaver = AppImageDatabaseProvider.BitMapToString(this.imageProvider.getUserProfileIconBitmapDrawable(containerSize).getBitmap());
            // start to update this image into cloud
            new UpdateUserProfile().execute(UPDATE_WALLPAPER_PHOTO_MODE);
        }
    }

    private void setUpViewComponentsValue() {
        final TextView textViewUserEmailAddress = (TextView) this.findViewById(R.id.textViewProfileEmailAddress);
        textViewUserEmailAddress.setText(this.userProfile.getEmail());
        final TextView textViewUserPhoneContact = (TextView) this.findViewById(R.id.textViewProfilePhoneContact);
        textViewUserPhoneContact.setText(this.userProfile.getPhoneNumber());
        final TextView textViewUserAddress = (TextView) this.findViewById(R.id.textViewProfileAddress);
        textViewUserAddress.setText(this.userProfile.getAddress());
        final TextView textViewUserName = (TextView) this.findViewById(R.id.textViewProfileUserName);
        textViewUserName.setText(this.userProfile.getUserId());
        this.setAppBarLayoutBackgroundWithScaling();
        this.setImageViewSourceWithScaling();
    }


    private void setAppBarLayoutBackgroundWithScaling() {
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        int width = appBarLayout.getWidth();
        int height = appBarLayout.getHeight();
        appBarLayout.setBackground(this.imageProvider.getWallpaperPhotoBitmapDrawable(new ContainerSize(width, height)));
    }

    private void setImageViewSourceWithScaling() {
        final ImageView imageViewProfileIcon = (ImageView) this.findViewById(R.id.imageViewProfileIcon);
        int width = imageViewProfileIcon.getWidth();
        int height = imageViewProfileIcon.getHeight();
        imageViewProfileIcon.setBackground(this.imageProvider.getUserProfileIconBitmapDrawable(new ContainerSize(width, height)));
    }

    private File createImageFile(String fileName) throws IOException {
        // String fileCreateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDirectory = Environment.getExternalStorageDirectory();
        File imageFile = File.createTempFile(fileName, ".jpg", storageDirectory);
        // save current file path
        SharedPreferences sharedPreferences = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(fileName, imageFile.getAbsolutePath());
        editor.commit();
        return imageFile;
    }

    private void showEditingDialogToUser(final int mode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_profile_dialog_layout, null);
        builder.setView(dialogView);
        final EditText editText = (EditText) dialogView.findViewById(R.id.editTextNewContent);
        switch (mode) {
            case ADDRESS_EDIT_MODE:
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                break;
            case PHONE_CONTACT_EDIT_MODE:
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case EMAIL_ADDRESS_EDIT_MODE:
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                break;
            default:
                break;
        }
        ;
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        })
                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        temporarySaver = editText.getText().toString();
                        switch (mode) {
                            case ADDRESS_EDIT_MODE:
                                new UpdateUserProfile().execute(ADDRESS_EDIT_MODE);
                                break;
                            case PHONE_CONTACT_EDIT_MODE:
                                new UpdateUserProfile().execute(PHONE_CONTACT_EDIT_MODE);
                                break;
                            case EMAIL_ADDRESS_EDIT_MODE:
                                new UpdateUserProfile().execute(EMAIL_ADDRESS_EDIT_MODE);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private class LoadUserDataAsync extends AsyncTask<Void, Void, String> {
        public LoadUserDataAsync() {
            // empty
        }

        @Override
        protected String doInBackground(Void... params) {
            userProfile = new DatabaseAdapter(UserProfileActivity.this).getUserProfile();

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (userProfile != null) {
                setUpViewComponentsValue();
//                CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//                toolBarLayout.setTitle(userProfile.getUserName());
            }
        }
    }

    // to be continued .....
    private class UpdateUserProfile extends AsyncTask<Integer, Void, String> {

        private ProgressDialog progressDialog;

        // constructor
        public UpdateUserProfile() {
            this.progressDialog = new ProgressDialog(UserProfileActivity.this, ProgressDialog.STYLE_SPINNER);
            this.progressDialog.setMessage(getString(R.string.waiting_for_update));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textToSpeech.speak(getString(R.string.waiting_for_update), TextToSpeech.QUEUE_FLUSH, null);
            this.progressDialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {
            switch (params[0]) {
                case ADDRESS_EDIT_MODE:
                    if (CloudDataAdapter.updateUserFieldInProfile(userProfile.getUserId(), DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[2], temporarySaver)) {
                        // start to update in device database if the process from cloud was successful
                        if (new DatabaseAdapter(UserProfileActivity.this).updateUserAddress(temporarySaver)) {
                            userProfile.setAddress(temporarySaver);
                            return "process_ok";
                        }
                    }
                    break;
                case EMAIL_ADDRESS_EDIT_MODE:
                    if (CloudDataAdapter.updateUserFieldInProfile(userProfile.getUserId(), DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[3], temporarySaver)) {
                        // start to update in device database if the process from cloud was successful
                        if (new DatabaseAdapter(UserProfileActivity.this).updateUserEmailAddress(temporarySaver)) {
                            userProfile.setEmail(temporarySaver);
                            return "process_ok";
                        }
                    }
                    break;
                case PHONE_CONTACT_EDIT_MODE:
                    if (CloudDataAdapter.updateUserFieldInProfile(userProfile.getUserId(), DatabaseHelper.TABLE_USER_PROFILE_COLUMNS[4], temporarySaver)) {
                        // start to update in device database if the process from cloud was successful
                        if (new DatabaseAdapter(UserProfileActivity.this).updateUserPhoneContact(temporarySaver)) {
                            userProfile.setPhoneNumber(temporarySaver);
                            return "process_ok";
                        }
                    }
                    break;
                case UPDATE_ICON_PROFILE_MODE:
                    if (CloudDataAdapter.updateUserFieldInProfile(userProfile.getUserId(), RegisterNewUserService.USER_PROFILE_ICON_BASE64_STRING, temporarySaver)) {
                        return "process_ok";
                    }
                    break;
                case UPDATE_WALLPAPER_PHOTO_MODE:
                    if (CloudDataAdapter.updateUserFieldInProfile(userProfile.getUserId(), RegisterNewUserService.USER_WALLPAPER_PHOTO_BASE64_STRING, temporarySaver)) {
                        return "process_ok";
                    }
                    break;
                default:
                    break;

            }
            return "process_failed";
        }

        @Override
        protected void onPostExecute(String processState) {
            super.onPostExecute(processState);
            if (processState.equals("process_ok")) {
                textToSpeech.speak(userProfile.getUserName() + ", we processed your request !", TextToSpeech.QUEUE_FLUSH, null);
                this.progressDialog.dismiss();
                setUpViewComponentsValue(); // re setup view components
            } else {
                textToSpeech.speak("What the fuck ?", TextToSpeech.QUEUE_FLUSH, null);
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.textToSpeech.stop();
        this.textToSpeech.shutdown();
    }

}

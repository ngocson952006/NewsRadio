package com.example.truongngoc.newsradio;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.truongngoc.newsradio.database.helper.adapter.DatabaseAdapter;
import com.example.truongngoc.newsradio.model.AppImageDatabaseProvider;
import com.example.truongngoc.newsradio.model.ApplicationChecker;
import com.example.truongngoc.newsradio.model.ContainerSize;
import com.example.truongngoc.newsradio.model.Profile;
import com.example.truongngoc.newsradio.model.UserChooseItem;
import com.example.truongngoc.newsradio.model.UserChooseItemFactory;
import com.example.truongngoc.newsradio.ui.adapter.MainGridviewAdapter;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    // TAG
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int REGISTRATION_REQUEST_CODE = 1;
    public static final String USER_PROFILE = "user_profile";
    public static final String USER_NAME = "user_name";
    // app version
    public static final String APP_VERSION = "1.0.0";
    private GridView userChooseItemsGridview;
    private MainGridviewAdapter gridviewAdapter;
    private TextView textViewAppVersion;

    // technologies
    private TextToSpeech textToSpeech; // to interact to user
    private DatabaseAdapter databaseAdapter;
    private Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        this.databaseAdapter = new DatabaseAdapter(this); // initialize database adapter
//        if ((this.userProfile = this.databaseAdapter.getUserProfile()) == null) {
//            // change to register activity
//            Intent registerIntent = new Intent(this, RegisterActivity.class);
//            this.startActivityForResult(registerIntent, REGISTRATION_REQUEST_CODE);
//        }
        // if this is the first use time of user , change to registration screen
        // greeting to user
        this.textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // set language
                textToSpeech.setLanguage(new Locale("vi", "VI"));
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // start to load user data asynchronously
        new LoadUserDataAsync().execute();
        // initialize adapter
        this.gridviewAdapter = new MainGridviewAdapter(this, R.layout.gridview_item, UserChooseItemFactory.getAppMainUserChooseItems(this));
        this.userChooseItemsGridview = (GridView) this.findViewById(R.id.gridViewUserChooseItems);
        this.userChooseItemsGridview.setAdapter(this.gridviewAdapter); // set adapter
        // register item click listener
        this.userChooseItemsGridview.setOnItemClickListener(this);
        this.textViewAppVersion = (TextView) this.findViewById(R.id.textViewVersion);
        this.textViewAppVersion.setText(this.getString(R.string.app_version) + APP_VERSION);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!ApplicationChecker.isInternetAvailable(this)) {
            ApplicationChecker.showConnectionFailedDialog(this);
        }
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.textToSpeech.shutdown();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // this.textToSpeech.speak("Hello. Have a nice day", TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.navigation_my_profile:
                Intent seeProfileIntent = new Intent(this, UserProfileActivity.class);
                this.startActivity(seeProfileIntent);
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // analyze what user chose
        UserChooseItem chosenItem = (UserChooseItem) parent.getItemAtPosition(position);
        String label = chosenItem.getLabel();
        if (label.equals(getString(R.string.read_news))) {
            Intent readNewsIntent = new Intent(this, CategoriesNewsActivity.class);
            // put user profile here
            readNewsIntent.putExtra(MainActivity.USER_NAME, userProfile.getUserName());
            startActivity(readNewsIntent);
//        } else if (label.equals(getString(R.string.app_information))) {
//            this.textToSpeech.speak("Bài hát Tôi Là Người Việt Nam là một bài hát về niềm tự hào và mong muốn khám phá vẻ đẹp quê hương đất nước của một người con Việt", TextToSpeech.QUEUE_ADD, null);
//        }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // analyze request code
            switch (requestCode) {
                case REGISTRATION_REQUEST_CODE:
                    final Profile newUserProfile = (Profile) data.getParcelableExtra("new_user_profile");
                    setUpNavigationViewData(newUserProfile);
                    this.textToSpeech.speak(this.getString(R.string.successful_registration), TextToSpeech.QUEUE_FLUSH, null);
                    break;
            }
        }
    }


    private class LoadUserDataAsync extends AsyncTask<Void, Void, String> {
        public LoadUserDataAsync() {

        }

        @Override
        protected String doInBackground(Void... params) {
            userProfile = databaseAdapter.getUserProfile();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (userProfile == null) {
                // change to register activity
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivityForResult(registerIntent, REGISTRATION_REQUEST_CODE);
            } else {
                setUpNavigationViewData(userProfile);
            }
        }
    }

    private void setUpNavigationViewData(final Profile profile) {
        // profile icon
        final ImageView profileIconNavigationView = (ImageView) findViewById(R.id.imageViewNavigationUserIcon);
        //   int containerWidth = profileIconNavigationView.getWidth();
        // int containerHeight = profileIconNavigationView.getHeight();
        ContainerSize containerSize = new ContainerSize(100, 100);
        // get size of the image view , then get fit bitmap for this size from database
        profileIconNavigationView.setImageDrawable((new AppImageDatabaseProvider(this)).getUserProfileIconBitmapDrawable(containerSize));
        // set up data for navigation view
//        final ImageView userDefaultIcon = (ImageView) findViewById(R.id.imageViewUserDefaultIcon);
//        userDefaultIcon.setImageResource(R.drawable.default_user_icon);
        final TextView textViewNavigationUserName = (TextView) findViewById(R.id.textViewNavigationUserName);
        textViewNavigationUserName.setText(profile.getUserName());
        final TextView textViewNavigationUserEmail = (TextView) findViewById(R.id.textViewNavigationUseEmail);
        textViewNavigationUserEmail.setText(profile.getEmail());
    }

}

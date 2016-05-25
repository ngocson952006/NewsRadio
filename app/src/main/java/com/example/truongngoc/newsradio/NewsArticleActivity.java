package com.example.truongngoc.newsradio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.truongngoc.newsradio.model.BitmapCache;
import com.example.truongngoc.newsradio.model.NewsFeed;
import com.example.truongngoc.newsradio.service.ShareNewsFeedService;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class NewsArticleActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // TAG
    public static final String TAG = NewsArticleActivity.class.getSimpleName();

    private TextToSpeech textToSpeech;
    private NewsFeed desiredNewsFeed;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_article);
        FacebookSdk.sdkInitialize(this);
        this.callbackManager = CallbackManager.Factory.create();
        this.shareDialog = new ShareDialog(this);
        // get data from intent
        Intent intent = this.getIntent();
        this.desiredNewsFeed = (NewsFeed) intent.getParcelableExtra(NewsActivity.USER_DESIRED_NEWS_FEED);
        String header = intent.getStringExtra(NewsActivity.NEWS_HEADER);
        // set header
        final TextView textViewHeader = (TextView) this.findViewById(R.id.textViewArticleCategory);
        textViewHeader.setText(header);
        // set content of article to UI
        final TextView textViewArticleTitle = (TextView) this.findViewById(R.id.textViewArticleTitle);
        textViewArticleTitle.setText(this.desiredNewsFeed.getTitle());
        final TextView textViewArticleDescription = (TextView) this.findViewById(R.id.textViewArticleDetail);
        textViewArticleDescription.setText(this.desiredNewsFeed.getContent());
        final TextView textViewArticlePublicTime = (TextView) this.findViewById(R.id.textViewArticlePublicTime);
        textViewArticlePublicTime.setText(this.desiredNewsFeed.getPublicTime());
        final TextView textViewArticleLearnMoreLink = (TextView) this.findViewById(R.id.textViewArticleLearnMoreLink);
        textViewArticleLearnMoreLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to the web view for more details
                Uri articleUri = Uri.parse(desiredNewsFeed.getLink());
                Intent articleMoreDetailsIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                if (articleMoreDetailsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(articleMoreDetailsIntent);
                }
            }
        });
        // initialize text to speech
        this.textToSpeech = new TextToSpeech(this, this);
        // start to set article's image in background
        new SetUpLayoutBarAsync().execute(this.desiredNewsFeed.getIllustrateImageLink());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_article);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // first , show the dialog to users , so they can choose where that news should be shared
                // News Radio supply two providers to share : facebook and NewsRadio forums
                AlertDialog.Builder shareProvidersBuilder = new AlertDialog.Builder(NewsArticleActivity.this);
                shareProvidersBuilder.setTitle(R.string.share);
                shareProvidersBuilder.setItems(R.array.share_providers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            // share on facebook
                            case 0:
                                if (desiredNewsFeed != null) {
                                    if (shareDialog.canShow(ShareLinkContent.class)) {
                                        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                                                .setContentDescription("Hello")
                                                .setContentUrl(Uri.parse(desiredNewsFeed.getLink()))
                                                        // .setImageUrl(Uri.parse(desiredNewsFeed.getIllustrateImageLink()))
                                                .build();
                                        shareDialog.show(shareLinkContent);
                                    }
                                } else {
                                    Toast.makeText(NewsArticleActivity.this, "NULL", Toast.LENGTH_LONG).show();
                                }

                                break;
                            // share NewsRadio forums
                            case 1:
                                Intent shareNewsIntent = new Intent(NewsArticleActivity.this, ShareNewsActivity.class);
//                // put the share information
                                //  shareNewsIntent.putExtra(ShareNewsFeedService.SHARE_USER_ID, userProfile.getUserId());
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(ShareNewsFeedService.NEWS_FEED_TO_SHARE, desiredNewsFeed);
                                // bundle.putString(ShareNewsFeedService.SHARE_USER_ID, userProfile.getUserId());
                                shareNewsIntent.putExtra(ShareNewsFeedService.NEWS_FEED_TO_SHARE, bundle);
                                startActivity(shareNewsIntent);
                                break;
                            default:
                                break;
                        }
                    }
                });
                shareProvidersBuilder.create().show();
            }
        });

    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.ERROR) {
            this.textToSpeech.setLanguage(Locale.ENGLISH);
            //this.textToSpeech.setSpeechRate(0.9f);
            // read article for user
            this.textToSpeech.speak(this.desiredNewsFeed.getTitle() + "." + this.desiredNewsFeed.getContent(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    private class SetUpLayoutBarAsync extends AsyncTask<String, Void, Bitmap> {
        private BitmapCache bitmapCache;

        public SetUpLayoutBarAsync() {
            // initialize the bitmap cache
            this.bitmapCache = new BitmapCache();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String bitmapCacheKey = params[0];
            Bitmap bitmap = this.bitmapCache.getBitmapFromMemoryCache(bitmapCacheKey); // get image from bitmap cache
            // otherwise , if this image has not been saved in cache yet
            if (bitmap == null) {
                try {
                    URL url = new URL(params[0]);
                    Log.e(TAG, "Starts the service !! " + url.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    // save this bitmap into cache for later uses
                    this.bitmapCache.addBitmapToMemoryCache(bitmapCacheKey, bitmap);
                } catch (Exception e) {
                    Log.e(TAG, "Could not download image with url : " + params[0], e);
                    bitmap = null;
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // set background article image for AppBarLayout
            final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_article_image);
            appBarLayout.setBackground(new BitmapDrawable(bitmap));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.textToSpeech != null) {
            this.textToSpeech.shutdown();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.callbackManager.onActivityResult(requestCode, resultCode, data); // hard code
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this.getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this.getApplicationContext());
    }
}

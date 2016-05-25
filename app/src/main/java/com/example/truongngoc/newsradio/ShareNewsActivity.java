package com.example.truongngoc.newsradio;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.truongngoc.newsradio.database.helper.adapter.DatabaseAdapter;
import com.example.truongngoc.newsradio.database.helper.helper.DatabaseHelper;
import com.example.truongngoc.newsradio.model.CloudDataAdapter;
import com.example.truongngoc.newsradio.model.ImageWorkerFactory;
import com.example.truongngoc.newsradio.model.NewsFeed;
import com.example.truongngoc.newsradio.model.Profile;
import com.example.truongngoc.newsradio.service.ShareNewsFeedService;

/**
 * Created by TruongNgoc on 22/11/2015.
 */
public class ShareNewsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // TAG
    public static final String TAG = ShareNewsActivity.class.getSimpleName();

    private NewsFeed shareNewsFeed;
    private ProgressDialog shareProgressDialog;
    private TextToSpeech textToSpeech; // use to notify to user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_share);
        this.setTitle("Share News");

        Intent intent = this.getIntent();
        this.shareNewsFeed = intent.getBundleExtra(ShareNewsFeedService.NEWS_FEED_TO_SHARE).getParcelable(ShareNewsFeedService.NEWS_FEED_TO_SHARE);
        final TextView textViewShareArticleTitle = (TextView) this.findViewById(R.id.textViewShareArticleTitle);
        textViewShareArticleTitle.setText(shareNewsFeed.getTitle() + getString(R.string.more));
        final ImageView imageViewArticleShare = (ImageView) this.findViewById(R.id.imageViewShareArticleImage);
        String urlKey = shareNewsFeed.getIllustrateImageLink();
        ImageWorkerFactory.loadBitmap(this, urlKey, imageViewArticleShare); // load bitmap background
        // share button
        final Button buttonShare = (Button) this.findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // make sure that user inputted title for this share
                final EditText editText = (EditText) findViewById(R.id.editTextUserShareTitle);
                String title = editText.getText().toString();
                if (title.length() == 0) {
                    textToSpeech.speak(getString(R.string.no_share_title), TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(ShareNewsActivity.this, R.string.no_share_title, Toast.LENGTH_SHORT).show();
                } else {
                    // prepare the dialog
                    if (shareProgressDialog == null) {
                        shareProgressDialog = new ProgressDialog(ShareNewsActivity.this, ProgressDialog.STYLE_SPINNER);
                        shareProgressDialog.setMessage(getString(R.string.sharing));
                    }
                /*
                    start a service running background to process the sharing
                   */
                    //put user id and news feed here
                    final Profile userProfile = new DatabaseAdapter(ShareNewsActivity.this).getUserProfile();
                    Intent shareNewsIntent = new Intent(ShareNewsActivity.this, ShareNewsFeedService.class);
                    // put the share information
                    //shareNewsIntent.putExtra(ShareNewsFeedService.SHARE_USER_ID, userProfile.getUserId());
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ShareNewsFeedService.NEWS_FEED_TO_SHARE, shareNewsFeed); // the news feed
                    bundle.putString(ShareNewsFeedService.SHARE_USER_ID, userProfile.getUserId()); // the user id
                    bundle.putString(ShareNewsFeedService.USER_SHARE_TITLE, title); // the title for this share
                    shareNewsIntent.putExtra(ShareNewsFeedService.NEWS_FEED_TO_SHARE, bundle);
                    // show the dialog
                    shareProgressDialog.show();
                    // start the service
                    startService(shareNewsIntent);
                    // register a receiver
                    IntentFilter intentFilter = new IntentFilter(ShareNewsFeedService.TAG);
                    ShareNewsBroadcastReceiver shareNewsBroadcastReceiver = new ShareNewsBroadcastReceiver();
                    LocalBroadcastManager.getInstance(ShareNewsActivity.this).registerReceiver(shareNewsBroadcastReceiver, intentFilter);
                }
            }
        });
        this.textToSpeech = new TextToSpeech(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.textToSpeech != null) {
            if (this.textToSpeech.isSpeaking()) {
                this.textToSpeech.stop();
            }
            this.textToSpeech.shutdown();
        }
    }


    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.ERROR) {

        }
    }

    // broadcast receiver
    private class ShareNewsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            shareProgressDialog.dismiss();
            // analyze the intent action
            if (intent.getAction().equals(ShareNewsFeedService.TAG)) {
                // get the id for this share
                // id is ID_NOT_FOUND or FAILED_SHARE_ID , this means this share is failed (base on CloudDataAdapter class operations)
                String shareId = intent.getStringExtra(DatabaseHelper.TABLE_USER_SHARED_NEWS_COLUMNS[0]);
                if (shareId.equals(CloudDataAdapter.NO_ID_FOUND) || shareId.equals(CloudDataAdapter.FAILED_SHARE_ID)) {
                    // speak the sentences
                    textToSpeech.speak(getString(R.string.error_message), TextToSpeech.QUEUE_FLUSH, null);
                    // show error dialog
                    AlertDialog.Builder errorBuilder = new AlertDialog.Builder(ShareNewsActivity.this)
                            .setIcon(R.drawable.error_icon)
                            .setTitle(R.string.error)
                            .setMessage(getString(R.string.error_message))
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(500); // vibrate for 1 second
                    errorBuilder.create().show();
                } else if (shareId.equals(CloudDataAdapter.ID_ALREADY_SHARED)) {
                    // user shared this news
                    Toast.makeText(ShareNewsActivity.this, "You've already shared this news", Toast.LENGTH_SHORT).show();
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(500);
                } else {
                    Toast.makeText(ShareNewsActivity.this, getString(R.string.successful_share), Toast.LENGTH_SHORT).show();
                    // finish this activity to comeback the current article detail
                    finish();
                }
            }
        }
    }

}

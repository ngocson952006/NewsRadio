package com.example.truongngoc.newsradio;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.truongngoc.newsradio.ui.adapter.NewsCategoriesArrayAdapter;
import com.example.truongngoc.newsradio.model.UserChooseItem;

import java.util.Locale;

/**
 * Created by TruongNgoc on 17/11/2015.
 */
public class CategoriesNewsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        TextToSpeech.OnInitListener {

    // TAG
    public static final String TAG = CategoriesNewsActivity.class.getName();

    public static final String DESIRED_URL = "user_chose_url";
    private TextToSpeech textToSpeech; // to notify to user
    private String userName; // hold user name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set content view
        this.setContentView(R.layout.list_read_choice_acitvity);

        // get action bar
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.textToSpeech = new TextToSpeech(this, this);
        final ListView listViewNewsCategories = (ListView) this.findViewById(R.id.listViewNewsCategories);
        NewsCategoriesArrayAdapter adapter = new NewsCategoriesArrayAdapter(this);
        listViewNewsCategories.setOnItemClickListener(this);
        listViewNewsCategories.setAdapter(adapter);
        // get user profile
        Intent intent = this.getIntent();
        this.userName = intent.getStringExtra(MainActivity.USER_NAME);
        //textToSpeech.speak();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserChooseItem chosenItem = (UserChooseItem) parent.getItemAtPosition(position);
        String categoryName = chosenItem.getLabel();
        String newsRssUrl = chosenItem.getUrls()[0];
        textToSpeech.speak(categoryName, TextToSpeech.QUEUE_FLUSH, null);
        // start intent for read news activity
        Intent readNewsIntent = new Intent(this, NewsActivity.class);
        readNewsIntent.putExtra(DESIRED_URL, newsRssUrl);
        readNewsIntent.putExtra(NewsActivity.NEWS_HEADER, chosenItem.getLabel());
        this.startActivity(readNewsIntent);
    }

    @Override
    public void onInit(int status) {
        // check the status
        if (status != TextToSpeech.ERROR) {
            this.textToSpeech.setLanguage(Locale.ENGLISH);
            Log.d(TAG, "text to speech initialized");
            this.textToSpeech.speak("Hi " + this.userName + ".Just choose a category.", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.textToSpeech.stop();
        this.textToSpeech.shutdown();
    }
}

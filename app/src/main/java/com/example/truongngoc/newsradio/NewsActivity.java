package com.example.truongngoc.newsradio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.truongngoc.newsradio.model.ApplicationChecker;
import com.example.truongngoc.newsradio.model.NewsFeed;
import com.example.truongngoc.newsradio.service.DownloadRssPageService;
import com.example.truongngoc.newsradio.ui.adapter.NewsItemArrayAdapter;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    // TAG
    public static final String TAG = NewsActivity.class.getSimpleName();

    public static final String NEWS_HEADER = "news_header";
    public static final String USER_DESIRED_NEWS_FEED = "user_desired_news_feed";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listViewArticles;
    private NewsItemArrayAdapter adapter;
    private ArrayList<NewsFeed> listArticles;
    private String categoryName = "Anonymous";
    // save the webservice url
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Intent intent = this.getIntent();
        url = intent.getStringExtra(CategoriesNewsActivity.DESIRED_URL);
        // set the header
        final TextView textViewHeader = (TextView) this.findViewById(R.id.newsHeaderTextView);
        // get header value
        this.categoryName = intent.getStringExtra(NewsActivity.NEWS_HEADER);
        textViewHeader.setText(this.categoryName);
        this.swipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipeRefreshContainer);
        // register a listener when user drag this list view
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadContent();
            }
        });
        // set color for refresh cursor
        //this.swipeRefreshLayout.setColorSchemeResources(R.array.refresh_cursor_colors);
        this.listViewArticles = (ListView) findViewById(R.id.listViewNewsArticles);
        this.listViewArticles.setOnItemClickListener(this);
        if (savedInstanceState != null) {
            this.listArticles = savedInstanceState.getParcelableArrayList("key");
            this.adapter = new NewsItemArrayAdapter(this, this.listArticles);
            listViewArticles.setAdapter(this.adapter);
        } else {
            // check internet connection
            if (!ApplicationChecker.isInternetAvailable(this)) {
                ApplicationChecker.showConnectionFailedDialog(this);
            } else {
                // restart service again
                // start to fetch news from webservice
                swipeRefreshLayout.setRefreshing(true);
                Intent downloadNewsService = new Intent(NewsActivity.this, DownloadRssPageService.class);
                downloadNewsService.putExtra(DownloadRssPageService.RSS_URL, url);
                startService(downloadNewsService);
                // register a receiver
                IntentFilter intentFilter = new IntentFilter(DownloadRssPageService.TAG);
                NewsFetchReceiver newsFetchReceiver = new NewsFetchReceiver();
                LocalBroadcastManager.getInstance(NewsActivity.this).registerReceiver(newsFetchReceiver, intentFilter);
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewsFeed item = (NewsFeed) parent.getItemAtPosition(position);
        Intent articleDetailIntent = new Intent(this, NewsArticleActivity.class);
        // put data
        // put user desired news feed into intent
        articleDetailIntent.putExtra(NewsActivity.USER_DESIRED_NEWS_FEED, item);
        articleDetailIntent.putExtra(NEWS_HEADER, this.categoryName);
        // start activity
        this.startActivity(articleDetailIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // news downloading receiver
    private class NewsFetchReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //  analyze intent action
            if (intent.getAction().equals(DownloadRssPageService.TAG)) {
                // check that the SwipeRefreshLayout is refreshing
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                listArticles = intent.getParcelableArrayListExtra(DownloadRssPageService.RESPONSE_LIST);
                adapter = new NewsItemArrayAdapter(NewsActivity.this, listArticles);
                listViewArticles.setAdapter(adapter);
            }
        }
    }


    private void reloadContent() {
        // check if the device is connecting to the internet
        if (!ApplicationChecker.isInternetAvailable(this)) {
            ApplicationChecker.showConnectionFailedDialog(this);
        } else {
            // clear all data from adapter
            if (adapter.getCount() != 0) {
                adapter.clear();
            }
            // restart service again
            // start to fetch news from webservice
            swipeRefreshLayout.setRefreshing(true);
            Intent downloadNewsService = new Intent(NewsActivity.this, DownloadRssPageService.class);
            downloadNewsService.putExtra(DownloadRssPageService.RSS_URL, url);
            startService(downloadNewsService);
            // register a receiver
            IntentFilter intentFilter = new IntentFilter(DownloadRssPageService.TAG);
            NewsFetchReceiver newsFetchReceiver = new NewsFetchReceiver();
            LocalBroadcastManager.getInstance(NewsActivity.this).registerReceiver(newsFetchReceiver, intentFilter);
        }
    }

}

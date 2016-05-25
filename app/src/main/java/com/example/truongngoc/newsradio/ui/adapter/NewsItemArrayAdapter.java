package com.example.truongngoc.newsradio.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.truongngoc.newsradio.R;
import com.example.truongngoc.newsradio.model.ImageWorkerFactory;
import com.example.truongngoc.newsradio.model.NewsFeed;

import java.util.ArrayList;

/**
 * Created by TruongNgoc on 18/11/2015.
 */
public class NewsItemArrayAdapter extends ArrayAdapter<NewsFeed> {
    private static final int TEXT_VIEW_TEXT_SIZE = 50;
    private static final String MORE = "...";
    private Context context;
    private ArrayList<NewsFeed> newsFeedsList;

    public NewsItemArrayAdapter(Context context, ArrayList<NewsFeed> newsFeedsList) {
        super(context, R.layout.news_list_item);
        this.context = context;
        this.newsFeedsList = newsFeedsList;
    }

    public class ViewHolder {
        ImageView itemIconImageView;
        TextView newsTitleTextView;
        TextView publicTimeTextView;
        Button navigateButton;
    }

    @Override
    public int getCount() {
        return this.newsFeedsList.size();
    }

    @Override
    public NewsFeed getItem(int position) {
        return this.newsFeedsList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.news_list_item, parent, false);

            viewHolder.itemIconImageView = (ImageView) convertView.findViewById(R.id.newsIconImageView);
            viewHolder.newsTitleTextView = (TextView) convertView.findViewById(R.id.newsTitleTextView);
            viewHolder.publicTimeTextView = (TextView) convertView.findViewById(R.id.newsPublicTimeTextView);
            viewHolder.navigateButton = (Button) convertView.findViewById(R.id.navigateButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        NewsFeed item = this.newsFeedsList.get(position);
        //viewHolder.layoutContainer.setBackground();
        String newsTitle = item.getTitle();
        // check size of the text view
        if (newsTitle.length() > TEXT_VIEW_TEXT_SIZE) {
            String[] words = newsTitle.split(" ");
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < 6; ++i) {
                buffer.append(words[i] + " ");
            }
            viewHolder.newsTitleTextView.setText(buffer.toString().trim());

        } else {
            viewHolder.newsTitleTextView.setText(item.getTitle() + MORE);
        }
        viewHolder.publicTimeTextView.setText(item.getPublicTime());
        // start to download item image
        ImageWorkerFactory.loadBitmap(this.context, item.getIllustrateImageLink(), viewHolder.itemIconImageView);
        viewHolder.navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Android Developer Button", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

}

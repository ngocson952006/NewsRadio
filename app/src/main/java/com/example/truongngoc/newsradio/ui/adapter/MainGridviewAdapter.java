package com.example.truongngoc.newsradio.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.truongngoc.newsradio.R;
import com.example.truongngoc.newsradio.model.UserChooseItem;

import java.util.ArrayList;

/**
 * Created by TruongNgoc on 06/11/2015.
 */
public class MainGridviewAdapter extends ArrayAdapter<UserChooseItem> {
    private Context context;
    private ArrayList<UserChooseItem> chooseItemArray;

    private class ViewHolder {
        public RelativeLayout layoutContainer;
        public TextView textViewLabel;
        public ImageView imageViewIcon;
    }


    public MainGridviewAdapter(Context context, int resource, ArrayList<UserChooseItem> items) {
        super(context, resource);
        this.context = context;
        this.chooseItemArray = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.gridview_item, parent, false);
            viewHolder.layoutContainer = (RelativeLayout) convertView.findViewById(R.id.gridViewItemContainer);
            viewHolder.textViewLabel = (TextView) convertView.findViewById(R.id.textViewLabel);
            viewHolder.imageViewIcon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserChooseItem item = this.chooseItemArray.get(position);
        //viewHolder.layoutContainer.setBackground();
        viewHolder.textViewLabel.setText(item.getLabel());
        viewHolder.imageViewIcon.setImageBitmap(item.getIconBitmap());
        return convertView;
    }

    @Override
    public void insert(UserChooseItem object, int index) {
        super.insert(object, index);
    }

    @Override
    public int getPosition(UserChooseItem item) {
        return super.getPosition(item);
    }

    @Override
    public UserChooseItem getItem(int position) {
        return this.chooseItemArray.get(position);
    }

    @Override
    public int getCount() {
        return this.chooseItemArray.size();
    }
}

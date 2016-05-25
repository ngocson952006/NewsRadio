package com.example.truongngoc.newsradio.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.truongngoc.newsradio.R;
import com.example.truongngoc.newsradio.model.UserChooseItem;
import com.example.truongngoc.newsradio.model.UserChooseItemFactory;

import java.util.ArrayList;

/**
 * Created by TruongNgoc on 17/11/2015.
 */
public class NewsCategoriesArrayAdapter extends ArrayAdapter<UserChooseItem> {

    private Context context;
    private ArrayList<UserChooseItem> newsCategories;

    public NewsCategoriesArrayAdapter(Context context) {
        super(context, R.layout.categories_litsview_item);
        this.context = context;
        this.newsCategories = UserChooseItemFactory.getNewsCatetogoires(this.context);
    }

    public class ViewHolder {
        ImageView categoryIcon;
        TextView categoryName;
    }

    @Override
    public int getCount() {
        return this.newsCategories.size(); // return the size
    }

    @Override
    public UserChooseItem getItem(int position) {
        return this.newsCategories.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.categories_litsview_item, parent, false);

            viewHolder.categoryIcon = (ImageView) convertView.findViewById(R.id.categoryIcon);
            viewHolder.categoryName = (TextView) convertView.findViewById(R.id.categoryNameTexView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserChooseItem item = this.newsCategories.get(position);
        //viewHolder.layoutContainer.setBackground();
        viewHolder.categoryName.setText(item.getLabel());
        viewHolder.categoryIcon.setImageBitmap(item.getIconBitmap());
        return convertView;
    }
}

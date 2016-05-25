package com.example.truongngoc.newsradio.model;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.example.truongngoc.newsradio.R;

import java.util.ArrayList;


/**
 * Created by TruongNgoc on 06/11/2015.
 */
public class UserChooseItemFactory {
    public static ArrayList<UserChooseItem> getAppMainUserChooseItems(Context context) {
        ArrayList<UserChooseItem> mainList = new ArrayList<>();
        mainList.add(new UserChooseItem(context.getString(R.string.read_news), BitmapFactory.decodeResource(context.getResources(), R.drawable.news_icon)));
        mainList.add(new UserChooseItem(context.getString(R.string.shared_news), BitmapFactory.decodeResource(context.getResources(), R.drawable.share_icon)));
        mainList.add(new UserChooseItem(context.getString(R.string.settings), BitmapFactory.decodeResource(context.getResources(), R.drawable.settings_icon)));
        mainList.add(new UserChooseItem(context.getString(R.string.app_information), BitmapFactory.decodeResource(context.getResources(), R.drawable.information_icon)));
        return mainList;
    }

    public static final ArrayList<UserChooseItem> getNewsCatetogoires(Context context) {
        ArrayList<UserChooseItem> listCategories = new ArrayList<>();
        listCategories.add(new UserChooseItem(
                context.getString(R.string.last_news),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.last_news_icon),
                RssUrlFeedProvider.provideNationalNews()));
        listCategories.add(new UserChooseItem(
                context.getString(R.string.politics),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.politics_icon),
                RssUrlFeedProvider.providePoliticsNews()));
        listCategories.add(new UserChooseItem(
                context.getString(R.string.health),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.health_icon),
                RssUrlFeedProvider.provideHealthNews()));
        listCategories.add(new UserChooseItem(
                context.getString(R.string.money),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.money_icon),
                RssUrlFeedProvider.provideMoneyNews()));
        listCategories.add(new UserChooseItem(
                context.getString(R.string.technologies),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.technology_icon),
                RssUrlFeedProvider.provideTechnologiesNews()));
        listCategories.add(new UserChooseItem(
                context.getString(R.string.sport),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.sport_icon),
                RssUrlFeedProvider.provideSportsNews()));
        listCategories.add(new UserChooseItem(
                context.getString(R.string.irresistible_news),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.irresitisble),
                RssUrlFeedProvider.provideIrrestisbleNews()));
        return listCategories;
    }
}

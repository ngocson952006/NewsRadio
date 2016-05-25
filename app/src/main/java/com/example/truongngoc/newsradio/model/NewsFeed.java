package com.example.truongngoc.newsradio.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TruongNgoc on 16/11/2015.
 * class to hold the news item
 */
public class NewsFeed implements Parcelable {

    private String title; // the title of the news
    private String content; // the content
    private String illustrateImageLink; //
    private String link; // link for webview
    private String publicTime;

    public NewsFeed() {
    }

    public NewsFeed(String title, String content, String illustrateImageLink, String link, String publicTime) {
        this.title = title;
        this.content = content;
        this.illustrateImageLink = illustrateImageLink;
        this.link = link;
        this.publicTime = publicTime;
    }


    protected NewsFeed(Parcel in) {
        title = in.readString();
        content = in.readString();
        illustrateImageLink = in.readString();
        link = in.readString();
        publicTime = in.readString();
    }

    public static final Creator<NewsFeed> CREATOR = new Creator<NewsFeed>() {
        @Override
        public NewsFeed createFromParcel(Parcel in) {
            return new NewsFeed(in);
        }

        @Override
        public NewsFeed[] newArray(int size) {
            return new NewsFeed[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPublicTime() {
        return publicTime;
    }

    public void setPublicTime(String publicTime) {
        this.publicTime = publicTime;
    }

    public String getIllustrateImageLink() {
        return illustrateImageLink;
    }

    public void setIllustrateImageLink(String illustrateImageLink) {
        this.illustrateImageLink = illustrateImageLink;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(illustrateImageLink);
        dest.writeString(link);
        dest.writeString(publicTime);
    }
}

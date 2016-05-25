package com.example.truongngoc.newsradio.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TruongNgoc on 24/11/2015.
 */
public class ShareNewsFeed implements Parcelable {
    private String shareTitle; // the share title
    private String shareId; // id of the share
    private Profile shareUserId; // user id who shares the news . Base on this id , others can see the profile
    private String sharedTime; // share time
    private String NewsFeed; // the feed content

    public ShareNewsFeed() {
    }

    protected ShareNewsFeed(Parcel in) {
        shareId = in.readString();
        shareUserId = in.readParcelable(Profile.class.getClassLoader());
        sharedTime = in.readString();
        NewsFeed = in.readString();
    }

    public static final Creator<ShareNewsFeed> CREATOR = new Creator<ShareNewsFeed>() {
        @Override
        public ShareNewsFeed createFromParcel(Parcel in) {
            return new ShareNewsFeed(in);
        }

        @Override
        public ShareNewsFeed[] newArray(int size) {
            return new ShareNewsFeed[size];
        }
    };

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public Profile getShareUserId() {
        return shareUserId;
    }

    public void setShareUserId(Profile shareUserId) {
        this.shareUserId = shareUserId;
    }

    public String getSharedTime() {
        return sharedTime;
    }

    public void setSharedTime(String sharedTime) {
        this.sharedTime = sharedTime;
    }

    public String getNewsFeed() {
        return NewsFeed;
    }

    public void setNewsFeed(String newsFeed) {
        NewsFeed = newsFeed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shareId);
        dest.writeParcelable(shareUserId, flags);
        dest.writeString(sharedTime);
        dest.writeString(NewsFeed);
    }
}

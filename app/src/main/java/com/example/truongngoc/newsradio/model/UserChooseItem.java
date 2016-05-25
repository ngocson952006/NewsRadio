package com.example.truongngoc.newsradio.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TruongNgoc on 06/11/2015.
 */
public class UserChooseItem implements Parcelable {

    // fields
    private String label;
    private Bitmap iconBitmap;
    private String[] urls; // this is optional , base on the type of the item;

    public UserChooseItem(String label, Bitmap iconBitmap) {
        this.label = label;
        this.iconBitmap = iconBitmap;
    }

    public UserChooseItem(String label, Bitmap iconBitmap, String[] urls) {
        this.label = label;
        this.iconBitmap = iconBitmap;
        this.urls = urls;
    }


    /*
             getters and setters
         */
    protected UserChooseItem(Parcel in) {
        label = in.readString();
        iconBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<UserChooseItem> CREATOR = new Creator<UserChooseItem>() {
        @Override
        public UserChooseItem createFromParcel(Parcel in) {
            return new UserChooseItem(in);
        }

        @Override
        public UserChooseItem[] newArray(int size) {
            return new UserChooseItem[size];
        }
    };

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(Bitmap iconBitmap) {
        this.iconBitmap = iconBitmap;
    }


    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeParcelable(iconBitmap, flags);
    }

}

package com.example.truongngoc.newsradio.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TruongNgoc on 07/11/2015.
 * class to hold personal information
 */
public class Profile implements Parcelable {

    private String userId; // id of user
    private String userName; // user name
    private String phoneNumber; //
    private String email;
    private String address;
    private Bitmap profileIconBitmap; //
    private Bitmap wallpaperPhotoBitmap;
    private String publicTime;

    public Profile(String userId, String userName, String phoneNumber, String email, String address) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    public Profile() {
    }


    protected Profile(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        address = in.readString();
        profileIconBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        wallpaperPhotoBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        publicTime = in.readString();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Bitmap getProfileIconBitmap() {
        return profileIconBitmap;
    }

    public void setProfileIconBitmap(Bitmap profileIconBitmap) {
        this.profileIconBitmap = profileIconBitmap;
    }

    public Bitmap getWallpaperPhotoBitmap() {
        return wallpaperPhotoBitmap;
    }

    public void setWallpaperPhotoBitmap(Bitmap wallpaperPhotoBitmap) {
        this.wallpaperPhotoBitmap = wallpaperPhotoBitmap;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeParcelable(profileIconBitmap, flags);
        dest.writeParcelable(wallpaperPhotoBitmap, flags);
        dest.writeString(publicTime);
    }
}

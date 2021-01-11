package com.example.cs223_final.ui.home.add;

import android.os.Parcel;
import android.os.Parcelable;

public class Upload implements Parcelable, Comparable<Upload> {
    private String mName;
    private String mImageUrl;
    private String Category;


    public Upload() {

    }

    public Upload(String name, String imageUrl, String category) {
        mName = name;
        mImageUrl = imageUrl;
        Category = category;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }


    //PARCELABLE IMPLEMENTATION

    protected Upload(Parcel in) {
        mName = in.readString();
        mImageUrl = in.readString();
        Category = in.readString();
    }

    public static final Creator<Upload> CREATOR = new Creator<Upload>() {
        @Override
        public Upload createFromParcel(Parcel in) {
            return new Upload(in);
        }

        @Override
        public Upload[] newArray(int size) {
            return new Upload[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mImageUrl);
        dest.writeString(Category);
    }

    @Override
    public int compareTo(Upload o) {

        return o.getName().compareTo(mName);

    }
}

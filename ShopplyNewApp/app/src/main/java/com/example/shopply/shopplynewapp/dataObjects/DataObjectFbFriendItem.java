package com.example.shopply.shopplynewapp.dataObjects;

/**
 * Created by Evyatar on 30/07/2015.
 */
public class DataObjectFbFriendItem {

    private String mFullName;
    private String mImageUrl;

    public DataObjectFbFriendItem(String fullName, String ImageUrl){
        mFullName = fullName;
        mImageUrl = ImageUrl;
    }

    public String getFullName(){
        return mFullName;
    }

    public String getImageUrl(){
        return mImageUrl;
    }
}

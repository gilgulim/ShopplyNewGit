package com.example.shopply.shopplynewapp.dataObjects;

/**
 * Created by Evyatar on 30/07/2015.
 */
public class DataObjectFbFriendItem {

    private String mId;
    private String mFullName;
    private String mImageUrl;

    public DataObjectFbFriendItem(String id, String fullName, String ImageUrl){
        mId = id;
        mFullName = fullName;
        mImageUrl = ImageUrl;
    }

    public String getFullName(){
        return mFullName;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public String getId(){
        return mId;
    }
}

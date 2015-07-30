package com.example.shopply.shopplynewapp.dataObjects;

import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by Gilp on 16/05/2015.
 */
public class DataObjectShoppingList {
    private Drawable mImageCategory;
    private String mText1;
    private String mId;
    public DataObjectShoppingList(String id, String text1, Drawable imageCategory){
        mId = id;
        mText1 = text1;
        mImageCategory = imageCategory;
    }
    public String getmId() {
        return mId;
    }

    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public Drawable getmImageCategory() {
        return mImageCategory;
    }

    public void setmImageCategory(Drawable mImageCategory) {
        this.mImageCategory = mImageCategory;
    }
}



package com.example.shopply.shopplynewapp;

import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by Gilp on 16/05/2015.
 */
public class DataObjectShoppingList {
    private Drawable mImageCategory;
    private String mText1;

    DataObjectShoppingList(String text1, Drawable imageCategory){
        mText1 = text1;
        mImageCategory = imageCategory;
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



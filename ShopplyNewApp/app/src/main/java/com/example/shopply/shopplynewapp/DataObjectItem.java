package com.example.shopply.shopplynewapp;

import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by Gilp on 16/05/2015.
 */
public class DataObjectItem {

    private Drawable mImageItem;
    private String mItemName;
    private int mItemAmount;
    private int mItemType; //0 = QTY, 1 = KG

    public DataObjectItem(String itemName, Drawable imageItem, int amount, int itemType){
        mImageItem = imageItem;
        mItemAmount = amount;
        mItemName = itemName;
        mItemType = itemType;

    }

    public Drawable getmImageItem() {
        return mImageItem;
    }

    public void setmImageItem(Drawable mImageItem) {
        this.mImageItem = mImageItem;
    }

    public String getmItemName() {
        return mItemName;
    }

    public void setmItemName(String mItemName) {
        this.mItemName = mItemName;
    }

    public int getmItemAmount() {
        return mItemAmount;
    }

    public void setmItemAmount(int mItemAmount) {
        this.mItemAmount = mItemAmount;
    }

    public int getmItemType() {
        return mItemType;
    }

    public void setmItemType(int mItemType) {
        this.mItemType = mItemType;
    }

}



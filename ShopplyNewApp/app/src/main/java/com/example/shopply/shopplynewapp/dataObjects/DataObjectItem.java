package com.example.shopply.shopplynewapp.dataObjects;

import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by Gilp on 16/05/2015.
 */
public class DataObjectItem implements Comparable<DataObjectItem>{
    private String mItemId;
    private String mItemsListRelationShipId;
    private String mItemName;
    private String mItemCategoryColor;
    private int mItemAmount;
    private int mItemType; //0 = QTY, 1 = KG

    public DataObjectItem(String itemsListRelationShipId, String itemId, String itemName, String itemCategryColor, int amount, int itemType){
        this(itemId,itemName,itemCategryColor,amount,itemType);
        mItemsListRelationShipId = itemsListRelationShipId;


    }

    public DataObjectItem(String itemId, String itemName, String itemCategryColor, int amount, int itemType){
        mItemId = itemId;
        mItemCategoryColor = itemCategryColor;
        mItemAmount = amount;
        mItemName = itemName;
        mItemType = itemType;

    }
    public String getmItemId() {
        return mItemId;
    }

    public void setmItemId(String mItemId) {
        this.mItemId = mItemId;
    }

    public String getmItemCategoryColor() {
        return mItemCategoryColor;
    }

    public void setmItemCategoryColor(String mItemCategoryColor) {
        this.mItemCategoryColor = mItemCategoryColor;
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

    public String getmItemsListRelationShipId() {
        return mItemsListRelationShipId;
    }



    @Override
    public int compareTo(DataObjectItem o) {
        return mItemCategoryColor.compareTo(o.getmItemCategoryColor());
    }
}



package com.example.shopply.shopplynewapp.adapters;

/**
 * Created by Evyatar on 02/06/2015.
 */

public interface IShoppingListButtonsListener {
    public enum ShoppingListButtonType{
        BTN_ITEM_SELECTED,
        BTN_DISCARD,
        BTN_EDIT,
        BTN_SHARE,
        BTN_TOGGLE_BG
    }
    void onShoppingListButtonClicked(ShoppingListButtonType buttonType, int position);
}


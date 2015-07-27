package com.example.shopply.shopplynewapp.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopply.shopplynewapp.R;

/**
 * Created by Evyatar on 02/06/2015.
 */

public class ShoppingListDataObjectHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {


    private ImageView imgView_shoppingListBgImage;
    private TextView lbl_shoppingListTitle;
    private ImageView imgView_Edit;
    private ImageView imgView_share;
    private ImageView imgView_discard;
    private ImageView imgView_toggleBgImage;
    private IShoppingListButtonsListener mShoppingListButtonListener;

    public ShoppingListDataObjectHolder(View itemView) {
        super(itemView);


        lbl_shoppingListTitle = (TextView) itemView.findViewById(R.id.lbl_shoppingListTitle);
        imgView_shoppingListBgImage = (ImageView) itemView.findViewById(R.id.imgView_shoppingListBgImage);
        imgView_discard = (ImageView) itemView.findViewById(R.id.imgView_discard);
        imgView_Edit = (ImageView) itemView.findViewById(R.id.imgView_Edit);
        imgView_share = (ImageView) itemView.findViewById(R.id.imgView_share);
        imgView_toggleBgImage = (ImageView) itemView.findViewById(R.id.imgViewToggleBG);


        imgView_shoppingListBgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mShoppingListButtonListener!= null) {
                    mShoppingListButtonListener.onShoppingListButtonClicked(IShoppingListButtonsListener.ShoppingListButtonType.BTN_ITEM_SELECTED, getPosition());
                }
            }
        });
        imgView_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mShoppingListButtonListener!= null) {
                    mShoppingListButtonListener.onShoppingListButtonClicked(IShoppingListButtonsListener.ShoppingListButtonType.BTN_SHARE, getPosition());
                }
            }
        });

        imgView_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mShoppingListButtonListener!= null) {
                    mShoppingListButtonListener.onShoppingListButtonClicked(IShoppingListButtonsListener.ShoppingListButtonType.BTN_EDIT, getPosition());
                }
            }
        });

        imgView_toggleBgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShoppingListButtonListener != null) {
                    mShoppingListButtonListener.onShoppingListButtonClicked(IShoppingListButtonsListener.ShoppingListButtonType.BTN_TOGGLE_BG, getPosition());
                }
            }
        });

        imgView_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShoppingListButtonListener != null) {
                    mShoppingListButtonListener.onShoppingListButtonClicked(IShoppingListButtonsListener.ShoppingListButtonType.BTN_DISCARD, getPosition());
                }
            }
        });

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
/*        if(mShoppingListButtonListener != null) {
            mShoppingListButtonListener.onShoppingListButtonClicked(IShoppingListButtonsListener.ShoppingListButtonType.BTN_ITEM_SELECTED, getPosition());
        }*/
    }

    public void setShoppingListTitle(String title){
        lbl_shoppingListTitle.setText(title);
    }

    public void setShoppingListImage(Drawable drawableImg){

        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            imgView_shoppingListBgImage.setBackgroundDrawable(drawableImg);
        } else {
            imgView_shoppingListBgImage.setBackground(drawableImg);

        }

    }

    public void setButtonsListener(IShoppingListButtonsListener listener){
        mShoppingListButtonListener = listener;
    }
}

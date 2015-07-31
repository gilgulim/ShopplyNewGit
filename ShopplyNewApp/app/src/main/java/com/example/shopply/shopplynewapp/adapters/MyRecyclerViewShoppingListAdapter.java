package com.example.shopply.shopplynewapp.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shopply.shopplynewapp.dataObjects.DataObjectShoppingList;
import com.example.shopply.shopplynewapp.R;

import java.util.ArrayList;

/**
 * Created by Gilp on 16/05/2015.
 */

public class MyRecyclerViewShoppingListAdapter extends RecyclerView.Adapter<ShoppingListDataObjectHolder>
        implements IShoppingListButtonsListener
{
    private static String LOG_TAG = "MyRecyclerViewShoppingListAdapter";
    private ArrayList<DataObjectShoppingList> mDataset;
    private IShoppingListButtonsListener mShoppingListItemButtonsListener;

    public MyRecyclerViewShoppingListAdapter(ArrayList<DataObjectShoppingList> myDataset) {
        mDataset = myDataset;
    }

    public MyRecyclerViewShoppingListAdapter() {
    }

    public void setDataset(ArrayList<DataObjectShoppingList> myDataset){
        mDataset = myDataset;
    }

    @Override
    public ShoppingListDataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_shopping_list_row, parent, false);

        ShoppingListDataObjectHolder dataObjectHolder = new ShoppingListDataObjectHolder(view);
        dataObjectHolder.setButtonsListener(this);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(ShoppingListDataObjectHolder holder, final int position) {
        holder.setShoppingListTitle(mDataset.get(position).getmText1());
        holder.setShoppingListImage(mDataset.get(position).getmImageCategory());
    }

    public void changeItemBackground(Drawable img,int index){
        mDataset.get(index).setmImageCategory(img);
        notifyItemChanged(index);
    }

    public void addItem(DataObjectShoppingList dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }
    public String getShoppingListId(int index){
        return mDataset.get(index).getmId();
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);

    }

    public void setShoppingListItemButtonsListener(IShoppingListButtonsListener shoppingListItemButtonsListener){
        mShoppingListItemButtonsListener = shoppingListItemButtonsListener;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onShoppingListButtonClicked(ShoppingListButtonType buttonType, int position) {
        if(mShoppingListItemButtonsListener!=null){
            mShoppingListItemButtonsListener.onShoppingListButtonClicked(buttonType, position);
        }
    }
}
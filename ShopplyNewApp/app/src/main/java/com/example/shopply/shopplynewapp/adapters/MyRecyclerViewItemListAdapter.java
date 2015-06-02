package com.example.shopply.shopplynewapp.adapters;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopply.shopplynewapp.DataObjectItem;
import com.example.shopply.shopplynewapp.R;

import java.util.ArrayList;

/**
 * Created by Gilp on 16/05/2015.
 */
public class MyRecyclerViewItemListAdapter extends RecyclerView.Adapter<MyRecyclerViewItemListAdapter.DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewItemListAdapter";
    private ArrayList<DataObjectItem> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ImageView itemBG;
        TextView itemName;
        TextView itemAmount;


        public DataObjectHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.ItemListName);
            itemAmount = (TextView) itemView.findViewById(R.id.ItemListAmount);
            itemBG = (ImageView) itemView.findViewById(R.id.ItemListImage);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewItemListAdapter(ArrayList<DataObjectItem> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_item_list_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.itemName.setText(mDataset.get(position).getmItemName());

        String itemType;
        if(mDataset.get(position).getmItemType()==0){
            itemType = "QTY";
        } else{
            itemType = "KG";
        }
        holder.itemAmount.setText(String.format("%d %s", mDataset.get(position).getmItemAmount(), itemType));


        //support API 15 (Jellybean)
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.itemBG.setBackgroundDrawable(mDataset.get(position).getmImageItem());
        } else {
            holder.itemBG.setBackground(mDataset.get(position).getmImageItem());
        }

    }

    public void addItem(DataObjectItem dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
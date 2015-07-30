package com.example.shopply.shopplynewapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.shopply.shopplynewapp.dataObjects.DataObjectFbFriendItem;
import com.example.shopply.shopplynewapp.R;

import java.util.ArrayList;

/**
 * Created by Evyatar on 30/07/2015.
 */
public class MyRecyclerViewFbListAdapter extends RecyclerView.Adapter<MyRecyclerViewFbListAdapter.FbFriendItemDataObjectHolder> implements RemoveItemListener{

    private static ArrayList<DataObjectFbFriendItem> mDataset;

    public static class FbFriendItemDataObjectHolder extends RecyclerView.ViewHolder {

        ImageView fbUserImage;
        TextView fbUserFullName;

        public FbFriendItemDataObjectHolder(View itemView) {
            super(itemView);
            fbUserImage = (ImageView)itemView.findViewById(R.id.fbUserImage);
            fbUserFullName = (TextView)itemView.findViewById(R.id.fbUserFullName);
        }
    }

    public MyRecyclerViewFbListAdapter(ArrayList<DataObjectFbFriendItem> dataSet)
    {
        mDataset = dataSet;
    }

    @Override
    public FbFriendItemDataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.facebook_friend_row, parent, false);

        FbFriendItemDataObjectHolder dataObjectHolder = new FbFriendItemDataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(FbFriendItemDataObjectHolder holder, int position) {

        DataObjectFbFriendItem fbDataObject = mDataset.get(position);
        holder.fbUserFullName.setText(fbDataObject.getFullName());


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onItemRemove(int position) {

    }
}

package com.example.shopply.shopplynewapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopply.shopplynewapp.dataObjects.DataObjectFbFriendItem;
import com.example.shopply.shopplynewapp.R;
import com.example.shopply.shopplynewapp.tasks.DownloadImageTask;

import java.util.ArrayList;

/**
 * Created by Evyatar on 30/07/2015.
 */
public class MyRecyclerViewFbListAdapter extends RecyclerView.Adapter<MyRecyclerViewFbListAdapter.FbFriendItemDataObjectHolder> implements RemoveItemListener{

    private static ArrayList<DataObjectFbFriendItem> mDataset;
    private FbFriendSelectedListener fbFriendSelectedListener;

    public static class FbFriendItemDataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        String fbId;
        ImageView fbUserImage;
        TextView fbUserFullName;
        FbFriendSelectedListener fbFriendSelectedListener;

        public FbFriendItemDataObjectHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            fbUserImage = (ImageView)itemView.findViewById(R.id.fbUserImage);
            fbUserFullName = (TextView)itemView.findViewById(R.id.fbUserFullName);
        }


        @Override
        public void onClick(View view) {
            if(fbFriendSelectedListener!=null) {
                fbFriendSelectedListener.onFriendSelected(fbId);
            }
        }
    }

    public MyRecyclerViewFbListAdapter()
    {
        mDataset = new ArrayList<>();

    }

    public void setDataset( ArrayList<DataObjectFbFriendItem> dataSet){
        mDataset = dataSet;
        notifyDataSetChanged();
    }
    public void setFbFriendSelectedListener(FbFriendSelectedListener listener){
        fbFriendSelectedListener = listener;
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

        holder.fbFriendSelectedListener = fbFriendSelectedListener;
        holder.fbId = fbDataObject.getId();
        holder.fbUserFullName.setText(fbDataObject.getFullName());

        DownloadImageTask downloadImageTask = new DownloadImageTask(holder.fbUserImage);
        downloadImageTask.execute(fbDataObject.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onItemRemove(View itemView, int position) {

    }
}

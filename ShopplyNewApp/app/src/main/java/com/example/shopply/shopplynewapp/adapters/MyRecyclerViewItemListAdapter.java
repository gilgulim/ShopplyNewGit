package com.example.shopply.shopplynewapp.adapters;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.example.shopply.shopplynewapp.DataObjectItem;
import com.example.shopply.shopplynewapp.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilp on 16/05/2015.
 */
public class MyRecyclerViewItemListAdapter extends RecyclerView.Adapter<MyRecyclerViewItemListAdapter.DataObjectHolder> implements RemoveItemListener{

    private static String LOG_TAG = "MyRecyclerViewItemListAdapter";
    private static ArrayList<DataObjectItem> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        RemoveItemListener removeItemListener;
        ImageView itemBG;
        TextView itemName;
        TextView itemAmount;
        SwipeLayout swipeLayout;


        public DataObjectHolder(View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.ItemListName);
            itemAmount = (TextView) itemView.findViewById(R.id.ItemListAmount);
            itemBG = (ImageView) itemView.findViewById(R.id.ItemListImage);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.Item_SwipeLayout);

            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.left_wrapper));
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, itemView.findViewById(R.id.right_wrapper));

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    //when the SurfaceView totally cover the BottomView.
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    //you are swiping.
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    removeItemListener.onItemRemove(getPosition());
                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                }

            });


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
        holder.removeItemListener = this;

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


    @Override
    public void onItemRemove(int position) {
        DataObjectItem dataObjectItem = mDataset.get(position);
        if(dataObjectItem != null) {

            String itemId = dataObjectItem.getmItemId();
            String listId = dataObjectItem.getmListId();

            ParseQuery<ParseObject> query = new ParseQuery("n_items");
            query.whereEqualTo("objectId", itemId);
            try {
                List<ParseObject> itemsArray = query.find();

                query = new ParseQuery("n_shoppingLists");
                query.whereEqualTo("objectId", listId);
                List<ParseObject> listArray = query.find();

                query = new ParseQuery("n_itemsListsRelationships");
                query.whereEqualTo("itemId", itemsArray.get(0));
               // query.whereEqualTo("listId", listArray.get(0));
                List<ParseObject> listItemsToDelete = query.find();

                int size = listItemsToDelete.size();

            } catch (Exception ex) {

                String strex = ex.toString();

            }


            query.whereEqualTo("itemID", itemId);
            //query.whereEqualTo("listID", listId);


        }


        mDataset.remove(position);
        notifyDataSetChanged();

    }

    public void addItem(DataObjectItem dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }



    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
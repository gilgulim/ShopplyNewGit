package com.example.shopply.shopplynewapp.adapters;


import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.example.shopply.shopplynewapp.DataObjectItem;
import com.example.shopply.shopplynewapp.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilp on 16/05/2015.
 */
public class MyRecyclerViewLiveShoppingItemListAdapter extends RecyclerView.Adapter<MyRecyclerViewLiveShoppingItemListAdapter.DataObjectHolder> implements RemoveItemListener{

    private static String LOG_TAG = "MRV-LS-ItemListAdapter";
    private static ArrayList<DataObjectItem> mDataset;
    private static MyClickListener myClickListener;
    private static final int SWIPE_DELAY_TIME = 1500;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        RemoveItemListener removeItemListener;
        //ImageView itemBG;
        ImageView itemColorCategory;
        TextView itemName;
        TextView itemAmount;
        TextView itemAdded;
        TextView itemMissing;
        SwipeLayout swipeLayout;


        public DataObjectHolder(final View itemView) {
            super(itemView);
            itemAdded = (TextView) itemView.findViewById(R.id.LSItemAdded);
            itemMissing = (TextView) itemView.findViewById(R.id.LSMissingItem);

            itemName = (TextView) itemView.findViewById(R.id.LSItemListName);
            itemAmount = (TextView) itemView.findViewById(R.id.LSItemListAmount);
            //itemBG = (ImageView) itemView.findViewById(R.id.LSItemListImage);
            itemColorCategory = (ImageView) itemView.findViewById(R.id.imageViewCategoryColor);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.liveShoppingItemSwipeLayout);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.live_shopping_left_wrapper));
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, itemView.findViewById(R.id.live_shopping_right_wrapper));

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
                public void onOpen(final SwipeLayout layout) {
                    Log.i("TEST", "buttom layout ID " + layout.getCurrentBottomView().getId() + " left: " + R.id.live_shopping_left_wrapper + " Right: " + R.id.live_shopping_right_wrapper);

                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            if(layout.getCurrentBottomView().getId()==R.id.live_shopping_left_wrapper){
                                //removeItemListener.onItemRemove(getPosition());
                                //TODO:remove from list and add to other/default list
                                Toast.makeText(itemView.getContext().getApplicationContext(),"Missing",Toast.LENGTH_SHORT).show();
                            }else{
                                //TODO:remove from list.
                                Toast.makeText(itemView.getContext().getApplicationContext(),"Added to Cart",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, SWIPE_DELAY_TIME);



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

        private void changeItemColorCategory(int position) {
            //todo: update item color;
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewLiveShoppingItemListAdapter(ArrayList<DataObjectItem> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_live_shopping_item_list_row, parent, false);

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
        holder.itemColorCategory.setBackgroundColor(Color.parseColor(mDataset.get(position).getmItemCategoryColor()));

        //support API 15 (Jellybean)
//        int sdk = android.os.Build.VERSION.SDK_INT;
//        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            holder.itemBG.setBackgroundDrawable(mDataset.get(position).getmImageItem());
//        } else {
//            holder.itemBG.setBackground(mDataset.get(position).getmImageItem());
//        }

    }


    @Override
    public void onItemRemove(int position) {
        DataObjectItem dataObjectItem = mDataset.get(position);
        if(dataObjectItem != null) {

            String relationshipId = dataObjectItem.getmItemsListRelationShipId();

            ParseQuery<ParseObject> query = new ParseQuery("n_itemsListsRelationships");
            query.whereEqualTo("objectId", relationshipId);
            try {
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                    if(list.size() > 0){
                        list.get(0).deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.d("Parse", "Item with id  removed from n_itemsListsRelationships table");
                            }
                        });
                    }
                    }
                });

            } catch (Exception ex) {
                String strex = ex.toString();
            }
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
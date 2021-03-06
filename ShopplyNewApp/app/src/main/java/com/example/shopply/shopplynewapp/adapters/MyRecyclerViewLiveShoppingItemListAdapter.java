package com.example.shopply.shopplynewapp.adapters;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.shopply.shopplynewapp.activities.LiveShoppingCardView;
import com.example.shopply.shopplynewapp.activities.ShoppingListCardView;
import com.example.shopply.shopplynewapp.dataObjects.DataObjectItem;
import com.example.shopply.shopplynewapp.R;
import com.example.shopply.shopplynewapp.tasks.MissingItemPushNotificationTask;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilp on 16/05/2015.
 */
public class MyRecyclerViewLiveShoppingItemListAdapter extends RecyclerView.Adapter<MyRecyclerViewLiveShoppingItemListAdapter.DataObjectHolder> implements RemoveItemListener{

    private static String LOG_TAG = "MRV-LS-ItemListAdapter";
    private static ArrayList<DataObjectItem> mDataset;
    private static MyClickListener myClickListener;
    private static final int SWIPE_DELAY_TIME = 500;
    private static int totalItemsCounter =0;
    private static int missingItemsCounter=0;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        RemoveItemListener removeItemListener;
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
            itemColorCategory = (ImageView) itemView.findViewById(R.id.imageViewCategoryColor);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.liveShoppingItemSwipeLayout);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.live_shopping_left_wrapper));
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, itemView.findViewById(R.id.live_shopping_right_wrapper));

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {
                }

                @Override
                public void onOpen(final SwipeLayout layout) {
                    Log.i("TEST", "buttom layout ID " + layout.getCurrentBottomView().getId() + " left: " + R.id.live_shopping_left_wrapper + " Right: " + R.id.live_shopping_right_wrapper);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (layout.getCurrentBottomView().getId() == R.id.live_shopping_left_wrapper) {
                                //missing item
                                missingItemsCounter++;
                                Toast.makeText(itemView.getContext().getApplicationContext(), "Missing item", Toast.LENGTH_SHORT).show();
                                notifySharedFriendsForMissingItem(getPosition());
                            } else {
                                //added to cart
                                Toast.makeText(itemView.getContext().getApplicationContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
                                removeItemListener.onItemRemove(itemView, getPosition());
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

        private void notifySharedFriendsForMissingItem(final int position) {
            final String itemId = mDataset.get(position).getmItemId();

            ParseQuery<ParseObject> itemQuery = ParseQuery.getQuery("n_itemsListsRelationships");
            itemQuery.whereEqualTo("objectId", itemId);
            itemQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            ParseObject itemListObject = list.get(0).getParseObject("listID");
                            String listId = itemListObject.getObjectId();

                            MissingItemPushNotificationTask missingItemPushNotificationTask = new MissingItemPushNotificationTask();
                            missingItemPushNotificationTask.execute(ParseUser.getCurrentUser().getString("FacebookUserID"), listId, mDataset.get(position).getmItemName());
                            removeItemListener.onItemRemove(itemView, getPosition());
                        }else{
                            Log.i(LOG_TAG,"itemSize == 0");
                        }
                    } else {
                        Log.i(LOG_TAG,"itemQuery failed. e = " + e.getMessage());
                    }
                }
            });
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
        totalItemsCounter = myDataset.size();
        missingItemsCounter = 0;
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
    }


    @Override
    public void onItemRemove(View itemView, int position) {
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
                                Log.d(LOG_TAG, "Item with id  removed from n_itemsListsRelationships table");
                            }
                        });
                    }
                    }
                });

            } catch (Exception ex) {
                Log.i(LOG_TAG, ex.getMessage());
            }
        }

        mDataset.remove(position);
        notifyDataSetChanged();
        if(mDataset.isEmpty()){
            displayFinishShoppingScreen(itemView);
        }
    }

    private void displayFinishShoppingScreen(final View view) {
        LayoutInflater li = LayoutInflater.from(view.getContext());
        View promptsView = li.inflate(R.layout.live_shopping_summary, null);

        TextView totalTime = (TextView)promptsView.findViewById(R.id.textViewTime);
        ((LiveShoppingCardView)view.getContext()).stopTimer();
        totalTime.setText(((LiveShoppingCardView)view.getContext()).getShippingClockText());

        TextView totalItems = (TextView)promptsView.findViewById(R.id.textViewTotalItems);
        totalItems.setText(""+totalItemsCounter);

        TextView missingItems = (TextView)promptsView.findViewById(R.id.textViewMissingItems);
        missingItems.setText(""+missingItemsCounter);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setView(promptsView);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent mainIntent = new Intent(view.getContext(), ShoppingListCardView.class);
                        view.getContext().startActivity(mainIntent);
                    }
                });
        // create and show alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
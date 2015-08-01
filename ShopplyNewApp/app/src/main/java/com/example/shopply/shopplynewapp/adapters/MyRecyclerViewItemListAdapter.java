package com.example.shopply.shopplynewapp.adapters;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.example.shopply.shopplynewapp.dataObjects.DataObjectItem;
import com.example.shopply.shopplynewapp.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilp on 16/05/2015.
 */
public class MyRecyclerViewItemListAdapter extends RecyclerView.Adapter<MyRecyclerViewItemListAdapter.ItemListDataObjectHolder> implements RemoveItemListener, ChangeItemColorListener{

    private static String LOG_TAG = "MyRecyclerViewItemListAdapter";
    private static ArrayList<DataObjectItem> mDataset;
    private static MyClickListener myClickListener;
    private static final int SWIPE_DELAY_TIME = 1500;



    public static class ItemListDataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ChangeItemColorListener changeItemColorListener;
        RemoveItemListener removeItemListener;
        //ImageView itemBG;
        ImageView itemCategoryColor;
        TextView itemName;
        TextView itemAmount;
        TextView itemDeleted;
        SwipeLayout swipeLayout;


        public ItemListDataObjectHolder(final View itemView) {
            super(itemView);
            itemDeleted = (TextView) itemView.findViewById(R.id.textViewDeleteItem);

            itemName = (TextView) itemView.findViewById(R.id.ItemListName);
            itemAmount = (TextView) itemView.findViewById(R.id.ItemListAmount);
            //itemBG = (ImageView) itemView.findViewById(R.id.ItemListImage);
            itemCategoryColor = (ImageView) itemView.findViewById(R.id.imageViewCategoryColor);
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
                    String correntColor = mDataset.get(getPosition()).getmItemCategoryColor();
                    switch (correntColor){
                        case "#03A9F4":
                            ((ImageButton)itemView.findViewById(R.id.imageViewColor1)).setImageResource(R.drawable.ic_checkmark);
                            break;
                        case "#00BCD4":
                            ((ImageButton)itemView.findViewById(R.id.imageViewColor2)).setImageResource(R.drawable.ic_checkmark);
                            break;
                        case "#009688":
                            ((ImageButton)itemView.findViewById(R.id.imageViewColor3)).setImageResource(R.drawable.ic_checkmark);
                            break;
                        case "#4CAF50":
                            ((ImageButton)itemView.findViewById(R.id.imageViewColor4)).setImageResource(R.drawable.ic_checkmark);
                            break;
                        case "#8BC34A":
                            ((ImageButton)itemView.findViewById(R.id.imageViewColor5)).setImageResource(R.drawable.ic_checkmark);
                            break;
                        case "#CDDC39":
                            ((ImageButton)itemView.findViewById(R.id.imageViewColor6)).setImageResource(R.drawable.ic_checkmark);
                            break;
                    }

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    Log.i("TEST", "buttom layout ID " + layout.getCurrentBottomView().getId() + " left: " + R.id.left_wrapper + " Right: " + R.id.right_wrapper);
                    if (layout.getCurrentBottomView().getId() == R.id.left_wrapper) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                removeItemListener.onItemRemove(itemView, getPosition());
                                Toast.makeText(itemView.getContext().getApplicationContext(), "item deleted", Toast.LENGTH_SHORT).show();
                            }
                        }, SWIPE_DELAY_TIME);
                    } else {
                        changeItemColorCategory(itemView, getPosition());
                    }


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

        private void changeItemColorCategory(final View itemView, final int position) {
            final List<ImageButton> colors = new ArrayList<>();

            colors.add((ImageButton) itemView.findViewById(R.id.imageViewColor1));
            colors.add((ImageButton) itemView.findViewById(R.id.imageViewColor2));
            colors.add((ImageButton) itemView.findViewById(R.id.imageViewColor3));
            colors.add((ImageButton) itemView.findViewById(R.id.imageViewColor4));
            colors.add((ImageButton) itemView.findViewById(R.id.imageViewColor5));
            colors.add((ImageButton) itemView.findViewById(R.id.imageViewColor6));

            //TODO load checkbox icon :
            for(final ImageButton imageButton : colors){
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(view.getContext().getApplicationContext(),"arr i=" +colors.indexOf(imageButton) + ",pos=" + getPosition(),Toast.LENGTH_SHORT).show();


                        ParseObject point = ParseObject.createWithoutData("n_items", mDataset.get(position).getmItemId());
                        final int categoryColor =((ColorDrawable)imageButton.getBackground()).getColor();
                        point.put("itemCategoryColor", String.format("#%06X", (0xFFFFFF & categoryColor)));
                        point.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    changeItemColorListener.onChangeItemColor(categoryColor, position);

                                } else {
                                    // The save failed.
                                    Log.i("PARSE", " Save Exception" + e.getMessage());
                                }
                            }
                        });
                        for(final ImageButton img : colors){
                            img.setImageResource(0);
                        }
                        imageButton.setImageResource(R.drawable.ic_checkmark);
                    }
                });
            }
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
    public void onChangeItemColor(int color, int position) {
        mDataset.get(position).setmItemCategoryColor(String.format("#%06X", (0xFFFFFF & color)));
        this.notifyItemChanged(position);
    }

    @Override
    public ItemListDataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_item_list_row, parent, false);

        ItemListDataObjectHolder dataObjectHolder = new ItemListDataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(ItemListDataObjectHolder holder, int position) {
        holder.itemName.setText(mDataset.get(position).getmItemName());
        holder.removeItemListener = this;
        holder.changeItemColorListener = this;

        String itemType;
        if(mDataset.get(position).getmItemType()==0){
            itemType = "QTY";
        } else{
            itemType = "KG";
        }

        holder.itemAmount.setText(String.format("%d %s", mDataset.get(position).getmItemAmount(), itemType));
        holder.itemCategoryColor.setBackgroundColor(Color.parseColor(mDataset.get(position).getmItemCategoryColor()));
/*
        //support API 15 (Jellybean)
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.itemBG.setBackgroundDrawable(mDataset.get(position).getmImageItem());
        } else {
            holder.itemBG.setBackground(mDataset.get(position).getmImageItem());
        }
 */
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

    public void addItem(DataObjectItem dataObj) {
        mDataset.add(dataObj);
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
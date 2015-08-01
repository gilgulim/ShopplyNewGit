package com.example.shopply.shopplynewapp.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.shopply.shopplynewapp.dataObjects.DataObjectItem;
import com.example.shopply.shopplynewapp.R;
import com.example.shopply.shopplynewapp.adapters.MyRecyclerViewLiveShoppingItemListAdapter;
import com.facebook.appevents.AppEventsLogger;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LiveShoppingCardView extends ActionBarActivity {

    private RecyclerView mRecyclerViewItem;
    private RecyclerView.Adapter mAdapterItem;
    private RecyclerView.LayoutManager mLayoutManagerItem;
    private static String TAG = " > > > LiveShopping:";
    private String shoppingListObjectID;
    private ArrayList<DataObjectItem> results = new ArrayList<DataObjectItem>();

    private LayoutInflater li;
    private View promptsView;
    private TextView shoppingClock;
    private long startTime = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            String timeText;
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;

            timeText = hours + ":" + (minutes < 10 ? "0":"") + minutes + ":" + (seconds < 10 ? "0":"") + seconds;
            shoppingClock.setText(timeText);

            timerHandler.postDelayed(this, 500);
        }
    };
    public void stopTimer(){
        timerHandler.removeCallbacks(timerRunnable);
    }
    public String getShippingClockText(){
        return shoppingClock.getText().toString();
    }
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_shopping_card_view);
        initData();
    }

    private void initData() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.ActionBarColor)));
        shoppingListObjectID = getIntent().getStringExtra("listObjectID");
        Log.i(TAG, "item ID = " + R.id.my_recycler_view_item);

        mRecyclerViewItem = (RecyclerView) findViewById(R.id.my_recycler_view_live_shopping_item);
        mRecyclerViewItem.setHasFixedSize(true);
        mLayoutManagerItem = new LinearLayoutManager(this);
        mRecyclerViewItem.setLayoutManager(mLayoutManagerItem);

        mAdapterItem = new MyRecyclerViewLiveShoppingItemListAdapter(getDataSet());
        mRecyclerViewItem.setAdapter(mAdapterItem);

        shoppingClock = (TextView) findViewById(R.id.textViewTime);
        shoppingClock.setText("00:00:00");
        startClockThread();
    }

    private void startClockThread() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

    }


    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewLiveShoppingItemListAdapter) mAdapterItem).setOnItemClickListener(new MyRecyclerViewLiveShoppingItemListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(TAG, " Clicked on Item " + position);
            }
        });

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_live_shopping_card_view, menu);
        setTitle("");
        final ParseQuery<ParseObject> shoppingListObject = ParseQuery.getQuery("n_shoppingLists");
        shoppingListObject.whereEqualTo("objectId", shoppingListObjectID);
        shoppingListObject.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        setTitle(list.get(0).getString("shoppingListName"));
                    }
                } else {
                    Log.i(TAG, "retrieve shopping list failed, error = " + e.getMessage());
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<DataObjectItem> getDataSet() {
        li = LayoutInflater.from(this);
        promptsView = li.inflate(R.layout.new_item_dialog, null);

        final ParseQuery<ParseObject> shoppingListObject = ParseQuery.getQuery("n_shoppingLists");
        shoppingListObject.whereEqualTo("objectId", shoppingListObjectID);
        try{
            List<ParseObject> listOfShoppingListObjects = shoppingListObject.find();
            if (listOfShoppingListObjects.size() > 0 ){
                ParseQuery<ParseObject> query = new ParseQuery("n_itemsListsRelationships");
                query.whereEqualTo("listID", listOfShoppingListObjects.get(0));
                query.include("itemID");
                List<ParseObject> listOfRelationships = query.find();
                if (listOfRelationships.size() > 0){
                    List<DataObjectItem> tempList = new ArrayList<DataObjectItem>();
                    Comparator<DataObjectItem> comparator = new Comparator<DataObjectItem>() {
                        @Override
                        public int compare(DataObjectItem o1, DataObjectItem o2) {
                            return o1.getmItemCategoryColor().compareTo(o2.getmItemCategoryColor());
                        }
                    };

                    for (ParseObject itemObject : listOfRelationships){
                        ParseObject itemRelationshipObject = itemObject.getParseObject("itemID");
                        Log.i(TAG, "object ID = " + itemObject.getObjectId() + ", listID = " + itemRelationshipObject.getObjectId());

                        ParseQuery<ParseObject> itemQuery = new ParseQuery("n_items");
                        itemQuery.whereEqualTo("objectId", itemRelationshipObject.getObjectId());

                        List<ParseObject> listOfItems = itemQuery.find();
                        Log.i(TAG, "size of item of selected list result = " + listOfItems.size());
                        if(listOfItems.size() > 0 ){
                            String itemName = listOfItems.get(0).getString("itemName");
                            int itemQty = listOfItems.get(0).getInt("itemQty");
                            String itemCategoryColor = listOfItems.get(0).getString("itemCategoryColor");
                            String itemQtyType = listOfItems.get(0).getString("itemQtyType");
                            DataObjectItem item = new DataObjectItem(itemObject.getObjectId(), itemName, itemCategoryColor, itemQty , (itemQtyType.equals("QTY") ? 0 : 1));

                            tempList.add(item);
                        }
                    }
                    Collections.sort(tempList,comparator);
                    for(DataObjectItem data : tempList){
                        results.add(data);
                    }

                }
            }

        } catch (ParseException e) {
            Log.i(TAG,"get shopping list object with items data failed, error = " + e.getMessage());
        }
        return results;
    }
}

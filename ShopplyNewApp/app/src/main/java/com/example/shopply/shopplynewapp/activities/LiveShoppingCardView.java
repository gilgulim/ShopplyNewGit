package com.example.shopply.shopplynewapp.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
import java.util.List;

public class LiveShoppingCardView extends ActionBarActivity {

    private int itemTypeIconsIndex;
    private RecyclerView mRecyclerViewItem;
    private RecyclerView.Adapter mAdapterItem;
    private RecyclerView.LayoutManager mLayoutManagerItem;
    private static String TAG = " > > > LiveShopping:";
    private String shoppingListObjectID;
    private ArrayList results = new ArrayList<DataObjectItem>();
    private int itemIndex = 0;
    private LayoutInflater li;
    private View promptsView;
    private TextView shoppingClock;
    private int hh=0,mm=0;

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
        Thread t = new Thread() {
            @Override
        public void run(){
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(hh > 99){
                                    hh=0;
                                }
                                if(mm > 59){
                                    mm=0;
                                    hh++;
                                }
                                shoppingClock.setText((hh < 10 ? "0" + hh : hh) + ":" +
                                        (mm < 10 ? "0" + mm : mm));
                                Log.i(TAG,(hh < 10 ? "0" + hh : hh) + ":" +
                                        (mm < 10 ? "0" + mm : mm));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
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
        // Inflate the menu; this adds items to the action bar if it is present.
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<DataObjectItem> getDataSet() {
        //Drawable img1;
        li = LayoutInflater.from(this);
        promptsView = li.inflate(R.layout.new_item_dialog, null);
        //ArrayList<ImageButton> itemTypeIcons = getItemTypeIconsImageViewArray(promptsView);

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
                            //int itemTypeIndex = listOfItems.get(0).getInt("itemTypeIndex");
                            String itemCategoryColor = listOfItems.get(0).getString("itemCategoryColor");
                            String itemQtyType = listOfItems.get(0).getString("itemQtyType");
                            //img1 = itemTypeIcons.get(itemTypeIndex).getDrawable();
                            DataObjectItem item = new DataObjectItem(itemObject.getObjectId(), itemName, itemCategoryColor, itemQty , (itemQtyType.equals("QTY") ? 0 : 1));
                            results.add(itemIndex++,item);
                        }
                    }
                }
            }

        } catch (ParseException e) {
            Log.i(TAG,"get shopping list object with items data failed, error = " + e.getMessage());
        }

        return results;
    }

    private ArrayList<ImageButton> getItemTypeIconsImageViewArray(View promptsView) {
        ArrayList<ImageButton> itemTypeIcons = new ArrayList<ImageButton>();
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonApple));
        itemTypeIcons.get(0).setColorFilter(Color.parseColor("#9DC709"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonBanana));
        itemTypeIcons.get(1).setColorFilter(Color.parseColor("#EEDB25"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonBoxWheat));
        itemTypeIcons.get(2).setColorFilter(Color.parseColor("#8CC7FA"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonBroccoli));
        itemTypeIcons.get(3).setColorFilter(Color.parseColor("#54A23C"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonCarrot));
        itemTypeIcons.get(4).setColorFilter(Color.parseColor("#FB7519"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonCheese));
        itemTypeIcons.get(5).setColorFilter(Color.parseColor("#FCC812"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonChicken));
        itemTypeIcons.get(6).setColorFilter(Color.parseColor("#A6712D"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonCleaning));
        itemTypeIcons.get(7).setColorFilter(Color.parseColor("#000000"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonCroissant));
        itemTypeIcons.get(8).setColorFilter(Color.parseColor("#E48E29"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonFavorite));
        itemTypeIcons.get(9).setColorFilter(Color.parseColor("#FFB404"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonFish));
        itemTypeIcons.get(10).setColorFilter(Color.parseColor("#198298"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonFullChicken));
        itemTypeIcons.get(11).setColorFilter(Color.parseColor("#A6712D"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonMilk));
        itemTypeIcons.get(12).setColorFilter(Color.parseColor("#F8F5F0"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonOil));
        itemTypeIcons.get(13).setColorFilter(Color.parseColor("#E5BE00"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonSteak));
        itemTypeIcons.get(14).setColorFilter(Color.parseColor("#8E3018"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonTomato));
        itemTypeIcons.get(15).setColorFilter(Color.parseColor("#DC2610"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonWheat));
        itemTypeIcons.get(16).setColorFilter(Color.parseColor("#EBD37D"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonWine));
        itemTypeIcons.get(17).setColorFilter(Color.parseColor("#8E0000"));
        itemTypeIcons.add((ImageButton) promptsView.findViewById(R.id.imageButtonBread));
        itemTypeIcons.get(18).setColorFilter(Color.parseColor("#D79A35"));

        return itemTypeIcons;
    }
}

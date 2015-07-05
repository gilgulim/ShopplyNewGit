package com.example.shopply.shopplynewapp.activities;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.shopply.shopplynewapp.DataObjectItem;
import com.example.shopply.shopplynewapp.R;
import com.example.shopply.shopplynewapp.adapters.MyRecyclerViewItemListAdapter;
import com.facebook.appevents.AppEventsLogger;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class ItemListCardView extends ActionBarActivity {
    private RecyclerView mRecyclerViewItem;
    private RecyclerView.Adapter mAdapterItem;
    private RecyclerView.LayoutManager mLayoutManagerItem;
    private static String TAG = " > > > ShoppingListEditMode:";
    private String shoppingListObjectID;
    private ArrayList results = new ArrayList<DataObjectItem>();
    private int itemIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list_card_view);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ActionBarColor)));
        shoppingListObjectID = getIntent().getStringExtra("listObjectID");
        mRecyclerViewItem = (RecyclerView) findViewById(R.id.my_recycler_view_item);
        mRecyclerViewItem.setHasFixedSize(true);
        mLayoutManagerItem = new LinearLayoutManager(this);
        mRecyclerViewItem.setLayoutManager(mLayoutManagerItem);
        mAdapterItem = new MyRecyclerViewItemListAdapter(getDataSet());
        mRecyclerViewItem.setAdapter(mAdapterItem);

        // Code to Add an item with default animation
        //((MyRecyclerViewShoppingListAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewShoppingListAdapter) mAdapter).deleteItem(index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_list_card_view, menu);
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
        if (id == R.id.addItem) {
            addNewItem();
            //fireNewShoppingListDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNewItem() {

        final ParseObject listItem = new ParseObject("n_items");
        listItem.put("itemName", "new Item");
        listItem.put("itemQty", 1);
        listItem.put("itemQtyType", "QTY");

        final ParseQuery<ParseObject> shoppingListObject = ParseQuery.getQuery("n_shoppingLists");
        shoppingListObject.whereEqualTo("objectId", shoppingListObjectID);
        shoppingListObject.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null){
                    Log.i(TAG, "shopping lists size = " + list.size());
                    if (list.size() > 0) {
                        final ParseObject itemsListsRelationships = new ParseObject("n_itemsListsRelationships");
                        itemsListsRelationships.put("itemID", listItem);
                        itemsListsRelationships.put("listID", list.get(0));
                        itemsListsRelationships.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null){
                                    Drawable img1;
                                    Resources res = getResources();
                                    img1 = res.getDrawable(R.drawable.list_bg_supermarket);
                                    DataObjectItem item = new DataObjectItem("new Item", img1, 3 , 1);
                                    ((MyRecyclerViewItemListAdapter) mAdapterItem).addItem(item, itemIndex++);
                                }else {
                                    Log.i(TAG, "save new item failed, error = " + e.getMessage());
                                }
                            }
                        });
                    }
                }else {
                    Log.i(TAG, "retrieve shopping list failed, error = " + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewItemListAdapter) mAdapterItem).setOnItemClickListener(new MyRecyclerViewItemListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(TAG, " Clicked on Item " + position);
            }
        });

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    private ArrayList<DataObjectItem> getDataSet() {
        Drawable img1,img2;
        Resources res = getResources();
        img1 = res.getDrawable(R.drawable.list_bg_supermarket);


        final ParseQuery<ParseObject> shoppingListObject = ParseQuery.getQuery("n_shoppingLists");
        shoppingListObject.whereEqualTo("objectId", shoppingListObjectID);
        try{
            List<ParseObject> listOfshoppingListObjects = shoppingListObject.find();
            if (listOfshoppingListObjects.size() > 0 ){
                ParseQuery<ParseObject> query = new ParseQuery("n_itemsListsRelationships");
                query.whereEqualTo("listID",listOfshoppingListObjects.get(0));
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
                            String itemQtyType = listOfItems.get(0).getString("itemQtyType");
                            DataObjectItem item = new DataObjectItem(itemName, img1, itemQty , (itemQtyType == "QTY" ? 0 : 1));
                            results.add(itemIndex++,item);
                        }
                    }
                }
            }

        } catch (ParseException e) {
            Log.i(TAG,"get shopping list object with items data failed, error = " + e.getMessage());
        }


        return results;


        /*
        ParseQuery<ParseObject> query = new ParseQuery("n_usersListsRelationships");
        query.whereEqualTo("userID", ParseUser.getCurrentUser());
        query.include("listID");
        try {
            List<ParseObject> listOfRelationships = query.find();
            if (listOfRelationships.size() > 0){
                for (ParseObject listObject : listOfRelationships) {
                    ParseObject shoppingListRelationshipObject = (ParseObject) listObject.getParseObject("listID");
                    Log.i(TAG, "object ID = " + listObject.getObjectId() + ", listID = " + shoppingListRelationshipObject.getObjectId());
                    ParseQuery<ParseObject> listQuery = new ParseQuery("n_shoppingLists");
                    listQuery.whereEqualTo("objectId", shoppingListRelationshipObject.getObjectId());
                    listQuery.whereEqualTo("shoppingListIsDeleted", false);
                    try {
                        List<ParseObject> listOfShoppingLists = listQuery.find();
                        if (listOfShoppingLists.size() > 0) {
                            String shoppingListName = listOfShoppingLists.get(0).getString("shoppingListName");
                            DataObjectShoppingList shoppingListItem = new DataObjectShoppingList(listOfShoppingLists.get(0).getObjectId(),shoppingListName, img1);
                            results.add(itemIndex++, shoppingListItem);
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (ParseException e) {
            Log.i(TAG, "query: get usersListsRelationships :: Error: " + e.getMessage());
        }
        return results;
         */
    }


    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}

package com.example.shopply.shopplynewapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ToggleButton;

import com.example.shopply.shopplynewapp.dataObjects.DataObjectItem;
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
    private LayoutInflater li;
    private View promptsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list_card_view);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.ActionBarColor)));
        shoppingListObjectID = getIntent().getStringExtra("listObjectID");
        mRecyclerViewItem = (RecyclerView) findViewById(R.id.my_recycler_view_item);
        mRecyclerViewItem.setHasFixedSize(true);
        mLayoutManagerItem = new LinearLayoutManager(this);
        mRecyclerViewItem.setLayoutManager(mLayoutManagerItem);

        mAdapterItem = new MyRecyclerViewItemListAdapter(getDataSet());
        mRecyclerViewItem.setAdapter(mAdapterItem);

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
            fireNewItemDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void fireNewItemDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        if(promptsView.getParent()!=null){
            //create new view for dialog
            promptsView = li.inflate(R.layout.new_item_dialog, null);
        }
        alertDialogBuilder.setView(promptsView);

        final EditText itemName = (EditText) promptsView.findViewById(R.id.editTextNewItemName);
        final ToggleButton itemQtyType = (ToggleButton) promptsView
                .findViewById(R.id.toggleButtonItemQtyType);
        final NumberPicker itemQty = (NumberPicker) promptsView
                .findViewById(R.id.numberPickerItemQty);
        itemQty.setValue(1);
        itemQty.setMaxValue(99);
        itemQty.setWrapSelectorWheel(true);
        itemQty.setMinValue(1);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                addNewItem("#CDDC39", String.valueOf(
                                        itemName.getText())
                                        , itemQtyType.isChecked()
                                        , itemQty.getValue());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create and show alert dialog

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void addNewItem(final String itemCategoryColor, final String s, final boolean checked
            , final int value) {

        final ParseObject listItem = new ParseObject("n_items");
        listItem.put("itemName", s);
        listItem.put("itemQty", value);
        listItem.put("itemQtyType", (!checked ? "QTY" : "KG"));
        listItem.put("itemCategoryColor", itemCategoryColor);

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
                                    String itemId = listItem.getObjectId();
                                    DataObjectItem item = new DataObjectItem(listItem.getObjectId(), s, itemCategoryColor, value , !checked ? 0 : 1);
                                    ((MyRecyclerViewItemListAdapter) mAdapterItem).addItem(item);
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
                    for (ParseObject itemObject : listOfRelationships){
                        ParseObject itemRelationshipObject = itemObject.getParseObject("itemID");
                        Log.i(TAG, "object ID = " + itemObject.getObjectId() + ", listID = " + itemRelationshipObject.getObjectId());
                        ParseQuery<ParseObject> itemQuery = new ParseQuery("n_items");
                        itemQuery.whereEqualTo("objectId", itemRelationshipObject.getObjectId());

                        List<ParseObject> listOfItems = itemQuery.find();
                        Log.i(TAG, "size of item of selected list result = " + listOfItems.size());
                        if(listOfItems.size() > 0 ){

                            String itemId = listOfItems.get(0).getObjectId();
                            String itemName = listOfItems.get(0).getString("itemName");
                            int itemQty = listOfItems.get(0).getInt("itemQty");
                            String itemQtyType = listOfItems.get(0).getString("itemQtyType");
                            String itemCategoryColor = listOfItems.get(0).getString("itemCategoryColor");
                            DataObjectItem item = new DataObjectItem(itemObject.getObjectId(), itemName, itemCategoryColor, itemQty , (itemQtyType.equals("QTY") ? 0 : 1));
                            results.add(item);
                        }
                    }
                }
            }

        } catch (ParseException e) {
            Log.i(TAG,"get shopping list object with items data failed, error = " + e.getMessage());
        }

        return results;
    }


    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}

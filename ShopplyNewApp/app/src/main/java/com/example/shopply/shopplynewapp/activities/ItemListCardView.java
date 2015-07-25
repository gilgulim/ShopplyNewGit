package com.example.shopply.shopplynewapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;

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

    private int itemTypeIconsIndex;
    private RecyclerView mRecyclerViewItem;
    private RecyclerView.Adapter mAdapterItem;
    private RecyclerView.LayoutManager mLayoutManagerItem;
    private static String TAG = " > > > ShoppingListEditMode:";
    private String shoppingListObjectID;
    private ArrayList results = new ArrayList<DataObjectItem>();
    private int itemIndex = 0;
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

        //load icons in order to retrieve items' icons images


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
            fireNewItemDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void fireNewItemDialog() {
        //default itemCategoryIndex
        //itemTypeIconsIndex=0;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        if(promptsView.getParent()!=null){
            //create new view for dialog
            promptsView = li.inflate(R.layout.new_item_dialog, null);
        }
        alertDialogBuilder.setView(promptsView);

      /*  final ArrayList<ImageButton> itemTypeIcons = getItemTypeIconsImageViewArray(promptsView);
        final ImageView previewIcon = (ImageView)promptsView.findViewById(R.id.imageViewPreview);
        previewIcon.setColorFilter(Color.parseColor("#9DC709"));
        for(final ImageButton imageView:itemTypeIcons){
           imageView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   previewIcon.setImageDrawable(imageView.getDrawable());
                   itemTypeIconsIndex = itemTypeIcons.indexOf(imageView);
               }
           });
        }*/
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
                                addNewItem("#CDDC39", itemTypeIconsIndex, String.valueOf(
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
/*

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

*/
    private void addNewItem(final String itemCategoryColor, int iconTypeIndex, final String s, final boolean checked
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
                                    //Drawable img1;

                                    String itemId = listItem.getObjectId();
                                    //img1 = itemTypeIcons.get(itemTypeIconsIndex).getDrawable();
                                    DataObjectItem item = new DataObjectItem(listItem.getObjectId(), s, itemCategoryColor, value , !checked ? 0 : 1);
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

                            String itemId = listOfItems.get(0).getObjectId();
                            String itemName = listOfItems.get(0).getString("itemName");
                            int itemQty = listOfItems.get(0).getInt("itemQty");
                            int itemTypeIndex = listOfItems.get(0).getInt("itemTypeIndex");
                            String itemQtyType = listOfItems.get(0).getString("itemQtyType");
                            String itemCategoryColor = listOfItems.get(0).getString("itemCategoryColor");
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


    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}

package com.example.shopply.shopplynewapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import com.example.shopply.shopplynewapp.DataObjectShoppingList;
import com.example.shopply.shopplynewapp.R;
import com.example.shopply.shopplynewapp.adapters.IShoppingListButtonsListener;
import com.example.shopply.shopplynewapp.adapters.MyRecyclerViewShoppingListAdapter;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class ShoppingListCardView extends ActionBarActivity implements IShoppingListButtonsListener {


    private RecyclerView mRecyclerView;
    private MyRecyclerViewShoppingListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String  TAG = " > > > ShoppingLists:";
    private ArrayList results = new ArrayList<DataObjectShoppingList>();
    private int itemIndex = 0;
    private int listBgIndex=1;
    private static final int MAX_LIST_BG_INDEX = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_card_view);
        mAdapter = new MyRecyclerViewShoppingListAdapter(getDataSet());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ActionBarColor)));

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_shopping_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setShoppingListItemButtonsListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_list_card_view, menu);
        this.setTitle(ParseUser.getCurrentUser().getString("username"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addShoppingList) {
            fireNewShoppingListDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fireNewShoppingListDialog() {
       LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.new_list_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                addNewShoppingList(String.valueOf(userInput.getText()));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create and show alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    private ArrayList<DataObjectShoppingList> getDataSet() {
        final Drawable img1;
        Resources res = getResources();
        img1 = res.getDrawable(R.drawable.list_bg_supermarket);

        ParseQuery<ParseObject> query = new ParseQuery("n_usersListsRelationships");
        query.whereEqualTo("userID", ParseUser.getCurrentUser());
        query.include("listID");
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (ParseObject listObject : list) {
                            ParseObject shoppingListRelationshipObject = (ParseObject) listObject.getParseObject("listID");
                            Log.i(TAG, "object ID = " + listObject.getObjectId() + ", listID = " + shoppingListRelationshipObject.getObjectId());
                            ParseQuery<ParseObject> listQuery = new ParseQuery("n_shoppingLists");
                            listQuery.whereEqualTo("objectId", shoppingListRelationshipObject.getObjectId());
                            listQuery.whereEqualTo("shoppingListIsDeleted", false);
                            listQuery.findInBackground(new FindCallback<ParseObject>() {

                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        if (list.size() > 0) {
                                            String shoppingListName = list.get(0).getString("shoppingListName");
                                            DataObjectShoppingList shoppingListItem = new DataObjectShoppingList(list.get(0).getObjectId(), shoppingListName, img1);
                                            results.add(itemIndex++, shoppingListItem);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        Log.i(TAG, "query: get ListsData :: Error: " + e.getMessage());
                                    }
                                }
                            });
                        }

                        mAdapter.setDataset(results);
                    }
                } else {
                    Log.i(TAG, "query: get usersListsRelationships :: Error: " + e.getMessage());
                }
            }
        });
        return results;
    }

    private void addNewShoppingList(String shoppingListName) {

        final String name = shoppingListName;
        final ParseObject shoppingList = new ParseObject("n_shoppingLists");
        shoppingList.put("shoppingListName", name);
        shoppingList.put("shoppingListIsDeleted", false);

        final ParseObject userShoppingListRelationship = new ParseObject("n_usersListsRelationships");
        userShoppingListRelationship.put("listID", shoppingList);
        userShoppingListRelationship.put("userID", ParseUser.getCurrentUser());
        userShoppingListRelationship.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Drawable img1;
                    Resources res = getResources();
                    img1 = res.getDrawable(R.drawable.list_bg_supermarket);
                    DataObjectShoppingList objSuperMarket = new DataObjectShoppingList(shoppingList.getObjectId(), name, img1);
                    ((MyRecyclerViewShoppingListAdapter) mAdapter).addItem(objSuperMarket, itemIndex++);
                } else {
                    Log.i(TAG, "save to usersShoppingListRelationship failed, error = " + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onShoppingListButtonClicked(ShoppingListButtonType buttonType, int position) {

        switch (buttonType){
            case BTN_DISCARD:
                Log.i(TAG, "BTN_DISCARD " + position);
                fireDeleteShoppingListDialog(position);
                break;

            case BTN_EDIT:
                Log.i(TAG, "BTN_EDIT " + position);
                Intent editIntent = new Intent(ShoppingListCardView.this, ItemListCardView.class);
                editIntent.putExtra("listObjectID", mAdapter.getObjectId(position));
                ShoppingListCardView.this.startActivity(editIntent);
                break;

            case BTN_SHARE:
                Log.i(TAG, "BTN_SHARE " + position);

                Intent intent = new Intent(ShoppingListCardView.this, FacebookFriendsListView.class);
                ShoppingListCardView.this.startActivity(intent);

                break;

            case BTN_TOGGLE_BG:
                Log.i(TAG, "BTN_TOGGLE_BG " + position);
                toggleListBackground(position);
                break;

            case BTN_ITEM_SELECTED:
                Log.i(TAG, "BTN_ITEM_SELECTED " + position);
                Intent shopIntent = new Intent(ShoppingListCardView.this, LiveShoppingCardView.class);
                shopIntent.putExtra("listObjectID", mAdapter.getObjectId(position));
                ShoppingListCardView.this.startActivity(shopIntent);
                break;
        }
    }

    private void toggleListBackground(int position) {
        if (listBgIndex >=MAX_LIST_BG_INDEX){
            listBgIndex = 0;
        }

        Drawable bg;
        Resources res = getResources();
        switch(listBgIndex++){
            case 0:
                bg = res.getDrawable(R.drawable.list_bg_supermarket);
                break;
            case 1:
                bg = res.getDrawable(R.drawable.list_bg_pharmacy);
                break;
            case 2:
                bg = res.getDrawable(R.drawable.list_bg_meat);
                break;
            case 3:
                bg = res.getDrawable(R.drawable.list_bg_memo);
                break;
            default:
                bg = res.getDrawable(R.drawable.list_bg_simple);
                break;
        }
        ((MyRecyclerViewShoppingListAdapter) mAdapter).changeItemBackground(bg, position);
    }

    private void fireDeleteShoppingListDialog(final int position) {
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.delete_list_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(promptsView);

            // set dialog message
            alertDialogBuilder.setCancelable(false).setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteShoppingList(position);
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
            alertDialog.show();
    }

    private void deleteShoppingList(int position) {
        final String objectID = mAdapter.getObjectId(position);
        mAdapter.deleteItem(position);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("n_shoppingLists");
        query.whereEqualTo("objectId", objectID);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        list.get(0).put("shoppingListIsDeleted", true);
                        list.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.i(TAG, "onShoppingListButtonClicked() save list update failed, error = " + e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    Log.i(TAG, "onShoppingListButtonClicked() get list failed, error = " + e.getMessage());
                }
            }
        });
    }
}

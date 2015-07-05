package com.example.shopply.shopplynewapp.activities;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopply.shopplynewapp.DataObjectShoppingList;
import com.example.shopply.shopplynewapp.R;
import com.example.shopply.shopplynewapp.adapters.MyRecyclerViewShoppingListAdapter;
import com.facebook.appevents.AppEventsLogger;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class ShoppingListCardView extends ActionBarActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String  TAG = " > > > ShoppingList:";
    private ArrayList results = new ArrayList<DataObjectShoppingList>();
    private int itemIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_card_view);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ActionBarColor)));

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_shopping_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewShoppingListAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        // Code to remove an item with default animation
        //((MyRecyclerViewShoppingListAdapter) mAdapter).deleteItem(index);


        TextView itemsList = (TextView)findViewById(R.id.goToItemList);
        itemsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ShoppingListCardView.this, ItemListCardView.class);
                ShoppingListCardView.this.startActivity(mainIntent);
                //ShoppingListCardView.this.finish();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_list_card_view, menu);
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
            addNewShoppingList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewShoppingListAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewShoppingListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    private ArrayList<DataObjectShoppingList> getDataSet() {
        Log.i(TAG, "results before: " + results.size());
        final Drawable img1;
        Resources res = getResources();
        img1 = res.getDrawable(R.drawable.list_bg_supermarket);

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
                            Log.i(TAG, "Parse Object = " + listOfShoppingLists.get(0).getObjectId() + " - " + listOfShoppingLists.get(0).getString("shoppingListName"));
                            String shoppingListName = listOfShoppingLists.get(0).getString("shoppingListName");
                            DataObjectShoppingList shoppingListItem = new DataObjectShoppingList(shoppingListName, img1);
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
    }

    private void addNewShoppingList() {
        Drawable img1,img2;
        Resources res = getResources();
        img1 = res.getDrawable(R.drawable.list_bg_supermarket);
        img2 = res.getDrawable(R.drawable.list_bg_pharmacy);
        final DataObjectShoppingList objSuperMarket = new DataObjectShoppingList("Supermarket", img1 );

        ParseObject shoppingList = new ParseObject("n_shoppingLists");
        shoppingList.put("shoppingListName", "myList A" + itemIndex);
        shoppingList.put("shoppingListIsDeleted", false);

        ParseObject userShoppingListRelationship = new ParseObject("n_usersListsRelationships");
        userShoppingListRelationship.put("listID", shoppingList);
        userShoppingListRelationship.put("userID", ParseUser.getCurrentUser());
        userShoppingListRelationship.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
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
}

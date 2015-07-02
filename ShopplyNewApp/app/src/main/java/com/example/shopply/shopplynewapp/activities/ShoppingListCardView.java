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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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

        Toast.makeText(getApplicationContext(), ParseUser.getCurrentUser().getUsername(),Toast.LENGTH_LONG).show();
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
        Drawable img1;
        Resources res = getResources();
        img1 = res.getDrawable(R.drawable.list_bg_supermarket);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("usersListsRelationships");
        query.whereEqualTo("userID", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                final Drawable img1;
                Resources res = getResources();
                img1 = res.getDrawable(R.drawable.list_bg_supermarket);

                if (e == null){
                    if (list.size() > 0 ){
                        for(ParseObject object : list){
                            String listID = (String) object.get("listID");
                            ParseQuery<ParseObject> listQuery = ParseQuery.getQuery("ShoppingLists");
                            listQuery.whereEqualTo("shoppingListIsDeleted", false);
                            listQuery.whereEqualTo("objectID", listID);
                            listQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null){
                                        if (list.size() > 0){
                                            ParseObject shoppingListObject = list.get(0);
                                            String shoppingListName = (String) shoppingListObject.get("shoppingListName");
                                            DataObjectShoppingList shoppingListItem = new DataObjectShoppingList(shoppingListName, img1 );
                                            results.add(itemIndex++, shoppingListItem);
                                        }
                                    } else {
                                        Log.i(TAG, "query: find shopping lists details :: Error: " + e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Log.i(TAG, "query: find user shopping lists :: Error: " + e.getMessage());
                }
            }
        });

        return results;

//        Drawable img2;
//        Resources res = getResources();
//        img1 = res.getDrawable(R.drawable.list_bg_supermarket);
//        img2 = res.getDrawable(R.drawable.list_bg_pharmacy);
//        DataObjectShoppingList objSuperMarket = new DataObjectShoppingList("Supermarket", img1 );
//        DataObjectShoppingList objPharmacy = new DataObjectShoppingList("Pharmacy", img2 );
//        results.add(itemIndex++,objSuperMarket);
//        results.add(itemIndex++,objPharmacy);

    }

    private void addNewShoppingList() {
        Drawable img1,img2;
        Resources res = getResources();
        img1 = res.getDrawable(R.drawable.list_bg_supermarket);
        img2 = res.getDrawable(R.drawable.list_bg_pharmacy);
        final DataObjectShoppingList objSuperMarket = new DataObjectShoppingList("Supermarket", img1 );

        final ParseObject shoppingList = new ParseObject("n_shoppingLists");
        shoppingList.put("shoppingListName", "myList" + itemIndex);
        shoppingList.put("shoppingListIsDeleted", false);
        shoppingList.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseObject userShoppingListRelationship = new ParseObject("n_usersListsRelationships");
                    userShoppingListRelationship.put("listID", ParseObject.createWithoutData("shoppingLists", shoppingList.getObjectId()));
                    userShoppingListRelationship.put("userID", ParseObject.createWithoutData("User", ParseUser.getCurrentUser().getObjectId()));
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


                } else {
                    Log.i(TAG, "save to ShoppingList failed, error = " + e.getMessage());
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

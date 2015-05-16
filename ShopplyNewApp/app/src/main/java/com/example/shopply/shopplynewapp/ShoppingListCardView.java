package com.example.shopply.shopplynewapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
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

import java.util.ArrayList;


public class ShoppingListCardView extends ActionBarActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "ShoppingListCardViewActivity";

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

        // Code to Add an item with default animation
        //((MyRecyclerViewShoppingListAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewShoppingListAdapter) mAdapter).deleteItem(index);


        TextView itemsList = (TextView)findViewById(R.id.goToItemList);
        itemsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ShoppingListCardView.this, ItemListCardView.class);
                ShoppingListCardView.this.startActivity(mainIntent);
                ShoppingListCardView.this.finish();
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
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    private ArrayList<DataObjectShoppingList> getDataSet() {
        ArrayList results = new ArrayList<DataObjectShoppingList>();
        Drawable img1,img2;
        Resources res = getResources();
        img1 = res.getDrawable(R.drawable.list_bg_supermarket);
        img2 = res.getDrawable(R.drawable.list_bg_pharmacy);
        DataObjectShoppingList objSuperMarket = new DataObjectShoppingList("Supermarket", img1 );
        DataObjectShoppingList objPharmacy = new DataObjectShoppingList("Pharmacy", img2 );
        results.add(0,objSuperMarket);
        results.add(1,objPharmacy);
        results.add(2,objSuperMarket);
        results.add(3,objPharmacy);
        results.add(4,objSuperMarket);
        results.add(5,objPharmacy);
        return results;
    }
}

package com.example.shopply.shopplynewapp;

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

import java.util.ArrayList;


public class ItemListCardView extends ActionBarActivity {
    private RecyclerView mRecyclerViewItem;
    private RecyclerView.Adapter mAdapterItem;
    private RecyclerView.LayoutManager mLayoutManagerItem;
    private static String LOG_TAG = "ItemCardViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list_card_view);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ActionBarColor)));

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.addShoppingList) {
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewItemListAdapter) mAdapterItem).setOnItemClickListener(new MyRecyclerViewItemListAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    private ArrayList<DataObjectItem> getDataSet() {
        ArrayList results = new ArrayList<DataObjectItem>();
        Drawable img1,img2;
        Resources res = getResources();
        img1 = res.getDrawable(R.drawable.list_bg_supermarket);
        img2 = res.getDrawable(R.drawable.list_bg_pharmacy);
        DataObjectItem objBanana = new DataObjectItem("Bananas", img1, 3 , 1);
        DataObjectItem objSoda = new DataObjectItem("Soda", img2, 6 , 0);

        results.add(0,objBanana);
        results.add(1,objSoda);
        results.add(2,objBanana);
        results.add(3,objSoda);
        results.add(4,objBanana);
        results.add(5,objSoda);
        results.add(6,objBanana);
        results.add(7,objSoda);
        results.add(8,objBanana);
        results.add(9,objSoda);

        return results;
    }
}

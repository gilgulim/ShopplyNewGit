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

import com.example.shopply.shopplynewapp.DataObjectShoppingList;
import com.example.shopply.shopplynewapp.R;
import com.example.shopply.shopplynewapp.adapters.IShoppingListButtonsListener;
import com.example.shopply.shopplynewapp.adapters.MyRecyclerViewShoppingListAdapter;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;


public class ShoppingListCardView extends ActionBarActivity implements IShoppingListButtonsListener {
    private RecyclerView mRecyclerView;
    private MyRecyclerViewShoppingListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "ShoppingListCardViewActivity";
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
        mAdapter.setShoppingListItemButtonsListener(this);
        mRecyclerView.setAdapter(mAdapter);

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

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    private ArrayList<DataObjectShoppingList> getDataSet() {

        Drawable img1,img2;
        Resources res = getResources();
        img1 = res.getDrawable(R.drawable.list_bg_supermarket);
        img2 = res.getDrawable(R.drawable.list_bg_pharmacy);
        DataObjectShoppingList objSuperMarket = new DataObjectShoppingList("Supermarket", img1 );
        DataObjectShoppingList objPharmacy = new DataObjectShoppingList("Pharmacy", img2 );
        results.add(itemIndex++,objSuperMarket);
        results.add(itemIndex++,objPharmacy);
        return results;
    }

    private void addNewShoppingList() {
        Drawable img1,img2;
        Resources res = getResources();
        img1 = res.getDrawable(R.drawable.list_bg_supermarket);
        img2 = res.getDrawable(R.drawable.list_bg_pharmacy);
        DataObjectShoppingList objSuperMarket = new DataObjectShoppingList("Supermarket", img1 );
        DataObjectShoppingList objPharmacy = new DataObjectShoppingList("Pharmacy", img2 );

        ((MyRecyclerViewShoppingListAdapter)mAdapter).addItem(objSuperMarket,itemIndex++);
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
                mAdapter.deleteItem(position);
                Log.i("Non", "BTN_DISCARD " + position);
                break;
            case BTN_EDIT:
                Intent mainIntent = new Intent(ShoppingListCardView.this, ItemListCardView.class);
                ShoppingListCardView.this.startActivity(mainIntent);
                Log.i("Non", "BTN_EDIT " + position);
                break;
            case BTN_SHARE:
                Log.i("Non", "BTN_SHARE " + position);
                break;
            case BTN_ITEM_SELECTED:
                Log.i("Non", "BTN_ITEM_SELECTED " + position);
                break;
        }
    }
}

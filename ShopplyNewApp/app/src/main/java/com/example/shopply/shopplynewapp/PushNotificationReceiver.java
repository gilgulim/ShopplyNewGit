package com.example.shopply.shopplynewapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.shopply.shopplynewapp.activities.ShoppingListCardView;
import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by Evyatar on 01/08/2015.
 */
public class PushNotificationReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");

        Intent i = new Intent(context, ShoppingListCardView.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }
}

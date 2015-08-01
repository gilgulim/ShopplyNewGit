package com.example.shopply.shopplynewapp.tasks;

import android.os.AsyncTask;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Evyatar on 01/08/2015.
 */
public class ShareListPushNotificationTask extends AsyncTask<String, Void, Void>{

    @Override
    protected Void doInBackground(String... fbUsersIds) {

        String remoteUserFbId = fbUsersIds[0];
        ParseUser localUser = ParseUser.getCurrentUser();
        String localUsername = (String)localUser.get("username");

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("FacebookUserID", remoteUserFbId);

        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatchesQuery("user", userQuery);

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage(String.format("%s shared a shopping list with you.", localUsername));
        try {
            push.send();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        return null;
    }
}

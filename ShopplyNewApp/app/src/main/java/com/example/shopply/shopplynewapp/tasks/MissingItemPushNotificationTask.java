package com.example.shopply.shopplynewapp.tasks;

import android.os.AsyncTask;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Evyatar on 01/08/2015.
 **/
public class MissingItemPushNotificationTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... input) {
        if(input.length != 3)
            return  null;

        String localUserFbId = input[0];
        String listId = input[1];
        String missingItemName = input[2];

        ParseQuery<ParseObject> query = ParseQuery.getQuery("n_shoppingLists");
        query.whereEqualTo("objectId", listId);

        List<ParseObject> lists = null;

        try {
            lists = query.find();

            if(lists!=null && lists.size() >0){

                String listName = (String)lists.get(0).get("shoppingListName");

                List<ParseObject> relationshipsLists = null;

                query = ParseQuery.getQuery("n_usersListsRelationships");
                query.whereEqualTo("listID", lists.get(0));
                query.whereNotEqualTo("userID", ParseUser.getCurrentUser());
                relationshipsLists = query.find();

                if(relationshipsLists != null && relationshipsLists.size() >0){

                    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();

                    ParseQuery pushQuery = ParseInstallation.getQuery();
                    for(int i=0; i<relationshipsLists.size(); i++){
                        ParseObject relationObject = (ParseObject)relationshipsLists.get(i);
                        ParseUser user = (ParseUser) relationObject.get("userID");
                        pushQuery.whereEqualTo("user", user);
                    }

                    // Send push notification to query
                    ParsePush push = new ParsePush();
                    push.setQuery(pushQuery); // Set our Installation query
                    push.setMessage(String.format("Item, %s is missing in shopping list, %s", missingItemName, listName));
                    try {
                        push.send();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }



        return null;
    }
}

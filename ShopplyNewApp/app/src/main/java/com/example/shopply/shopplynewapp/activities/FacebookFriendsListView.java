package com.example.shopply.shopplynewapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopply.shopplynewapp.R;
import com.example.shopply.shopplynewapp.adapters.FbFriendSelectedListener;
import com.example.shopply.shopplynewapp.adapters.MyRecyclerViewFbListAdapter;
import com.example.shopply.shopplynewapp.dataObjects.DataObjectFbFriendItem;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FacebookFriendsListView extends ActionBarActivity implements FbFriendSelectedListener {

    private RecyclerView mRecyclerViewItem;
    private MyRecyclerViewFbListAdapter mAdapterItem;
    private RecyclerView.LayoutManager mLayoutManagerItem;

    private String shoppingListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        shoppingListId = intent.getStringExtra("listId");

        setContentView(R.layout.activity_facebook_friends_list_view);
        mRecyclerViewItem = (RecyclerView)findViewById(R.id.fbFriendsRecycleView);
        mLayoutManagerItem = new LinearLayoutManager(this);
        mRecyclerViewItem.setLayoutManager(mLayoutManagerItem);

        mAdapterItem = new MyRecyclerViewFbListAdapter();
        mAdapterItem.setFbFriendSelectedListener(this);
        mRecyclerViewItem.setAdapter(mAdapterItem);

        getFriendsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_facebook_friends_list_view, menu);
        this.setTitle("Select friends");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFriendSelected(final String fbId) {
        final AlertDialog alertDialog = showLoadingDialog();

        //Get the user's facebook parse object
        ParseQuery<ParseUser>  query = ParseUser.getQuery();
        query.whereEqualTo("FacebookUserID", fbId);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> fbUserslist, ParseException e) {
                boolean isError = false;
                String resultMessage = "";
                if (e == null && fbUserslist.size() > 0) {

                    //Get the list parse object
                    List<ParseObject> listsRes = null;
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("n_shoppingLists");
                    query.whereEqualTo("objectId", shoppingListId);
                    try {

                        listsRes = query.find();

                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                    if (listsRes != null && listsRes.size() > 0) {

                        //Validate that the list is not already set to this user
                        List<ParseObject> relationshipRes = null;
                        query = ParseQuery.getQuery("n_usersListsRelationships");
                        query.whereEqualTo("listID", listsRes.get(0));
                        query.whereEqualTo("userID", fbUserslist.get(0));

                        try {
                            relationshipRes = query.find();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        if (relationshipRes != null && relationshipRes.size() > 0) {
                            //The user already own this list
                            resultMessage = getString(R.string.facebook_err_share_already_exist);
                            isError = true;

                        } else {
                            //The relationship between that user and that list does not exist, create it
                            final ParseObject userShoppingListRelationship = new ParseObject("n_usersListsRelationships");
                            userShoppingListRelationship.put("listID", listsRes.get(0));
                            userShoppingListRelationship.put("userID", fbUserslist.get(0));
                            try {
                                userShoppingListRelationship.save();
                            } catch (ParseException e1) {
                                isError = true;
                            }
                        }
                    }else {
                        isError = true;
                    }

                }else {
                    isError = true;
                }

                //If there is no error
                if(isError){
                    if(resultMessage.length() ==0) {
                        resultMessage = getString(R.string.facebook_err_share_generic);
                    }
                }else {
                    resultMessage = getString(R.string.facebook_share_success);
                }

                alertDialog.hide();

                Toast.makeText(getApplicationContext(), resultMessage,
                        Toast.LENGTH_LONG).show();


            }
        });

    }

    private void getFriendsList(){

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newMyFriendsRequest(
                accessToken, new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                        ArrayList<DataObjectFbFriendItem> dataSet = new ArrayList<DataObjectFbFriendItem>();

                        try {

                            JSONObject jsonObject = graphResponse.getJSONObject();
                            JSONArray array =  jsonObject.getJSONArray("data");
                            for(int i=0; i<array.length(); i++) {

                                JSONObject fbFriendInfo = array.getJSONObject(i);
                                String fullName = fbFriendInfo.getString("name");
                                String fbId = fbFriendInfo.getString("id");
                                JSONObject picture = fbFriendInfo.getJSONObject("picture");
                                picture = picture.getJSONObject("data");
                                String profilePicUrl = picture.getString("url");

                                dataSet.add(new DataObjectFbFriendItem(fbId, fullName, profilePicUrl));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapterItem.setDataset(dataSet);
                        Log.d("Facebook request", jsonArray.toString() + "--\n" + graphResponse.toString());
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private AlertDialog showLoadingDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.loading_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);


        // set dialog message
        alertDialogBuilder.setCancelable(false);

        // create and show alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
        return alertDialog;

    }
}

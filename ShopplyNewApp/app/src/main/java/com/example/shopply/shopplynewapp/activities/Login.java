package com.example.shopply.shopplynewapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.shopply.shopplynewapp.R;
import com.example.shopply.shopplynewapp.tasks.MissingItemPushNotificationTask;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class Login extends Activity implements FacebookCallback<LoginResult> {

    private CallbackManager callbackManager;
    private com.facebook.login.widget.LoginButton btnFacebookLogin;

    private List<String> fbPermissions =  Arrays.asList("public_profile", "user_friends");
    private static final String  TAG = " > > > LoginActivity:";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set required permissions to the Facebook loging button
        btnFacebookLogin = (com.facebook.login.widget.LoginButton) findViewById(R.id.login_button);
        btnFacebookLogin.setReadPermissions(fbPermissions);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, this);

        TextView skipTextView = (TextView)findViewById(R.id.skipTextView);
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: handle anonymous login
                Intent mainIntent = new Intent(Login.this, ShoppingListCardView.class);
                Login.this.startActivity(mainIntent);
            }
        });

        //Check if the user is already connected
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken!=null){
            createAndLoginUser(accessToken.getUserId());

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
       AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {

        String userFbId =  loginResult.getAccessToken().getUserId();
        createAndLoginUser(userFbId);
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException e) {

    }

    private void createAndLoginUser(String userFbId) {

        //Search for the id in Shooply DB
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("FacebookUserID", userFbId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {

                    if (objects.size() == 0) {
                        Log.i(TAG, "creating new user");

                        //create new user
                        ParseUser user = new ParseUser();
                        user.setUsername(Profile.getCurrentProfile().getName());
                        user.setPassword("no password");
                        //user.setEmail("email@example.com");
                        user.put("isFacebookUser", true);
                        user.put("FacebookUserID", Profile.getCurrentProfile().getId());
                        try {
                            user.signUp();
                            user.logIn(user.getUsername(), "no password");
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        //
                    } else {
                        //user exist
                        Log.i(TAG, "user is exist");


                        try {
                            ParseUser.logIn(objects.get(0).getUsername(), "no password");
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }


                    }

                    //Register the parse user as an identification for receiving push notifications
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("user", ParseUser.getCurrentUser());
                    installation.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d("com.parse.push", "Error user ParseInstallation");
                            }
                        }
                    });

                    Intent mainIntent = new Intent(Login.this, ShoppingListCardView.class);
                    Login.this.startActivity(mainIntent);

                }
            }
        });
    }
}

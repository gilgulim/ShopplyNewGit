package com.example.shopply.shopplynewapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopply.shopplynewapp.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class Login extends Activity {

    private CallbackManager callbackManager;
    private static final String  TAG = " > > > LoginActivity:";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.i(TAG, "onSuccess");

                        createAndLoginUser();
                        Intent mainIntent = new Intent(Login.this, ShoppingListCardView.class);
                        Login.this.startActivity(mainIntent);

                    }

                    private void createAndLoginUser() {
                        String profileID = Profile.getCurrentProfile().getId();
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("FacebookUserID", profileID);
                        query.findInBackground(new FindCallback<ParseUser>() {
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (e == null) {

                                    if(objects.size() == 0){
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
                                            user.logIn(user.getUsername(),"no password");
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }

                                        //
                                    }else{
                                        //user exist
                                        Log.i(TAG, "user is exist");


                                        try {
                                            ParseUser.logIn(objects.get(0).getUsername(),"no password");
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                } else {
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i(TAG, "onError");
                    }
                });

        setContentView(R.layout.activity_login);
        TextView skipTextView = (TextView)findViewById(R.id.skipTextView);
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: handle anonymous login
                Intent mainIntent = new Intent(Login.this, ShoppingListCardView.class);
                Login.this.startActivity(mainIntent);
                //Login.this.finish();
            }
        });
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
}

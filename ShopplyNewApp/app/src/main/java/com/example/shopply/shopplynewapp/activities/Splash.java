package com.example.shopply.shopplynewapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.shopply.shopplynewapp.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Objects;


public class Splash extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
        FacebookSdk.sdkInitialize(getApplicationContext());

        //PARSE CODE
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "E8FFzEd5jte9M3G6gkyNCUWMqXMGfWAScQwQDwsb", "5k4kUjotQY2qffgg3R8sublHlAVkWlkFWwujNU1I");
        ParseFacebookUtils.initialize(this);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent loginIntent = new Intent(Splash.this,Login.class);
                Splash.this.startActivity(loginIntent);
            }
        }, SPLASH_DISPLAY_LENGTH);

//Facebook HASH KEY generator
//        PackageInfo info;
//        try {
//            info = getPackageManager().getPackageInfo("com.you.name", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                //String something = new String(Base64.encodeBytes(md.digest()));
//                Log.e("hash key", something);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
//            Log.e("name not found", e1.toString());
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("no such an algorithm", e.toString());
//        } catch (Exception e) {
//            Log.e("exception", e.toString());
//        }
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
}
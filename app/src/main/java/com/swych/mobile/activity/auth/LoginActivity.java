package com.swych.mobile.activity.auth;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.gson.Gson;
import com.swych.mobile.activity.LibraryActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;


import com.facebook.FacebookSdk;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.swych.mobile.R;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Tta2lL1hofGSKgJxTIwpvkE0f";
    private static final String TWITTER_SECRET = "5ebCEoYjBejcrwHlcVqVZBMmxGToHK9iokRmboRXDQJbdul1EY";





    // UI references.

    private TwitterLoginButton twitterLoginButton;
    private SignInButton googleLoginButton;
    private LoginButton facebookLoginButton;
    private GoogleApiClient mGoogleApiClient;
    private String socialNetworkUsed;
    private CallbackManager facebookCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        socialNetworkUsed = "facebook";

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);



    }
    // Onclick methods for buttons.

    public void startUserSignUpActivity(View v){
        Log.i("Login Activity", "Starting signIn process");
    }

    public void startEmailLoginActivity(View v){
        Log.i("Login Activity", "Starting Email Login for user");
        Intent intent = new Intent(this, EmailLogin.class);
        startActivity(intent);

    }

    public void facebookLogin(View v){
        Log.i("Login Activity", "attempting facebook login");
        socialNetworkUsed = "facebook";
        facebookLoginButton = (LoginButton) findViewById(R.id.btnFacebook);
        facebookLoginButton.registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        AccessToken token = loginResult.getAccessToken();
                        Gson gson = new Gson();
                        String credentials  = gson.toJson(token);
                        Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                        intent.putExtra("credentials", credentials);
                        startActivity(intent);

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        System.out.println("on cancel method");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        System.out.println("on error method");
                        System.out.println(exception.toString());
                    }
                });


        facebookLoginButton.callOnClick();

    }

    public void twitterLogin(View v){
        Log.i("Login Activity", "attempting twitter login");
        socialNetworkUsed = "twitter";
        twitterLoginButton = (TwitterLoginButton)
                findViewById(R.id.btnTwitter);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {

                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken token = session.getAuthToken();
                Gson gson = new Gson();
                String credentials = gson.toJson(token);
                Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                intent.putExtra("credentials", credentials);
                startActivity(intent);

            }

            @Override
            public void failure(TwitterException exception) {
                System.out.println("Failure method called.");
            }
        });

        twitterLoginButton.callOnClick();


    }

    public void googleLogin(View v){
        Log.i("Login Activity", "attempting google+ login");
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Pass the activity result to the login button.

        if(socialNetworkUsed.equals("facebook")){
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        else if(socialNetworkUsed.equals("twitter")){
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
        else{
            //google used.
        }


    }

}


package com.swych.mobile.activity.auth;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;

import android.content.IntentSender;
import android.os.AsyncTask;
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
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;
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

import java.io.IOException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Tta2lL1hofGSKgJxTIwpvkE0f";
    private static final String TWITTER_SECRET = "5ebCEoYjBejcrwHlcVqVZBMmxGToHK9iokRmboRXDQJbdul1EY";

    private static final String SERVER_CLIENT_ID="404334429910-7tkt38hj6kbtb954kv2qsotq386innvf.apps.googleusercontent.com";



    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;



    // UI references.
    private boolean mSignInClicked;
    private TwitterLoginButton twitterLoginButton;
    private SignInButton googleLoginButton;
    private LoginButton facebookLoginButton;
    private GoogleApiClient mGoogleApiClient;
    private String socialNetworkUsed;
    private CallbackManager facebookCallbackManager;
    private boolean mIntentInProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        socialNetworkUsed = "facebook";

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Plus.API)
//                .addScope(Plus.SCOPE_PLUS_LOGIN)
//                .build();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Plus.API)
//                .addScope(Plus.SCOPE_PLUS_LOGIN)
//                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);



    }

//    private GoogleApiClient buildGoogleApiClient() {
//        // When we build the GoogleApiClient we specify where connected and
//        // connection failed callbacks should be returned, which Google APIs our
//        // app uses and which OAuth 2.0 scopes our app requests.
//        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Plus.API, Plus.PlusOptions.builder().build())
//                .addScope(Plus.SCOPE_PLUS_LOGIN);
//
//        if (mRequestServerAuthCode) {
//            checkServerAuthConfiguration();
//            builder = builder.requestServerAuthCode(WEB_CLIENT_ID, this);
//        }
//
//        return builder.build();
//    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

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
        socialNetworkUsed = "google";
        googleLoginButton  = (SignInButton) findViewById(R.id.btnGoogle);
        mSignInClicked = true;
        System.out.println(!mGoogleApiClient.isConnecting());
        mGoogleApiClient.disconnect();
        System.out.println(mGoogleApiClient.isConnected());
        System.out.println(mGoogleApiClient.isConnecting());
        if(!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
        else{
            GetIdTokenTask task = new GetIdTokenTask();
            task.execute((Void) null);
        }
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
            if (requestCode == RC_SIGN_IN) {
                if (resultCode != RESULT_OK) {
                    mSignInClicked = false;
                }

                mIntentInProgress = false;

                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.reconnect();
                }
            }
        }


    }

    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        if(socialNetworkUsed.equals("google")){
            GetIdTokenTask task = new GetIdTokenTask();
            task.execute((Void) null);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            if (mSignInClicked && result.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    result.startResolutionForResult(this, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }



    private class GetIdTokenTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            String scopes = "audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID.
            String idToken = "";
            try {
                idToken = GoogleAuthUtil.getToken(getApplicationContext(), account, scopes);
            } catch (IOException e) {
                Log.i("google login", "Error retrieving ID token.", e);
            } catch (GoogleAuthException e) {
                Log.e("google login ", "Error retrieving ID token.", e);
            }
            return idToken;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("google login ", "ID token: " + result);
            if(result.equals("")){
                Log.i("google login", "google login failed");
            }
            else{
                Log.i("google login", "successfull login, proceeding to the LibraryActivity");
                Intent intent  = new Intent(getApplicationContext(), LibraryActivity.class);
                intent.putExtra("credentials", result);
                startActivity(intent);
            }
        }

    }

}


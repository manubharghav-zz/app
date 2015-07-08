package com.swych.mobile.activity.auth;

import android.accounts.Account;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;

import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.swych.mobile.commons.utils.Utils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;


import com.facebook.FacebookSdk;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import com.swych.mobile.R;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONObject;

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


    private UserLoginTask mAuthTask = null;
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "manu@gmail.com:Swych123", "reddy@outlook.com:swych123"
    };
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        socialNetworkUsed = "facebook";

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);


        FancyButton googlePLusButton = (FancyButton) findViewById(R.id.btnGoogleD);
        googlePLusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleLogin(v);
            }
        });


        FancyButton facebookButton = (FancyButton) findViewById(R.id.btnFacebookD);
        facebookButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                facebookLogin(v);
            }
        });

        FancyButton twitterButton = (FancyButton) findViewById(R.id.btnTwitterD);
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterLogin(v);
            }
        });



        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


    }


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


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void attemptLogin(){

        mEmailView.setError(null);
        mPasswordView.setError(null);


        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !Utils.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Utils.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }


        if (cancel){
            focusView.requestFocus();
        }
        else{
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Log.i("Email Login", "successfull login, proceeding to the LibraryActivity");
                Intent intent  = new Intent(getApplicationContext(), LibraryActivity.class);
                JSONObject userData = new JSONObject();
                try {
                    userData.put("email", mEmail);
                    userData.put("password", mPassword);
                }
                catch (Exception e){
                    // do nothing.
                }

                intent.putExtra("credentials", userData.toString());
                startActivity(intent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}


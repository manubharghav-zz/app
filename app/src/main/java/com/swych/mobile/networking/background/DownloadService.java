package com.swych.mobile.networking.background;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.swych.mobile.networking.Details;
import com.swych.mobile.networking.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by manu on 6/21/15.
 */
public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private DownloadType downloadType;


    private static final String TAG = "DownloadService";
    public DownloadService() {
        super(DownloadService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Download started");
        downloadType = (DownloadType)intent.getSerializableExtra("DownloadType");

        switch(downloadType){
            case BOOK:
                downloadBook(intent);
                break;


        }
    }

    private JSONObject downloadData(String requestUrl) throws IOException, JSONException {
        Log.d(TAG, "Downloading from "+ requestUrl);
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        /* forming th java.net.URL object */
        URL url = new URL(requestUrl);
        urlConnection = (HttpURLConnection) url.openConnection();

        /* optional request header */
        urlConnection.setRequestProperty ("Authorization", Details.getBasicAuth());


        urlConnection.setRequestMethod("GET");
        int statusCode = urlConnection.getResponseCode();


        if (statusCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuffer responceBuffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                responceBuffer.append(line);
            }
            reader.close();
            JSONObject response = new JSONObject(responceBuffer.toString());
            return response;


        } else {
            Log.d(TAG, "download failed with response "+ statusCode);
        }
        return null;
    }

    private void downloadBook(Intent intent){
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        String bookName = intent.getStringExtra("bookName");
        String nativeLanguage = intent.getStringExtra("nativeLanguage");
        String foreignLanguage = intent.getStringExtra("foreignLanguage");

        Bundle bundle = new Bundle();
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        try{
            JSONObject nativeLanguageVersion = downloadData(URLs.VERSION+bookName+"/"+nativeLanguage);
            JSONObject forienLanguageVersion = downloadData(URLs.VERSION + bookName + "/" + foreignLanguage);

            //persist these objects to database;
            if(nativeLanguageVersion.length()>0) {
                receiver.send(STATUS_FINISHED, bundle);
            }
        }
        catch (JSONException|IOException e){
            Log.d(TAG, "Error downloading the book");
            Log.d(TAG, e.toString());
        }
    }
}

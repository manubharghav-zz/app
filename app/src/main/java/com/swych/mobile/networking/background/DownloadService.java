package com.swych.mobile.networking.background;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.swych.mobile.MyApplication;
import com.swych.mobile.db.Book;
import com.swych.mobile.db.BookDao;
import com.swych.mobile.db.DaoSession;
import com.swych.mobile.networking.Details;
import com.swych.mobile.networking.DisplayBookObject;
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
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.AbstractDaoSession;

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

        DisplayBookObject displayBook = (DisplayBookObject) intent.getSerializableExtra("book");
        String bookName = displayBook.getTitle();
        String nativeLanguage = intent.getStringExtra("nativeLanguage");
        String foreignLanguage = intent.getStringExtra("foreignLanguage");



        Bundle bundle = new Bundle();
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        try{
            JSONObject srcLangBook = downloadData(URLs.VERSION+bookName+"/"+nativeLanguage);
            JSONObject tarLangBook = downloadData(URLs.VERSION + bookName + "/" + foreignLanguage);
            JSONObject mappings = downloadData(URLs.VERSION+bookName+"/" + nativeLanguage + "/" + foreignLanguage);
            //persist these objects to database;
            if(srcLangBook.length()>0) {
                persistToDb(displayBook, srcLangBook, tarLangBook, mappings);

//                BookDao bookDao = daoSession.getBookDao();
//                Book book = new Book();



                receiver.send(STATUS_FINISHED, bundle);
            }
        }
        catch (JSONException|IOException e){
            Log.d(TAG, "Error downloading the book");
            Log.d(TAG, e.toString());
        }
    }


    private boolean persistToDb(DisplayBookObject displayBook, JSONObject srcLanguageBook, JSONObject trgtLanguageBook, JSONObject mappings){

//        Initialize DAO' here
        DaoSession session = MyApplication.getNewSession();
        BookDao bookDao = session.getBookDao();

        Book book = new Book();
        book.setImageUrl(displayBook.getImageUrl());
        book.setTitle(displayBook.getTitle());
        book.setAuthor_id((long) 1);
        book.setAuthor_name(displayBook.getNativeVersion().getAuthor());
        long id = session.insert(book);

        return false;
    }
}

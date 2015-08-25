package com.swych.mobile.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.swych.mobile.R;
import com.swych.mobile.commons.utils.Language;
import com.swych.mobile.networking.DisplayBookObject;
import com.swych.mobile.networking.RequestManager;
import com.swych.mobile.networking.URLs;
import com.swych.mobile.networking.background.DownloadResultReceiver;
import com.swych.mobile.networking.background.DownloadService;
import com.swych.mobile.networking.background.DownloadType;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BookDetail extends ActionBarActivity  {
    DisplayBookObject bookObject;
    private static String TAG="BookDetail";
    private NetworkImageView bookImageView;
    private Spinner foreignLanguageSpinner;
    private Spinner nativeLanguageSpinner;
    private TextView titleView;
    private TextView descriptionView;
    private DownloadResultReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        bookObject =(DisplayBookObject) intent.getSerializableExtra(DisplayBookObject.class.getName());
        Log.d(TAG,"Book received Title: "+bookObject.getTitle());
        setContentView(R.layout.activity_book_detail);
        populateDetailView(bookObject);
    }

    public void populateDetailView(DisplayBookObject  book) {
        bookImageView = (NetworkImageView) findViewById(R.id.detail_image);
        titleView = (TextView) findViewById(R.id.title_detail);
        descriptionView = (TextView) findViewById(R.id.description_detail);
        nativeLanguageSpinner = (Spinner) findViewById(R.id.native_language_spinner);
        foreignLanguageSpinner = (Spinner) findViewById(R.id.foreign_language_spinner);
        bookImageView.setImageUrl(URLs.BASE + book.getImageUrl(), RequestManager.getInstance().getImageLoader());
        titleView.setText(book.getNativeVersion().getTitle());
        descriptionView.setText(book.getNativeVersion().getDescription());
        Language[] availableLanguages = book.getAvailableLanguages();
        ArrayAdapter<Language> languageAdapter = new ArrayAdapter<Language>(this,
                android.R.layout.simple_spinner_item, availableLanguages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nativeLanguageSpinner.setAdapter(languageAdapter);
        foreignLanguageSpinner.setAdapter(languageAdapter);


    }

    public void downloadBook(View v){
        Language nativeLanguage = (Language)nativeLanguageSpinner.getSelectedItem();
        Language foreignLanguage = (Language) foreignLanguageSpinner.getSelectedItem();


        if(nativeLanguage.getShortVersion().equals(foreignLanguage.getShortVersion())){
            Toast.makeText(getApplicationContext(),"Source and Target Languages are same", Toast.LENGTH_SHORT).show();
            // TODO get focus to the foreign Language spinner.
            foreignLanguageSpinner.setFocusableInTouchMode(true);
            foreignLanguageSpinner.requestFocus();
            return;
        }
        Toast.makeText(getApplicationContext(),"Source "+nativeLanguage +" Foreign " + foreignLanguage  , Toast.LENGTH_SHORT).show();
        //startBookDownload(bookObject, nativeLanguage, foreignLanguage);

    }



}

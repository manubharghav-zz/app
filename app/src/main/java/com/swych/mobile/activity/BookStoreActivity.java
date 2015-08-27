package com.swych.mobile.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.swych.mobile.R;
import com.swych.mobile.adapter.BookStoreAdapter;
import com.swych.mobile.commons.utils.Language;
import com.swych.mobile.networking.DisplayBookObject;
import com.swych.mobile.networking.RequestManager;
import com.swych.mobile.networking.background.DownloadResultReceiver;
import com.swych.mobile.networking.background.DownloadService;
import com.swych.mobile.networking.background.DownloadType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BookStoreActivity extends BaseActivity implements DownloadResultReceiver.Receiver {

    private static String TAG="BookStoreActivity";
    private GridView gridView;
    private BookStoreAdapter adapter;

    private ListView listView;
    private ArrayList<DisplayBookObject> bookList;
    private Map<String, Integer> readingOptions;

    private Spinner srcLanguageSpinner;
    private Language srcLanguageSelected;
    private Spinner swychLanguageSpinner;
    private Language swychLanguageSelected;

    private CheckBox chkMode1;
    private boolean isMode1checked=false;
    private CheckBox chkMode2;
    private boolean isMode2Checked=false;


    private boolean userSelectionDetected=false;

    private DownloadResultReceiver mReceiver;

    @Override
    public String getActivityName() {
        return getResources().getString(R.string.title_activity_book_store);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_store);

        getLayoutInflater().inflate(R.layout.activity_book_store, frameLayout);
        bookList = new ArrayList<DisplayBookObject>();
        readingOptions = new HashMap<>();
//        gridView = (GridView) findViewById(R.id.gridview);
        listView = (ListView) findViewById(R.id.book_store_books);

        RequestManager.getInstance().doRequest().getBooksForStore(bookList, readingOptions, listView);
        Log.d(TAG, "" + bookList.size());
        adapter = (BookStoreAdapter) listView.getAdapter();

        srcLanguageSpinner = (Spinner) findViewById(R.id.src_language_store);
        swychLanguageSpinner = (Spinner) findViewById(R.id.swych_language_store);

        chkMode1 = (CheckBox) findViewById(R.id.chkMode1);
        chkMode2 = (CheckBox) findViewById(R.id.chkMode2);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long arg3) {
                if (adapter == null) {
                    adapter = (BookStoreAdapter) listView.getAdapter();
                    Log.d(TAG, "adapter is null. attacing reference to the gridview's adapter");
                }
//                Toast.makeText(getApplicationContext(),"Downloading "+ adapter.getSluggifiedTitle(position), Toast.LENGTH_SHORT).show();

                if (userSelectionDetected) {
                    DisplayBookObject object = adapter.getItem(position);
                    startBookDownload(object,srcLanguageSelected,swychLanguageSelected);
                } else {
                    Toast.makeText(getApplicationContext(), "Please specify source and swych language to download a book", Toast.LENGTH_SHORT).show();
                }
            }
        });


        addListenerOnSrcSpinner();
        addListenerOnSwychSpinner();
        addListeneronMode1ChkBox();
        addListeneronMode2ChkBox();


    }


    // add listeners on spinners.
    public void addListenerOnSrcSpinner(){
        srcLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "selected in src Spinner: " + parent.getItemAtPosition(position));
                srcLanguageSelected = Language.valueOf(parent.getItemAtPosition(position).toString());

                filterBooks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing;
            }
        });

    }

    public void addListenerOnSwychSpinner(){
        swychLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "selected in swych Spinner: " + parent.getItemAtPosition(position));
                swychLanguageSelected = Language.valueOf(parent.getItemAtPosition(position).toString());
                filterBooks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    // add listeners on checkBoxes
    public void addListeneronMode1ChkBox(){
        chkMode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()){
                    isMode1checked=true;
                }
                else{
                    isMode1checked=false;
                }

                filterBooks();
            }
        });
    }

    public void addListeneronMode2ChkBox(){
        chkMode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()){
                    isMode2Checked=true;
                }
                else{
                    isMode2Checked=false;
                }

                filterBooks();
            }
        });
    }


    public ArrayList<DisplayBookObject> filterList(String srcLanguage, String targetLanguage, int modePref){
        Log.d(TAG,"reading options size: " + readingOptions.size());
        ArrayList<DisplayBookObject> filteredList = new ArrayList<>();
        String searchString=","+srcLanguage +","+targetLanguage;

        Log.d(TAG,"search String: "+ searchString+", mode pref =" +modePref);
        for(DisplayBookObject object:bookList){
            Integer availableModes= readingOptions.get(object.getTitle()+searchString);
            if(object.getTitle().contains("harry")){
                System.out.println(availableModes +" , " +object.getTitle());
            }
            if(availableModes!=null ){
                if(availableModes==3 || modePref==0 || (availableModes==modePref)) {
                    if(availableModes==3){
                        object.setMode2Present(true);
                        object.setMode1Present(true);
                    }
                    else if(availableModes==2){
                        object.setMode2Present(true);
                        object.setMode1Present(false);
                    }
                    else{
                        object.setMode1Present(true);
                        object.setMode2Present(false);
                    }
                    filteredList.add(object);
                }
            }
        }
        Log.d(TAG,"Size of filtered List: " + filteredList.size());
        return filteredList;
    }


    public void filterBooks(){
//        srcLanguageSelected = Language.valueOf(srcLanguageSpinner.getSelectedItem().toString());
//        swychLanguageSelected = Language.valueOf(swychLanguageSpinner.getSelectedItem().toString());
        int modePref=0;

        if(isMode1checked && isMode2Checked){
            modePref=3;
        }
        else if(isMode1checked){
            modePref=1;
        }
        else if (isMode2Checked){
            modePref=2;
        }


        if(srcLanguageSelected==null || swychLanguageSelected==null || srcLanguageSelected==swychLanguageSelected){
            return;
        }
        userSelectionDetected = true;

        ArrayList<DisplayBookObject> filteredList = filterList(srcLanguageSelected.getShortVersion(),swychLanguageSelected.getShortVersion(),modePref);
        listView.setAdapter(new BookStoreAdapter(getApplicationContext(), filteredList));
    }





    private void startBookDownload(DisplayBookObject book, Language nativeLanguage, Language foreignLanguage) {
        /* Starting Download Service */
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);

        intent.putExtra("book", book);
//        intent.putExtra("bookName", book.getTitle());
        intent.putExtra("nativeLanguage",srcLanguageSelected.getShortVersion());
        intent.putExtra("foreignLanguage", swychLanguageSelected.getShortVersion());
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("DownloadType", DownloadType.BOOK);
        intent.putExtra("isMode1Present", book.isMode1Present());
        intent.putExtra("isMode2Present", book.isMode2Present());
//        intent.putExtra("requestId", 101);

        startService(intent);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if(resultCode == DownloadService.STATUS_FINISHED){
            Log.d(TAG, "Book download completed");
            Toast.makeText(getApplicationContext(), "Book is downloaded. Visit library", Toast.LENGTH_SHORT).show();
        }
        else{
            //TODO Download failed. decide later
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
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
}

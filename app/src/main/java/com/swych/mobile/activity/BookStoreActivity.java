package com.swych.mobile.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;

import com.swych.mobile.R;
import com.swych.mobile.adapter.BookStoreAdapter;
import com.swych.mobile.commons.utils.Language;
import com.swych.mobile.networking.DisplayBookObject;
import com.swych.mobile.networking.RequestManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BookStoreActivity extends BaseActivity {

    private static String TAG="BookStoreActivity";
    private GridView gridView;
    private BookStoreAdapter adapter;

    private ListView listView;
    private ArrayList<DisplayBookObject> bookList;
    private Set<String> readingOptions;

    private Spinner srcLanguageSpinner;
    private Spinner swychLanguageSpinner;
    private Switch modeSwitch;

    @Override
    public String getActivityName() {
        return getResources().getString(R.string.title_activity_book_store);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_book_store);

        getLayoutInflater().inflate(R.layout.activity_book_store, frameLayout);
        bookList = new ArrayList<DisplayBookObject>();
        readingOptions = new HashSet<>();
//        gridView = (GridView) findViewById(R.id.gridview);
        listView = (ListView) findViewById(R.id.book_store_books);

        RequestManager.getInstance().doRequest().getBooksForStore(bookList,readingOptions, listView);
        Log.d(TAG, "" + bookList.size());
        adapter = (BookStoreAdapter) listView.getAdapter();

        srcLanguageSpinner = (Spinner) findViewById(R.id.src_language_store);
        swychLanguageSpinner = (Spinner) findViewById(R.id.swych_language_store);
        modeSwitch = (Switch) findViewById(R.id.mode_button);

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View v, int position,
//                                    long arg3) {
//                if(adapter==null){
//                    adapter = (BookStoreAdapter)gridView.getAdapter();
//                    Log.d(TAG, "adapter is null. attacing reference to the gridview's adapter");
//                }
////                Toast.makeText(getApplicationContext(),"Downloading "+ adapter.getSluggifiedTitle(position), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getApplicationContext(), BookDetail.class);
//                intent.putExtra(DisplayBookObject.class.getName(), adapter.getItem(position));
//                startActivity(intent);
//
//            }
//        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_store, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
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


    public ArrayList<DisplayBookObject> filterList(String srcLanguage, String targetLanguage, int modePref){
        Log.d(TAG,"reading options size: " + readingOptions.size());
        ArrayList<DisplayBookObject> filteredList = new ArrayList<>();
        String searchString;
        if(modePref==1){
            searchString = srcLanguage +","+targetLanguage+",1";
        }
        else if(modePref==2){
            searchString = srcLanguage +","+targetLanguage+",2";
        }
        else{
            searchString = srcLanguage +","+targetLanguage+","+"1,2";
        }

        Log.d(TAG,"search String: "+ searchString);
        for(DisplayBookObject object:bookList){
            if(readingOptions.contains(object.getTitle()+","+searchString)){
                filteredList.add(object);
            }
        }

        return filteredList;
    }


    public void filterBooks(View v){
        Language srcLanguage = Language.valueOf(srcLanguageSpinner.getSelectedItem().toString());
        Language swychLanguage = Language.valueOf(swychLanguageSpinner.getSelectedItem().toString());
        boolean modeSwitchStatus = modeSwitch.isChecked();
        int mode;
        if(modeSwitchStatus){
            mode =1;
        }
        else{
            mode= 2;
        }
        ArrayList<DisplayBookObject> filteredList = filterList(srcLanguage.getShortVersion(),swychLanguage.getShortVersion(),mode);
        listView.setAdapter(new BookStoreAdapter(getApplicationContext(),filteredList));
    }
}

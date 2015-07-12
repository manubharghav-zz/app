package com.swych.mobile.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.swych.mobile.R;
import com.swych.mobile.adapter.BookStoreAdapter;
import com.swych.mobile.networking.DisplayBookObject;
import com.swych.mobile.networking.RequestManager;
import com.swych.mobile.networking.background.DownloadResultReceiver;
import com.swych.mobile.networking.background.DownloadService;
import com.swych.mobile.networking.background.DownloadType;

public class BookStoreActivity extends BaseActivity {

    private static String TAG="BookStoreActivity";
    private GridView gridView;
    private BookStoreAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_book_store);

        getLayoutInflater().inflate(R.layout.activity_book_store, frameLayout);
        gridView = (GridView) findViewById(R.id.gridview);
        RequestManager.getInstance().doRequest().getBooksForStore(gridView);
        adapter = (BookStoreAdapter) gridView.getAdapter();




        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long arg3) {
                if(adapter==null){
                    adapter = (BookStoreAdapter)gridView.getAdapter();
                    Log.d(TAG, "adapter is null. attacing reference to the gridview's adapter");
                }
//                Toast.makeText(getApplicationContext(),"Downloading "+ adapter.getSluggifiedTitle(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), BookDetail.class);
                intent.putExtra(DisplayBookObject.class.getName(), adapter.getItem(position));
                startActivity(intent);

            }
        });
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
}

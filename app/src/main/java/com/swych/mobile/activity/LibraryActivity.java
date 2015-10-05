package com.swych.mobile.activity;

import com.swych.mobile.MyApplication;
import com.swych.mobile.activity.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.swych.mobile.R;
import com.swych.mobile.adapter.LibraryListAdapter;
import com.swych.mobile.db.DaoSession;
import com.swych.mobile.db.LibraryDao;
import com.swych.mobile.networking.background.DownloadResultReceiver;
import com.swych.mobile.networking.background.DownloadService;

import de.greenrobot.dao.query.QueryBuilder;


public class LibraryActivity extends BaseActivity {


    private static String TAG="LibraryActivity";
    private Cursor cursor;
    public static String libraryActivityId = "libraryItemId";
    private DaoSession session;
    private SQLiteDatabase db;
    private LibraryDao libraryDao;
    @Override
    public String getActivityName() {
        return getResources().getString(R.string.title_activity_library);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_library);
        getLayoutInflater().inflate(R.layout.activity_library,frameLayout);

        session = MyApplication.getSession();
        db = MyApplication.getDataBase();
        libraryDao = session.getLibraryDao();
        cursor = db.query(libraryDao.getTablename(),libraryDao.getAllColumns(),null,null,null,null,null );

        LibraryListAdapter listAdapter = new LibraryListAdapter(getApplicationContext(),cursor);

        final ListView listview = (ListView) findViewById(R.id.library_list);

        listview.setAdapter(listAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) listview.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ReaderActivity2.class);
                intent.putExtra(libraryActivityId, cursor.getLong(cursor.getColumnIndexOrThrow
                        ("_id")));
                startActivity(intent);
            }
        });

//        registerForContextMenu(listview);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean
                    checked) {
                mode.setTitle(listview.getCheckedItemCount()+" selected books");
                mode.invalidate();
                //todo if more than 2 disable the read now icon.
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                Log.d(TAG,"creating action mode");
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_library_contextual_actions,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                Log.d(TAG,"preparing action mode");
                if(listview.getCheckedItemCount()>1){
                    menu.findItem(R.id.readnow).setVisible(false);
                    return true;
                }
                else{
                    menu.findItem(R.id.readnow).setVisible(false);
                    return true;
                }


            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.sync:
                        Log.d(TAG,"Sync button clicked");
                        return true;
                    case R.id.delete:
                        Log.d(TAG,"Delete button clicked");
                        return true;
                    case R.id.readnow:
                        Log.d(TAG,"read now button clicked");
                        return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.d(TAG,"destroying action mode");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i(TAG, "item clicked, id = " + id);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if( id == R.id.action_bar_title){

        }
        else if(id == R.id.sync){
            Log.d(TAG, "Syncing now");


        }

        return super.onOptionsItemSelected(item);

    }


//    private void updateBooks() {
//        /* Starting Download Service */
//        mReceiver = new DownloadResultReceiver(new Handler());
//        mReceiver.setReceiver(this);
//        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);
//
//        intent.putExtra("book", book);
//        //        intent.putExtra("bookName", book.getTitle());
//        intent.putExtra("nativeLanguage",srcLanguageSelected.getShortVersion());
//        intent.putExtra("foreignLanguage", swychLanguageSelected.getShortVersion());
//        intent.putExtra("receiver", mReceiver);
//        intent.putExtra("DownloadType", DownloadType.BOOK);
//        intent.putExtra("isMode1Present", book.isMode1Present());
//        intent.putExtra("isMode2Present", book.isMode2Present());
//        //        intent.putExtra("requestId", 101);
//
//        startService(intent);
//    }


}


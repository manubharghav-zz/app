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
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.swych.mobile.R;
import com.swych.mobile.adapter.LibraryListAdapter;
import com.swych.mobile.db.DaoSession;
import com.swych.mobile.db.LibraryDao;

import de.greenrobot.dao.query.QueryBuilder;


public class LibraryActivity extends BaseActivity {


    private static String TAG="LibraryActivity";
    private Cursor cursor;
    public static String libraryActivityId = "libraryItemId";
    @Override
    public String getActivityName() {
        return getResources().getString(R.string.title_activity_library);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_library);
        getLayoutInflater().inflate(R.layout.activity_library,frameLayout);

        DaoSession session = MyApplication.getSession();
        SQLiteDatabase db = MyApplication.getDataBase();
        LibraryDao libraryDao = session.getLibraryDao();
        cursor = db.query(libraryDao.getTablename(),libraryDao.getAllColumns(),null,null,null,null,null );

        LibraryListAdapter listAdapter = new LibraryListAdapter(getApplicationContext(),cursor);

        final ListView listview = (ListView) findViewById(R.id.library_list);

        listview.setAdapter(listAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) listview.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(),ReaderActivity2.class);
                intent.putExtra(libraryActivityId, cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


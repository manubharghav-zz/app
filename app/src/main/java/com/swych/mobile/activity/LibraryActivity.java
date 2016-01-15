package com.swych.mobile.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.swych.mobile.MyApplication;
import com.swych.mobile.R;
import com.swych.mobile.adapter.LibraryListAdapter;
import com.swych.mobile.db.DaoSession;
import com.swych.mobile.db.LibraryDao;
import com.swych.mobile.networking.background.ActionType;
import com.swych.mobile.networking.background.DownloadResultReceiver;
import com.swych.mobile.networking.background.DownloadService;


public class LibraryActivity extends BaseActivity implements DownloadResultReceiver.Receiver {


    private static String TAG="LibraryActivity";
    private Cursor listviewCursor;
    private LibraryListAdapter libraryListAdapter;
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
        listviewCursor = db.query(libraryDao.getTablename(), libraryDao.getAllColumns(), null,
                null, null, null, null);

        libraryListAdapter = new LibraryListAdapter(getApplicationContext(),
                listviewCursor);

        final ListView listview = (ListView) findViewById(R.id.library_list);

        listview.setAdapter(libraryListAdapter);

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
        listview.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean
                    checked) {
                mode.setTitle(listview.getCheckedItemCount() + " selected books");
                mode.invalidate();
                final int checkedCount = listview.getCheckedItemCount();
                if(checkedCount> 1){
                    Log.d(TAG, "More than 2 items selected");

                }

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
                    menu.findItem(R.id.readnow).setVisible(true);
                    return true;
                }


            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Log.d(TAG, "processing itemId: " + item.getItemId());
                SparseBooleanArray selectedItems = listview.getCheckedItemPositions();
                long[] ids = new long[selectedItems.size()];
                for(int i=0;i<selectedItems.size();i++){
                    Cursor cursor = (Cursor) listview.getItemAtPosition(selectedItems.keyAt(i));
                    long libraryItemId = cursor.getLong(cursor.getColumnIndex("_id"));
                    ids[i] = libraryItemId;
                    Log.d(TAG,"processing library item Id: " + libraryItemId);

                }


                switch (item.getItemId()){
                    case R.id.sync:
                        Log.d(TAG,"Sync started");
                        break;
                    case R.id.delete:
                        Log.d(TAG, "Delete started");
                        deleteBook(ids);
                        break;
                    case R.id.readnow:
                        Log.d(TAG,"reading book");
                        break;
                }
                mode.finish();
                return true;
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



    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if(resultCode == DownloadService.STATUS_FINISHED){
            Log.d(TAG, "Book delete completed");
            Toast.makeText(getApplicationContext(), "Book delete completed", Toast
                    .LENGTH_SHORT).show();

            Log.d(TAG,"initial count " + listviewCursor.getCount());
            Cursor newlistviewCursor = db.query(libraryDao.getTablename(),libraryDao
                    .getAllColumns(),null,null,null,null,null );
            Log.d(TAG, "final count " + newlistviewCursor.getCount());


            libraryListAdapter.changeCursor(newlistviewCursor);
            libraryListAdapter.notifyDataSetChanged();
            listviewCursor = newlistviewCursor;

        }
        else if(resultCode==DownloadService.STATUS_DUPLICATE){
            Log.d(TAG, "Book already present in your library");
            Toast.makeText(getApplicationContext(), "This book exists in your library. Please visit library to read", Toast.LENGTH_SHORT).show();
        }
        else{
            //TODO Download failed. decide later
        }
    }



    private void deleteBook(long[] libraryItemIds) {
        /* Started deleting book. */
        DownloadResultReceiver mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);
        intent.putExtra("library_item_id",libraryItemIds);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("ActionType", ActionType.BOOK_DELETE);
        startService(intent);
    }
 }


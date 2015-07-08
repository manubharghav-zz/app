package com.swych.mobile.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.AbstractDaoSession;

import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.net.ConnectException;

public class DbHelper {
    private static String TAG = "DBHelper";
    private static String DB_NAME="swych-db";
    private SQLiteDatabase db;
    private DaoSession doaSession;
    private DaoMaster doaMaster;
    private Context context;

    private DaoMaster getMaster() {
        if (db == null) {
            db = getDatabase(DB_NAME);
        }
        return new DaoMaster(db);
    }


    public DbHelper(Context context){
        this.context = context;
    }
    public DaoSession getSession(boolean newSession) {
        if (newSession) {
            return getMaster().newSession();
        }
        if (doaSession == null) {
            doaSession = getMaster().newSession();
        }
        return doaSession;
    }

    private synchronized SQLiteDatabase getDatabase(String name) {
        SQLiteDatabase database=null;

        try {
            SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context, name, null);
            database = helper.getWritableDatabase();


        } catch (Exception ex) {
            Log.e(TAG, "Error creating database "+ name);
        }

        return database;
    }


}

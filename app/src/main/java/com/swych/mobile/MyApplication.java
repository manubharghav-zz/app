package com.swych.mobile;

import android.app.Application;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import com.swych.mobile.commons.utils.Language;
import com.swych.mobile.db.DaoSession;
import com.swych.mobile.db.DbHelper;
import com.swych.mobile.networking.RequestManager;

import java.util.Locale;

/**
 * Created by manu on 6/15/15.
 */
public class MyApplication extends Application {

    public MyApplication() {
        super();
    }
    private Language localLanguage;
    private static DbHelper dbHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DbHelper(getApplicationContext());
        localLanguage = Language.valueOf(Resources.getSystem().getConfiguration().locale.getDisplayLanguage());
        RequestManager.getInstance(getApplicationContext());
    }

    public Language getLanguage(){
        if(localLanguage==null){
            localLanguage = Language.getLongVersion(Resources.getSystem().getConfiguration().locale.getDisplayLanguage());
        }
        return localLanguage;
    }


    public static SQLiteDatabase getDataBase() {return dbHelper.getDatabase();}
    public static DaoSession getSession(){
        return dbHelper.getSession(false);
    }
    public static DaoSession getNewSession(){
        return dbHelper.getSession(true);
    }
}

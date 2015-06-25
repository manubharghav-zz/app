package com.swych.mobile;

import android.app.Application;
import android.content.res.Resources;

import com.swych.mobile.commons.utils.Language;
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
    @Override
    public void onCreate() {
        super.onCreate();
        localLanguage = Language.getLongVersion(Resources.getSystem().getConfiguration().locale.getDisplayLanguage());
        
    }

    public Language getLanguage(){
        if(localLanguage==null){
            localLanguage = Language.getLongVersion(Resources.getSystem().getConfiguration().locale.getDisplayLanguage());
        }
        return localLanguage;
    }
}

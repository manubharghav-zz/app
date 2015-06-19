package com.swych.mobile.networking;

import android.content.Context;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by manu on 6/15/15.
 */
public class RequestManager {

    private static RequestManager instance;
    private RequestProxy mRequestProxy;

    private RequestManager(Context context) {
        mRequestProxy = new RequestProxy(context);
    }
    public RequestProxy doRequest() {
        return mRequestProxy;
    }

    public ImageLoader getImageLoader(){
        return mRequestProxy.getImageLoader();
    }

    // This method should be called first to do singleton initialization
    public static synchronized RequestManager getInstance(Context context) {
        if (instance == null) {
            instance = new RequestManager(context);
        }
        return instance;
    }

    public static synchronized RequestManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(RequestManager.class.getSimpleName() +
                    " is not initialized, call getInstance(..) method first.");
        }
        return instance;
    }
}

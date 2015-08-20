package com.swych.mobile.networking;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ResponseDelivery;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.swych.mobile.adapter.BookStoreAdapter;
import com.swych.mobile.commons.utils.NetworkingUtils;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static com.google.android.gms.internal.zzhl.runOnUiThread;

/**
 * Created by manu on 6/15/15.
 */
public class RequestProxy {

    private RequestQueue mRequestQueue;
    private static String username=Details.username;
    private static String password = Details.password;
    private ImageLoader.ImageCache imageCache;
    private Context context;
    private ImageLoader imageLoader;
    // package access constructor
    RequestProxy(Context context) {
//        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.context = context;
        imageCache = new BitmapLruCache();
        mRequestQueue = RequestProxy.newRequestQueue(context, null);
        imageLoader = new ImageLoader(mRequestQueue, imageCache);
    }

    public ImageLoader getImageLoader(){

        return imageLoader;

    }

    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        File cacheDir = new File(context.getCacheDir(), "def_cache_dir");
        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                stack = new HurlStack() {
                    @Override
                    public HttpResponse performRequest(Request<?> request, Map<String, String> headers)
                            throws IOException, AuthFailureError {
                        headers.putAll(NetworkingUtils.createBasicAuthHeader(username, password));
                        return super.performRequest(request, headers);
                    }
                };



            } else {
                stack = new HttpClientStack(AndroidHttpClient.newInstance("volley/0")) {
                    @Override
                    public HttpResponse performRequest(Request<?> request, Map<String, String> headers)
                            throws IOException, AuthFailureError {
                        headers.putAll(NetworkingUtils.createBasicAuthHeader(username, password));
                        return super.performRequest(request, headers);
                    }
                };


            }
        }
        Network network = new BasicNetwork(stack);
        // important part
        int threadPoolSize = 10; // number of network dispatcher threads to create
        // pass Executor to constructor of ResponseDelivery object
        ResponseDelivery delivery = new ExecutorDelivery(Executors.newFixedThreadPool(threadPoolSize));
        // pass ResponseDelivery object as a 4th parameter for RequestQueue constructor
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network, threadPoolSize, delivery);
        queue.start();

        return queue;
    }


    public void login() {
        // login request
    }

    public void getBooksForStore(final ArrayList<DisplayBookObject> bookList, final ListView listView) {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // not UI thread, do parsing

                try {
                    bookList.addAll(Deserializer.getBooksFromJsonResponse(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(new BookStoreAdapter(context, bookList));
                    }
                });
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                if (error.networkResponse != null) {
                    Log.d("Error Response code: ", String.valueOf(error.networkResponse.statusCode));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO handle error response code. Need to figure out.
                    }
                });
            }
        };

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "http://www.swych.co/api/library",
                listener,
                errorListener);
        mRequestQueue.add(request);

    }
}


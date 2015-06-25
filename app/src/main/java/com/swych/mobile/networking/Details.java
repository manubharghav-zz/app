package com.swych.mobile.networking;

import android.util.Base64;

/**
 * Created by manu on 6/21/15.
 */
public class Details{

    public static String username = "sandeep";
    public static String password = "junekiMIXER+123";
    public static String userpwd = username+":"+password;

    public static String getBasicAuth(){
        return "Basic " + Base64.encodeToString(userpwd.getBytes(), Base64.NO_WRAP);
    }
}

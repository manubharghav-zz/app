package com.swych.mobile.utils;

/**
 * Created by manu on 6/10/15.
 */
public class Utils {


    public static boolean isPasswordValid(String password){
        boolean isValid = true;

        if(password.length()< 7 || password.equals(password.toLowerCase())){
            return false;
        }

        return true;
    }

    public static boolean isEmailValid(String email){
        return email.contains("@");
    }
}

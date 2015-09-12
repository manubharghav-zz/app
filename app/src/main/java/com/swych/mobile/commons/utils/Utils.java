package com.swych.mobile.commons.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

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


    public static long[] parseLongs(String s){
        // format of string: a1,a2,a3;
        String[] splits = s.split(",");
        long[] nums = new long[splits.length];
        for(int i=0;i<splits.length;i++){
            nums[i] = Long.parseLong(splits[i]);
        }

        return nums;
    }


    public static ImageView createFlagImageView(Context mContext){
        ImageView imageView = new ImageView(mContext);
        LinearLayout.LayoutParams srclayoutParams = new LinearLayout.LayoutParams(48, 48);
        imageView.setLayoutParams(srclayoutParams);
//        imageView.setPadding(15,15,15,15);

        return imageView;

    }
}

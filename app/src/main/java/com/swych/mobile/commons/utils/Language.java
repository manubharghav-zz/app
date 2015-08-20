package com.swych.mobile.commons.utils;

import com.swych.mobile.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Language {
    English("en", R.drawable.english), Hindi("hi",R.drawable.hindi), Spanish("es",R.drawable.spanish), German("de",R.drawable.german), Italian("it",R.drawable.italian), Portuguese("pt",R.drawable.portuguese), Mandarin("zh",R.drawable.mandarin), French("fr",R.drawable.french)   ;
    private String value;
    private int imageCode;
    private static final Map<String, Language> lookup = new HashMap<String, Language>();



    static {
        for (Language s : EnumSet.allOf(Language.class))
            lookup.put(s.getShortVersion(), s);
    }

    private Language(String value, int code) {
        this.value = value;this.imageCode=code;
    }

    public String getShortVersion(){
        return value;
    }
    public int getImageCode(){return imageCode;}

    public static Language getLongVersion(String shortVersion){
        return lookup.get(shortVersion);
    }
};   

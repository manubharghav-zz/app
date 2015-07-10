package com.swych.mobile.commons.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Language {
    English("en"), Hindi("hi"), Spanish("es"), German("de"), Italian("it"), Portuguese("pt"), Mandarin("zh"), French("fr")   ;
    private String value;

    private static final Map<String, Language> lookup = new HashMap<String, Language>();



    static {
        for (Language s : EnumSet.allOf(Language.class))
            lookup.put(s.getShortVersion(), s);
    }

    private Language(String value) {
        this.value = value;
    }

    public String getShortVersion(){
        return value;
    }

    public static Language getLongVersion(String shortVersion){
        return lookup.get(shortVersion);
    }
};   

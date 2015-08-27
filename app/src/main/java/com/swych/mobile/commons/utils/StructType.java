package com.swych.mobile.commons.utils;

/**
 * Created by Manu on 8/26/2015.
 */
public enum StructType {
    sentence(1),paragraph(2), chapter(3), book(4), volume(5);

    private int code;

    private StructType(int code) {
        this.code=code;
    }

    public int getCode(){
        return code;
    }

}

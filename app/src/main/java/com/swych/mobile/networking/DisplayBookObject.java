package com.swych.mobile.networking;

import com.swych.mobile.db.Version;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manu on 6/17/15.
 */
public class DisplayBookObject {

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    private List<Version> versions = new ArrayList<>(2);

    public void addNativeVersion(Version nativeVersion){
        this.versions.add(0, nativeVersion);
    }

    public Version getNativeVersion(){
        return this.versions.get(0);
    }
    public void addForeignVersion(Version foreignVersion){
        this.versions.add(0, foreignVersion);
    }

    public Version getForeignVersion() {
        return this.versions.get(1);
    }

}

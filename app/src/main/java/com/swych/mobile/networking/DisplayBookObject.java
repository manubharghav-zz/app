package com.swych.mobile.networking;



import com.swych.mobile.commons.utils.Language;


import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by manu on 6/17/15.
 */
public class DisplayBookObject implements Serializable{

    //TODO change the language set to use ENUM Language.


    public class Version implements Serializable{

        String author;
        String description;
        Language language;
        String title;

        public Version(){

        }

        public String getAuthor() {
            return author;
        }

        public Version setAuthor(String author) {
            this.author = author;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public Version setDescription(String description) {
            this.description = description;
            return this;
        }


        public Language getLanguage() {
            return language;
        }

        public Version setLanguage(String language) {
            languages.add(Language.getLongVersion(language));
            this.language = Language.getLongVersion(language);
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Version setTitle(String title) {
            this.title = title;
            return this;
        }

        public boolean addToBook(){
            if(language!=null){
                versions.add(this);
                return true;
            }
            return false;
        }



    }


    private String imageUrl;
    private String title;
    private List<Version> versions;
    private Set<Language> languages;

    public Language[] getAvailableLanguages() {
        return languages.toArray(new Language[0]);
    }



    public Version addVersion(){
        Version version = new Version();
        return version;
    }
    public DisplayBookObject(){
        versions = new ArrayList<>();
        languages = new HashSet<Language>();
    }



    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Version getNativeVersion(){
        return this.versions.get(0);
    }

    public Version getForeignVersion() {
        return this.versions.get(1);
    }

}


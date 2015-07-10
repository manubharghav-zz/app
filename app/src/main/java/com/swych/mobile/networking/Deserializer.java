package com.swych.mobile.networking;


import android.util.Log;

import com.swych.mobile.db.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by manu on 6/16/15.
 */
public class Deserializer {

    public static String TAG = "Deserializer";
    public static List<DisplayBookObject> getBooksFromJsonResponse(JSONObject response) throws JSONException {
        List<DisplayBookObject> books = new ArrayList<DisplayBookObject>();
        Iterator<String> bookIterator = response.keys();
        while(bookIterator.hasNext()){
            String bookName = bookIterator.next();

            JSONObject bookJson = new JSONObject(response.get(bookName).toString());
            String imageUrl = bookJson.get("image").toString();
            DisplayBookObject book = new DisplayBookObject();
            book.setImageUrl(imageUrl);
            book.setTitle(bookName);
            JSONArray versions = new JSONArray(bookJson.get("books").toString());
            for(int i=0;i<versions.length();i++){
                JSONObject version = versions.getJSONObject(i);
                boolean versionAdded = book.addVersion().setLanguage(version.getString("language")).setTitle(version.getString("title")).setDescription(version.getString("description")).setAuthor(version.getString("author")).addToBook();
                if(!versionAdded){
                    Log.d(TAG, "Error adding version " + version.getString("language"));
                }
            }
            books.add(book);
        }

        return books;
    }
}

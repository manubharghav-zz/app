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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by manu on 6/16/15.
 */
public class Deserializer {

    public static String TAG = "Deserializer";
    public static List<DisplayBookObject> getBooksFromJsonResponse(JSONObject response) throws JSONException {
        List<DisplayBookObject> books = new ArrayList<DisplayBookObject>();
        JSONObject library = response.getJSONObject("library");
        Iterator<String> bookIterator = library.keys();
        while(bookIterator.hasNext()){
            String bookName = bookIterator.next();
//            Log.d(TAG,"deserialising book: " + bookName);
            JSONObject bookJson = library.getJSONObject(bookName);
            String imageUrl = bookJson.get("image").toString();
            DisplayBookObject book = new DisplayBookObject();
            book.setImageUrl(imageUrl);
            book.setTitle(bookName);
            JSONArray versions = new JSONArray(bookJson.get("versions").toString());
            for(int i=0;i<versions.length();i++){
                JSONObject version = versions.getJSONObject(i);
                boolean versionAdded = book.addVersion().setLanguage(version.getString("lang")).setTitle(version.getString("title")).setAuthor(version.getString("author")).addToBook();
                if(!versionAdded){
                    Log.d(TAG, "Error adding version " + version.getString("lang"));
                }
            }
            books.add(book);
        }

        return books;
    }



    public static Map<String, Integer> getReadingOptionsFromJsonResponse(JSONObject response) throws JSONException{
        Map<String, Integer> map = new HashMap<>();

        JSONArray readingOptions = response.getJSONArray("reading_options");
        int mode;
        for(int i=0;i<readingOptions.length();i++){
            String[] splits = readingOptions.get(i).toString().split(",");

            mode = Integer.parseInt(splits[3]);
            String key = splits[0]+","+splits[1]+","+ splits[2];
            if(map.containsKey(key)) {
                map.put(key, 3);
            }
            else{
                map.put(key,mode);
            }
        }
        return map;
    }


    public static String parseMappings(String mapping) {
        StringBuffer buffer = new StringBuffer();
        mapping = mapping.replaceAll(",\\[\\],", "");
        mapping = mapping.replaceAll(",\\[\\]", "");
        mapping = mapping.replaceAll("\\[\\],", "");
        String[] splits = mapping.split("\\]\\],\\[\\[");
        String key, value;
        String[] keySplits, subSplits;
        for (String split : splits) {
            subSplits = split.split("\\],\\[");
            if (subSplits.length == 2) {
                key = subSplits[0].replaceAll("[\\[\\]]", "");
                value = subSplits[1].replaceAll("[\\[\\]]", "");

                keySplits = key.split(",");
                buffer.append(keySplits[0]).append(":").append(keySplits.length).append(",").append(value).append(";");
                buffer.append("-").append(keySplits[keySplits.length-1]).append(":").append(keySplits.length).append(",").append(value).append(";");
            }
        }
        return buffer.toString();
    }
}

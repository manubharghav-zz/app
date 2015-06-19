package com.swych.mobile.networking;


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


    public static List<DisplayBookObject> getBooksFromJsonresponse(JSONObject response) throws JSONException {
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
            JSONObject nativeVersionInJson = versions.getJSONObject(0);
            Version nativeVersion = new Version();
            nativeVersion.setTitle(nativeVersionInJson.getString("title"));
            nativeVersion.setDescription(nativeVersionInJson.getString("description"));
            nativeVersion.setLanguage(nativeVersionInJson.getString("language"));
            nativeVersion.setAuthor(nativeVersionInJson.getString("author"));

            book.addNativeVersion(nativeVersion);
            JSONObject foreignVersionInJson = versions.getJSONObject(1);
            Version foreignVersion = new Version();
            foreignVersion.setTitle(foreignVersionInJson.getString("title"));
            foreignVersion.setDescription(foreignVersionInJson.getString("description"));
            foreignVersion.setLanguage(foreignVersionInJson.getString("language"));
            foreignVersion.setAuthor(foreignVersionInJson.getString("author"));

            book.addForeignVersion(foreignVersion);


            books.add(book);
        }

        return books;
    }


    public static void main(String[] args) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new FileReader("/home/manu/Downloads/swych.json"));
        String jsonString = reader.readLine();
        System.out.println(jsonString);
        JSONObject object = new JSONObject(jsonString);
        getBooksFromJsonresponse(object);
    }
}

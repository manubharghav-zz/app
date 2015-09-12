package com.swych.mobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.android.volley.toolbox.NetworkImageView;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swych.mobile.R;
import com.swych.mobile.commons.utils.Language;
import com.swych.mobile.commons.utils.Utils;
import com.swych.mobile.networking.DisplayBookObject;
import com.swych.mobile.networking.RequestManager;
import com.swych.mobile.networking.URLs;

import java.util.List;

/**
 * Created by manu on 6/17/15.
 */
public class BookStoreAdapter extends BaseAdapter {
    private static String TAG = "BookStoreAdapter";
    private Context mContext;
    private List<DisplayBookObject> books;
    public BookStoreAdapter(Context c, List<DisplayBookObject> books) {
        mContext = c;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public DisplayBookObject getItem(int position) {
        return books.get(position);
    }

    @Override
    // dummy method wont do any work at all.
    public long getItemId(int position) {
        return position;
    }


    public String getSluggifiedTitle(int position){
        return books.get(position).getTitle();
    }






    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View bookListViewItem;
        TextView bookName;
        TextView bookAuthor;
        NetworkImageView imageView;
        LinearLayout availableLanguages;
        TextView Mode1;
        TextView Mode2;

        if (convertView == null) {
            bookListViewItem = new View(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            bookListViewItem = inflater.inflate(R.layout.bookstore_book_2, null);
        } else {
            bookListViewItem = (View) convertView;
        }

        DisplayBookObject book = books.get(position);

        bookName = (TextView) bookListViewItem.findViewById(R.id.book_title);
        imageView = (NetworkImageView)bookListViewItem.findViewById(R.id.image);

        imageView.setImageUrl(URLs.BASE + book.getImageUrl(), RequestManager.getInstance().getImageLoader());
        imageView.setDefaultImageResId(R.drawable.book);

        bookAuthor = (TextView) bookListViewItem.findViewById(R.id.book_author);
        Mode1 = (TextView) bookListViewItem.findViewById(R.id.Mode1);
        Mode2 = (TextView) bookListViewItem.findViewById(R.id.Mode2);
        availableLanguages = (LinearLayout) bookListViewItem.findViewById(R.id.available_languages);

        bookName.setText(book.getNativeVersion().getTitle());

        bookAuthor.setText(book.getNativeVersion().getAuthor());
        availableLanguages.removeAllViews();
        for(Language language: book.getAvailableLanguages()){
            ImageView languageImageView = Utils.createFlagImageView(mContext);
            languageImageView.setImageResource(language.getImageCode());
            availableLanguages.addView(languageImageView);
        }

        if(!book.isMode1Present()){
            Mode1.setVisibility(View.INVISIBLE);
        }
        else{
            Mode1.setVisibility(View.VISIBLE);
        }
        if(!book.isMode2Present()){
            Mode2.setVisibility(View.INVISIBLE);
        }
        else{
            Mode2.setVisibility(View.VISIBLE);
        }


        Log.d(TAG,"position: " + position + " Book: " +books.get(position).getTitle());
        return bookListViewItem;
    }
}

package com.swych.mobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.android.volley.toolbox.NetworkImageView;

import android.widget.ImageView;
import android.widget.TextView;

import com.swych.mobile.R;
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
        View grid;
        TextView bookName;
        NetworkImageView imageView;

        if (convertView == null) {
            grid = new View(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.bookstore_book, null);


        } else {
            grid = (View) convertView;

        }
        bookName = (TextView) grid.findViewById(R.id.grid_text);
        imageView = (NetworkImageView)grid.findViewById(R.id.image);

        bookName.setText(books.get(position).getNativeVersion().getTitle());
//            authorTextView.setText(books.get(position).getNativeVersion().getAuthor());
        imageView.setImageUrl(URLs.BASE + books.get(position).getImageUrl(), RequestManager.getInstance().getImageLoader());
        imageView.setDefaultImageResId(R.drawable.);
        Log.d(TAG,"position: " + position + " Book: " +books.get(position).getTitle());


        return grid;
    }
}

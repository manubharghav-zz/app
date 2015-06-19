package com.swych.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.android.volley.toolbox.NetworkImageView;
import android.widget.TextView;

import com.swych.mobile.R;
import com.swych.mobile.networking.DisplayBookObject;
import com.swych.mobile.networking.RequestManager;

import java.util.List;

/**
 * Created by manu on 6/17/15.
 */
public class BookStoreAdapter extends BaseAdapter {
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
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.bookstore_book, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            NetworkImageView imageView = (NetworkImageView)grid.findViewById(R.id.image);
            System.out.println(books.get(position).getNativeVersion().getTitle());
            textView.setText(books.get(position).getNativeVersion().getTitle());
            imageView.setImageUrl("http://www.swych.co"+books.get(position).getImageUrl(), RequestManager.getInstance().getImageLoader());
            imageView.setDefaultImageResId(R.drawable.book);
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}

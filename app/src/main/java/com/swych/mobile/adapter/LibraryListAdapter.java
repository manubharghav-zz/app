package com.swych.mobile.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swych.mobile.R;
import com.swych.mobile.commons.utils.Language;

import org.w3c.dom.Text;

/**
 * Created by Manu on 7/14/2015.
 */


public class LibraryListAdapter extends CursorAdapter {
    private static String TAG = "LibraryListAdapter";
    private Context mContext;

    public LibraryListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.library_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleView = (TextView) view.findViewById(R.id.library_book_title);
        TextView srcLanguageView = (TextView) view.findViewById(R.id.library_src_language);
        TextView swychLanguageView = (TextView) view.findViewById(R.id.library_swych_language);
        ImageView imageView = (ImageView) view.findViewById(R.id.library_book_image);
        String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
        Language srcLanguage = Language.getLongVersion(cursor.getString(cursor.getColumnIndexOrThrow("SRC_LANGUAGE")));
        Language swychLanguage = Language.getLongVersion(cursor.getString(cursor.getColumnIndexOrThrow("SWYCH_LANGUAGE")));
        titleView.setText(title);
        srcLanguageView.setText(srcLanguage.toString());
        swychLanguageView.setText(swychLanguage.toString());
        //TODO write code to do the following. 1) download image onto device 2) save the path into db 3) load image into imageview with null pointer checks
//        imageView.setImageURI;
    }
}

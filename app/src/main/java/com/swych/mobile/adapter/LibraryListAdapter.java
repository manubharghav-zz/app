package com.swych.mobile.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.swych.mobile.MyApplication;
import com.swych.mobile.R;
import com.swych.mobile.commons.utils.Language;
import com.swych.mobile.commons.utils.Utils;
import com.swych.mobile.db.Library;
import com.swych.mobile.db.LibraryDao;
import com.swych.mobile.db.Version;
import com.swych.mobile.db.VersionDao;
import com.swych.mobile.networking.RequestManager;
import com.swych.mobile.networking.URLs;

import org.w3c.dom.Text;

/**
 * Created by Manu on 7/14/2015.
 */


public class LibraryListAdapter extends CursorAdapter {
    private static String TAG = "LibraryListAdapter";
    private Context mContext;
    public LibraryListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.mContext = context;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.library_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {



        TextView titleView = (TextView) view.findViewById(R.id.book_title);
        NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.image);
        TextView authorView =  (TextView) view.findViewById(R.id.book_author);
        LinearLayout availableLanguages = (LinearLayout) view.findViewById(R.id.available_languages);
        String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
        Language srcLanguage = Language.getLongVersion(cursor.getString(cursor.getColumnIndexOrThrow("SRC_LANGUAGE")));
        Language swychLanguage = Language.getLongVersion(cursor.getString(cursor.getColumnIndexOrThrow("SWYCH_LANGUAGE")));
        titleView.setText(title);
        TextView Mode1 = (TextView)view.findViewById(R.id.Mode1);
        TextView Mode2 = (TextView) view.findViewById(R.id.Mode2);

        authorView.setText(cursor.getString(cursor.getColumnIndexOrThrow("AUTHOR")));

        imageView.setImageUrl(URLs.BASE + cursor.getString(cursor.getColumnIndexOrThrow("IMAGEURL")), RequestManager.getInstance().getImageLoader());
        imageView.setDefaultImageResId(R.drawable.book);

        availableLanguages.removeAllViews();

        ImageView srcLanguageImageView = Utils.createFlagImageView(mContext);
        srcLanguageImageView.setImageResource(srcLanguage.getImageCode());
        availableLanguages.addView(srcLanguageImageView);

        ImageView swychLanguageImageView = Utils.createFlagImageView(mContext);
        swychLanguageImageView.setImageResource(swychLanguage.getImageCode());
        availableLanguages.addView(swychLanguageImageView);

        boolean isMode1Present = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("MODE1")));
        boolean isMode2Present = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("MODE2")));
        if(isMode2Present){
            Mode2.setVisibility(View.VISIBLE);
        }
        else{
            Mode2.setVisibility(View.INVISIBLE);
        }


        if(isMode1Present){
            Mode1.setVisibility(View.VISIBLE);
        }
        else{
            Mode1.setVisibility(View.INVISIBLE);
        }
        //TODO write code to do the following. 1) download image onto device 2) save the path into db 3) load image into imageview with null pointer checks
//        imageView.setImageURI;
    }
}

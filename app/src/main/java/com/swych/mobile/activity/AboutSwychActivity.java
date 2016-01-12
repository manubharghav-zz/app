package com.swych.mobile.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.SearchView;

import com.swych.mobile.R;

public class AboutSwychActivity extends BaseActivity {

    @Override
    public String getActivityName() {
        return "About";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        setContentView(R.layout.activity_library);
        getLayoutInflater().inflate(R.layout.activity_about_swych, frameLayout);

        WebView webview = (WebView)findViewById(R.id.swych_description);

        StringBuilder builder = new StringBuilder();
        builder.append("<html> <body>");
        builder.append("<h1>"+ getString(R.string.about_heading_title) +"</h1>");
        builder.append(getString(R.string.swych_description));
        builder.append("<h2>"+ getString(R.string.about_heading_title) +"</h2>");
        builder.append(getString(R.string.swych_features));
        builder.append("<h2>" + getString(R.string.about_heading_feedback)+"</h2>");
        builder.append(getString(R.string.collect_feedback));

        builder.append("</body></html>");
        webview.loadData(builder.toString(), "text/html", "utf-8");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_store, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

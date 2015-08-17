package com.swych.mobile.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.swych.mobile.R;

public class TmpActivity extends AppCompatActivity {


    String templateFirstPart = "<html>"+
            "<head> "+
            "<script src='jquery-2.1.3.min.js' type='text/javascript'></script>"+
            "</head>"+
            "<body>"+
            "<div id='page_content' style='visibility:hidden'>"+
            "<script>" +
            "$( document ).ready(function() {" +
            "var viewportHeight;\n" +
            "var viewportWidth;\n" +
            "if (document.compatMode === 'BackCompat') {\n" +
            "    viewportHeight = document.body.clientHeight;\n" +
            "    viewportWidth = document.body.clientWidth;\n" +
            "} else {\n" +
            "    viewportHeight = document.documentElement.clientHeight;\n" +
            "    viewportWidth = document.documentElement.clientWidth;\n" +
            "}\n" +
            "alert('dimensions  '+viewportHeight +'   ' + viewportWidth);});"+
            "</script>"+
            "<h1 > Harry Potter and Chamber of Secrets  </h1>";

    String templateSecondPart = "" +
            "</body></html>";

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp);

        webView = (WebView) findViewById(R.id.dummyReader);


        final class MyWebChromeClient extends WebChromeClient {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                if( message.startsWith("dimensions")){
                    System.out.println(message);
                    webView.loadUrl("javascript:$('#page_content').css('visibility', 'visible')");
                }
                result.confirm();
                return true;
            }
        }


        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);

        getSupportActionBar().show();
        webView.loadDataWithBaseURL("file:///android_asset/",templateFirstPart + templateSecondPart,"text/html","utf-8",null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tmp, menu);
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


    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}

package com.swych.mobile.activity;

import com.swych.mobile.MyApplication;
import com.swych.mobile.activity.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.swych.mobile.R;
import com.swych.mobile.db.DaoSession;
import com.swych.mobile.db.Library;
import com.swych.mobile.db.LibraryDao;
import com.swych.mobile.db.Sentence;
import com.swych.mobile.db.Structure;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e. status bar and
 * navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ReaderActivity extends Activity {

    private static String TAG="READER";
    /**
     * Whether or not the system UI should be auto-hidden after {@link #AUTO_HIDE_DELAY_MILLIS}
     * milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after user interaction
     * before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise, will show the
     * system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private long libraryItemId;
    private WebView webView;

    private Map<Long, Sentence> srcVersionSentences;
    private GestureDetectorCompat gDetect;
    private ListIterator<Structure> srcIterator;


//    String templateFirstPart =
//            "<html>" +
//            "<head>" +
//            "<script src='jquery-2.1.3.min.js' type='text/javascript'>"
//            +"</script> "+
//            "<style>" +
//            "body{margin:60 30 30 30;}" +
//            "p{font-family:'Georgia';text-align:justify;" +
//            "  font-size:20px;line-height:200%;" +
//            "  text-align-last:justify;}" +
//            "div#content p:last-child:after{content:\"\";" +
//            "  display:inline-block;width:100%;}" +
//            "h2{vertical-align:top;}" +
//            "div#content{word-wrap:break-word;}" +
//            "</style>" +
//            "<link rel='stylesheet' " +
//            "  href='jquery.mobile-1.4.5.min.css' />" +
//            "<script>" +
//            "$( document ).ready(function() {\n" +
//            "  alert('cont_height:'+document.getElementById('page_content').offsetHeight);\n" +
//            "});\n" +
//            "$(document).on('click', '.sentence_block', function() {\n" +
//            "  alert('sentence_clicked_id:'+$(this).attr('data-sentence_id'));\n" +
//            "});" +
//            "</script>"+
//            "</head>" +
//            "<body>" +
//            "<div id='content'>";
//    String templateSecondPart =
//            "</div>" +
//            "<script src='jquery-1.11.3.min' type='text/javascript'></script>" +
//            "<script src='jquery.mobile-1.4.5.min.js' type='text/javascript'></script>" +
//            "</body>" +
//            "</html>";

    String templateFirstPart = "<html>"+
            "<head> "+
            "<script src='jquery-2.1.3.min.js' type='text/javascript'></script>"+
            "</head>"+
            "<body>"+
            "<div id='page_content' style='visibility:hidden'>"+
            "<script>" +
            "$( document ).ready(function() {\n" +
            "  alert('cont_height:'+document.getElementById('page_content').offsetHeight);\n" +
            "});\n" +
            "$(document).on('click', '.sentence_block', function() {\n" +
            "  alert('sentence_clicked_id:'+$(this).attr('data-sentence_id'));\n" +
            "});" +
            "</script>"+
            "<p span class='paragraph_block' align=\"justify\">";

    String templateSecondPart = "</p> " +
            "</div></body></html>";
    private Context context = this;
    private StringBuffer lineBuffer = null;

    private boolean readForward = true;
    private String bufferLineForward = null;
    private long bufferLineForwardSentenceId;
    private long bufferLinePreviousSentenceId;
    private String bufferLineBackward = null;

    private StringBuffer webViewBuffer = new StringBuffer();

    private int current_line_id = 0;
    private int current_word_id = 0;
    private int current_sentence_id = -1;

    private long pageLoadStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        libraryItemId = getIntent().getLongExtra(LibraryActivity.libraryActivityId,-1);
        libraryItemId=1;
        Log.i(TAG, "library item id: "+ libraryItemId);
        srcVersionSentences = new HashMap<Long, Sentence>();
        if(libraryItemId < 0){
            // Go back to library page
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // load layouts and activities


        setContentView(R.layout.activity_reader);
        View contentView = findViewById(R.id.fullscreen_content);
        webView = (WebView) contentView;
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        loadBookIntoMemory(libraryItemId);



        final class MyWebChromeClient extends WebChromeClient {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                System.out.println("ALERRTTTT " + message);
                if (message.startsWith("cont_height:")) {
                    String[] content = message.split(":");
                    int pageContentOffsetHeight = Integer.parseInt(content[1]);
                    if (pageContentOffsetHeight <= 562)
                    {
                        bufferLineForward = null;
                        populateWebView();
                    }
                    else
                    {
                        System.out.println("got till here");
                        long timeStart = System.currentTimeMillis();
                        webView.loadUrl("javascript:cutoff=' ';while(document.getElementById('page_content').offsetHeight > 562){ var s = $('#page_content span:last').text();var pos = s.lastIndexOf(' ');cutoff = s.substr(pos+1, s.length) + ' ' +cutoff; s = s.substr(0,pos); $('#page_content span:last').html(s); }alert('cutoff_string' +'###' + cutoff)");
                        long timetaken = (System.currentTimeMillis() -timeStart);
                        Log.i(TAG,"spent "+timetaken +" ms on the last sentence");

                    }
                }

                else if( message.startsWith("cutoff_string")){
                    bufferLineForward = message.split("###")[1];
                    webView.loadUrl("javascript:$('#page_content').css('visibility', 'visible')");
                    Log.i(TAG,"took " +(System.currentTimeMillis()- pageLoadStart) + " ms for loading the entire page");
                }
                else if(message.contains("sentence_clicked")){
                    int senID = Integer.parseInt(message.split(":")[1]);

//                    new AlertDialog.Builder(context)
//                            .setMessage(sourceMap.get(senID))
//                            .setTitle(Flines.get(sourceMap.get(senID)))
//                            .show();
                }
                result.confirm();
                return true;
            }
        }
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        pageLoadStart = System.currentTimeMillis();
        populateWebView();

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        gDetect = new GestureDetectorCompat(this, new GestureListener());
    }

    private void populateWebView() {
        long start = System.currentTimeMillis();
        String nextLine = getLine();
        webViewBuffer.append(nextLine);
        String htmlContent = templateFirstPart + webViewBuffer.toString()+ templateSecondPart;

        webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "utf-8", null);
        long end = System.currentTimeMillis() - start;

        Log.i(TAG,"took " + end + " ms in populating webview");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the system UI. This is to
     * prevent the jarring behavior of controls going away while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any previously scheduled
     * calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    private String getLine(){
        boolean read = true;

        if(readForward && bufferLineForward!=null){
            return "<span class='sentence_block' data-sentence_id='" + bufferLineForwardSentenceId + "'>" + bufferLineForward + "</span>";
        }
        else if(!readForward && bufferLineBackward!=null){
            return "<span class='sentence_block' data-sentence_id='" + bufferLinePreviousSentenceId + "'>" + bufferLineBackward+ "</span>";
        }

        StringBuffer buffer=new StringBuffer();
        Structure struct;
        while(read){
            struct = moveStructureIterator();
            long sentenceId;
            if((sentenceId = isSentence(struct.getContent()))>0){
                buffer.append("<span class='sentence_block' data-sentence_id='");
                buffer.append(sentenceId);
                buffer.append("'>");
                buffer.append(srcVersionSentences.get(sentenceId).getContent());
                buffer.append("</span>");
                read = false;
            }
            else if(isParagraph(struct.getContent())){
                buffer.append("</p><p align=\"justify\">").append("\n");
            }
            else if(isStructTag(struct.getContent())){
                buffer.append("<h2>Chapter ").append(struct.getContent().split("\\|\\|\\|")[1]);
                buffer.append("</h2>").append("\n");
            }

        }
        return buffer.toString();
    }

    private Structure moveStructureIterator(){
        //TODO remember to handle base cases. like start of book and end of book.
        Structure structure;
        if(readForward){

            structure = srcIterator.next();
        }
        else{
            structure = srcIterator.previous();
        }

        return structure;
    }

    private void loadBookIntoMemory(long libraryItemId){
        DaoSession session = MyApplication.getSession();
        LibraryDao libraryDao = session.getLibraryDao();
        Library libraryItem = libraryDao.loadDeep(libraryItemId);
        Log.i(TAG,libraryItem.getSrcVersionId() + "   " +libraryItem.getSwychVersionId());

        List<Sentence> srcSentenceList =  libraryItem.getSrcVersion().getSentences();
        StringBuffer buffer = new StringBuffer();
        long start = System.currentTimeMillis();
        for(Sentence sentence:srcSentenceList){
            srcVersionSentences.put(sentence.getSentence_id(), sentence);
        }
        List<Structure> structure = libraryItem.getSrcVersion().getStructure();
        srcIterator = structure.listIterator();
        /*Structure struct;
        Long sentenceId;
        while (srcIterator.hasNext()){
            struct = srcIterator.next();
            if((sentenceId = isSentence(struct.getContent()))>0){
                buffer.append(srcVersionSentences.get(sentenceId));
            }
            else if(isParagraph(struct.getContent())){
                buffer.append("</p><p align=\"justify\">").append("\n");
            }
            else if(isStructTag(struct.getContent())){
                buffer.append("<h2>Chapter ").append(struct.getContent().split("|||")[1]);
                buffer.append("</h2>").append("\n");
            }
        }*/



        webView.loadDataWithBaseURL("file:///android_asset/", templateFirstPart + buffer.toString() + templateSecondPart, "text/html", "utf-8", null);
        Log.i(TAG, "loaded sentences into memory in " + ((System.currentTimeMillis() - start) / 1000) +"ms for loading");


    }

    private Long isSentence(String s){

        long sentId;

        try{
            sentId = Long.parseLong(s);
            return sentId;
            // is an integer!
        } catch (NumberFormatException e) {

        }
        return (long)-1;
    }
    private boolean isStructTag(String s){
        if( s.contains("CHAPTER")){
            return true;
        }
        return false;
    }
    private boolean isParagraph(String s){
        if (s.contains("<P>")){
            return true;
        }
        return false;
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private float flingMin = 100;
        private float velocityMin = 100;
        @Override
        public boolean onDown(MotionEvent event) {
            System.out.println("Inside OnDown Method");
            //webView.onTouchEvent(event);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            System.out.println("Inside FLing detect method");
            //determine what happens on fling events
            //user will move forward through messages on fling up or left
            boolean forward = false;
            //user will move backward through messages on fling down or right
            boolean backward = false;

            //calculate the change in X position within the fling gesture
            float horizontalDiff = event2.getX() - event1.getX();
            //calculate the change in Y position within the fling gesture
            float verticalDiff = event2.getY() - event1.getY();


            float absHDiff = Math.abs(horizontalDiff);
            float absVDiff = Math.abs(verticalDiff);
            float absVelocityX = Math.abs(velocityX);
            float absVelocityY = Math.abs(velocityY);

            if(absHDiff>absVDiff && absHDiff>flingMin && absVelocityX>velocityMin){
                if(horizontalDiff>0) backward=true;
                else forward=true;
            }

            else if(absVDiff>flingMin && absVelocityY>velocityMin){
                if(verticalDiff>0) backward=true;
                else forward=true;
            }


            if(forward){
                System.out.println("Moving forward Through Messages");
                readForward = true;
                webViewBuffer = new StringBuffer();
                pageLoadStart  = System.currentTimeMillis();
                populateWebView();
            }
            //user is cycling backwards through pages
            else if(backward){
                System.out.println("Backward flings not yet supported. WIll do it shortly");
                readForward = false;
            }
            return true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e){
        super.dispatchTouchEvent(e);
        return this.gDetect.onTouchEvent(e);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gDetect.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}

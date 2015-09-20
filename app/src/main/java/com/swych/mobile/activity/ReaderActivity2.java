package com.swych.mobile.activity;



import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


import com.swych.mobile.MyApplication;
import com.swych.mobile.R;
import com.swych.mobile.commons.utils.Mode;
import com.swych.mobile.commons.utils.Utils;
import com.swych.mobile.db.DaoSession;
import com.swych.mobile.db.Library;
import com.swych.mobile.db.LibraryDao;
import com.swych.mobile.db.Mapping;
import com.swych.mobile.db.Sentence;
import com.swych.mobile.db.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ReaderActivity2 extends AppCompatActivity {

    private static String TAG = "ReaderActivity2";
    private GestureDetectorCompat gDetect;
    private boolean AUTO_HIDE=true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;


    // reader fields and controls.
    private static final String CHAPTER = "CHAPTER";
    private static final String PARAGRAPH_TAG = "<P>";

    private String previousContent = null;

    private boolean chapterEnd = false;

    private Mode mode;
    private boolean readForward = true;

    private long libraryItemId;
    private WebView webView;
    private Map<Long, Sentence> srcVersionSentences;
    private Map<Long,Sentence> destVersionSentences;
//    private Map<Long, Long> sentenceToStructureMap;
    private Map<Long, String> mappings;

//    private
    private ArrayList<Structure> structureList;


    private String currentPagePrefix="";
    private String currentPageSuffix="";
    private String previousPageSuffix="";
    private String nextPagePrefix="";



    private long firstSentenceId;
    private long lastSentenceId;
    private String firstSentenceNumString;
    private String lastSentenceNumString;

    private boolean isFirstSentenceMode2=false;
    private boolean isLastSentenceMode2=false;
    private int startOfPage;
    private int endOfPage;



    private StringBuffer webViewBuffer = new StringBuffer();

    private static String CLICK_EVENT="onClick";
    private static String RENDER_EVENT="render";
    private static String LOW_BUFFER="need_more";


    private static String SENTENCE_FORMAT= "<span class='sentence_block' data-sentence_id='%s'>%s</span>" ;
    private static String MODE2_FORMAT = "<span class='sentence_block mode2_block' data-sentence_id='%s'>%s</span>";
    private static String PARAGRAPH_FORMAT="</p>\n<p> ";
    private static String CHAPTER_FORMAT = "<span class='sentence_block heading' data-sentence_id='%s'>%s</span> \n";

    // js script.

    String templateFirstPartForward = "<html>"+
            "<head> "+
            "<script src='jquery-2.1.3.min.js' type='text/javascript'></script>"+
            "</head>"+
            "<style type=\"text/css\">\n" +
            ".sentence_block.heading {\n" +
            "    text-align: center;\n" +
            "    font-size: 25px;\n" +
            "}\n" +
            ".sentence_block{\n" +
            "font-size: 17px;\n" +
            "}\n" +
            ".sentence_block.mode2_block{\n" +
            "  font-size: 17px;\n" +
            "  font-style: italic;\n" +
            "  color: blue;\n" +
            "}"+
            "\n" +
            "</style>"+
            "<body>"+
            "<div id='page_content' align=\"justify\" style='visibility:hidden'>"+
            "<script>" +
            "$( document ).ready(function() {" +
            "cutoff=' ';\n" +
            "rem_sentece_id=' ';\n" +
            "var viewportHeight;\n" +
            "if (document.compatMode === 'BackCompat') {\n" +
            "    viewportHeight = document.body.clientHeight;\n" +
            "} else {\n" +
            "    viewportHeight = document.documentElement.clientHeight;\n" +
            "}" +
            "viewportHeight=viewportHeight-10; \n"+
            "    var lastSpan;\n" +
            "    var removeSentences = true;\n" +
            "    var isTranslation = false; \n" +
            "  if(document.getElementById('page_content').offsetHeight < viewportHeight){\n" +
            "    alert('need_more')\n" +
            "  }\n" +
            "  else{"+
            "    while(document.getElementById('page_content').offsetHeight > viewportHeight){\n" +
            "        lastSpan =  $('#page_content span.sentence_block:last');\n" +
            "        if(removeSentences){\n" +
            "        $('p').each(function(index, item) {\n" +
            "            if($.trim($(item).text()) === \"\") {\n" +
            "                $(item).remove(); // $(item).remove();\n" +
            "            }\n" +
            "        });\n" +
            "        lastSpan.detach();\n" +
            "        if(document.getElementById('page_content').offsetHeight > viewportHeight){\n" +
            "            continue;\n" +
            "        }\n" +
            "        removeSentences=false;\n" +
            "        if(lastSpan.hasClass(\"mode2_block\")){\n" +
            "            isTranslation=true;\n" +
            "        }"+
            "        rem_sentece_id = lastSpan.attr(\"data-sentence_id\");\n" +
            "        lastSpan.appendTo($('p:last'));\n" +
            "        }\n" +
            "        //remove maximum number of sentences;\n" +
            "\n" +
            "        \n" +
            "        var s = $('#page_content span.sentence_block:last').text();\n" +
            "        var pos = s.lastIndexOf(' ');\n" +
            "        cutoff = s.substr(pos+1, s.length) + ' ' +cutoff;\n" +
            "        s = s.substr(0,pos);\n" +
            "         $('#page_content span.sentence_block:last').html(s);\n" +
            "    }\n" +
            "\n" +
            "    alert('"+RENDER_EVENT+"' +'###' + cutoff +\"###\" + 'rem_sentece_id' + \"###\" + rem_sentece_id+\"###\"+'included_string'+\"### \"+ s +\"###translation###\" + isTranslation ); }" +
            "});\n" +
            "$(document).on('click', '.mode2_block', function() {\n" +
            "  alert('"+CLICK_EVENT+":'+$(this).attr('data-sentence_id'));\n" +
            "});" +
            "</script>"+
            "<p>";



    String templateFirstPartBackward = "<html>"+
            "<head> "+
            "<script src='jquery-2.1.3.min.js' type='text/javascript'></script>"+
            "</head>"+
            "<style type=\"text/css\">\n" +
            ".sentence_block.heading {\n" +
            "    text-align: center;\n" +
            "    font-size: 25px;\n" +
            "}\n" +
            ".sentence_block{\n" +
            "\tfont-size: 17px;\n" +
            "}\n" +
            ".sentence_block.mode2_block{\n" +
            "  font-size: 17px;\n" +
            "  font-style: italic;\n" +
            "  color: blue;\n" +
            "}" +
            "\n" +
            "</style>"+
            "<body>"+
            "<div id='page_content' align=\"justify\" style='visibility:hidden'>"+
            "<script>" +
            "$( document ).ready(function() {" +
            "var viewportHeight;\n" +
            "  if (document.compatMode === 'BackCompat') {\n" +
            "      viewportHeight = document.body.clientHeight;\n" +
            "  } else {\n" +
            "      viewportHeight = document.documentElement.clientHeight;\n" +
            "  }\n" +
            "viewportHeight=viewportHeight-10; \n"+
            "  cutoff=' ';\n" +
            "  rem_sentece_id='';\n" +
            "  var firstSpan;\n" +
            "  var removeSentences = true;\n" +
            "  var isTranslation = false; \n" +
            "  if(document.getElementById('page_content').offsetHeight < viewportHeight){\n" +
            "    alert('need_more')\n" +
            "  }\n" +
            "  else{"+
            "  while(document.getElementById('page_content').offsetHeight > viewportHeight){\n" +
            "      firstSpan =  $('#page_content span.sentence_block:first');\n" +
            "      if(removeSentences){\n" +
            "        $('p').each(function(index, item) {\n" +
            "            if($.trim($(item).text()) === \"\") {\n" +
            "                $(item).remove(); // $(item).remove();\n" +
            "            }\n" +
            "        });\n" +
            "\n" +
            "        firstSpan.detach();\n" +
            "        if(document.getElementById('page_content').offsetHeight > viewportHeight){\n" +
            "           continue;\n" +
            "        }\n" +
            "        removeSentences=false;\n" +
            "        if(firstSpan.hasClass(\"mode2_block\")){\n" +
            "            isTranslation=true;\n" +
            "        }"+
            "        rem_sentece_id = firstSpan.attr(\"data-sentence_id\");\n" +
            "        firstSpan.prependTo($('p:first'));}      \n" +
            "      var s = $('#page_content span.sentence_block:first').text();\n" +
            "      var pos = s.indexOf(' ');\n" +
            "\n" +
            "      if(pos>0){\n" +
            "           cutoff = cutoff + ' '+s.substr(0, pos);\n" +
            "           s = s.substr(pos+1,s.length);\n" +
            "      }\n" +
            "      else{\n" +
            "           cutoff = cutoff + ' '+s;\n" +
            "           s=\"\";\n" +
            "      }"+
            "      $('#page_content span.sentence_block:first').html(s);\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    alert('"+RENDER_EVENT+"' +'###' + cutoff +\"###\" + 'rem_sentece_id' + \"###\" + rem_sentece_id +\"###\"+'included_string'+\"### \"+ s +\"###translation###\" + isTranslation);}"+
            "});" +
            "$(document).on('click', '.mode2_block', function() {\n" +
            "  alert('"+CLICK_EVENT+":'+$(this).attr('data-sentence_id'));\n" +
            "});" +
            "</script>"+
            "<p>";

    String templateSecondPart = "</p> " +
            "</div></body></html>";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader2);

        // configure action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Library");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setIcon(R.drawable.library);


        // configure webview

        webView =(WebView) findViewById(R.id.reader);

        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(AUTO_HIDE){
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });



        // configuring gestures and immersive mode screen.

        gDetect = new GestureDetectorCompat(this,new GestureListener());
        hideSystemUI();
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                           //The system bars are visible.
                            actionBar.show();
                        } else {
                            // The system bars are NOT visible.
                            actionBar.hide();
                        }
                    }
                });



        // configure reader from here.

        libraryItemId = 1;
        //        libraryItemId = getIntent().getLongExtra(LibraryActivity.libraryActivityId,-1);
        Log.d(TAG,"started reading book: " + libraryItemId);
        if(libraryItemId<0){
            Log.d(TAG,"Error reading library item, id=" +libraryItemId);
            //TODO go back to library page.
        }

        loadBookIntoMemory(libraryItemId);

        // javascript for webview.
        final class MyWebChromeClient extends WebChromeClient {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                Log.d(TAG, message);
                if(message.startsWith(LOW_BUFFER)){
                    // todo get more line in the buffer.
                    if(!chapterEnd) {
                        populateWebView(false);
                    }
                    else {
                        if(readForward){
                            previousPageSuffix = currentPageSuffix;
                            nextPagePrefix="";
                            currentPageSuffix = "";

                        }
                        else{
                            chapterEnd=false;
                            nextPagePrefix = currentPagePrefix;
                            currentPageSuffix=previousPageSuffix;
                            previousPageSuffix="";
                            currentPagePrefix="";
                            lastSentenceId=firstSentenceId;
                        }
                        webView.loadUrl("javascript:$('#page_content').css('visibility', 'visible')");
                    }
                }
                else if( message.startsWith(RENDER_EVENT)){
                    String splits[] = message.split("###");

                    // update state;
                    if(readForward){
                        previousPageSuffix = currentPageSuffix;
                        currentPagePrefix = nextPagePrefix;

                        currentPageSuffix = splits[5];
                        nextPagePrefix = splits[1];
                        firstSentenceId = lastSentenceId;
                        isFirstSentenceMode2 = isLastSentenceMode2;
                        lastSentenceNumString = splits[3];
                        int[] numbers = getNumFromString(splits[3]);
                        lastSentenceId = numbers[numbers.length-1];
                        Log.d(TAG,"last sentence Id: " + lastSentenceId);
                        rewindIterator(false, (int) lastSentenceId);
                        isLastSentenceMode2 = Boolean.parseBoolean(splits[7]);
                        webView.loadUrl("javascript:$('#page_content').css('visibility', 'visible')");

                    }
                    else{
                        //going backward
                        nextPagePrefix = currentPagePrefix;
                        currentPageSuffix = previousPageSuffix;

                        currentPagePrefix = splits[5];
                        previousPageSuffix = splits[1];

                        firstSentenceNumString = splits[3];
                        int[] numbers = getNumFromString(splits[3]);
                        lastSentenceId=firstSentenceId;
                        isLastSentenceMode2=isFirstSentenceMode2;
                        firstSentenceId = numbers[0];
                        isFirstSentenceMode2 = Boolean.parseBoolean(splits[7]);
                        System.out.println("first sentence ID:  sout : " + startOfPage);
                        rewindIterator(true,(int) firstSentenceId);
                        webView.loadUrl("javascript:$('#page_content').css('visibility', 'visible')");
                    }


                }
                else if(message.startsWith(CLICK_EVENT)){

                    long[] sentenceIds = Utils.parseLongs(message.split(":")[1]);
                    String sentence = "";
                    try{
                    for(long id:sentenceIds){
                        sentence = sentence+" "+srcVersionSentences.get(id).getContent();
                    }}
                        catch(Exception e){
                            Log.d(TAG,"message " + message);
                            Log.d(TAG,"Clicked sentences: " + message.split(":")[1]);
                            e.printStackTrace();
                        }

                    Log.d(TAG,sentence);
                }
                result.confirm();
                Log.d(TAG, "start of Page: " + startOfPage + "  End of page: " + endOfPage);
                Log.d(TAG,"Previous Page Suffix : " + previousPageSuffix);
                Log.d(TAG,"Current Page Prefix : " + currentPagePrefix );
                Log.d(TAG,"Current Page Suffix: " + currentPageSuffix);
                Log.d(TAG, "Next Page Prefix: " + nextPagePrefix);
                Log.d(TAG,"Last Sentence Mode 2: " + isLastSentenceMode2);
                Log.d(TAG,"isFirstSentenceMode 2: " + isFirstSentenceMode2);
                Log.d(TAG, message);
                return true;
            }
        }
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        populateWebView(true);


    }


    // handler to relay events to the activity.
    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideSystemUI();
        }
    };

    // using the handler to post a method after a delay.
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reader_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.i(TAG, "item clicked, id = " + id);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if( id == R.id.action_bar_title){

        }
        else if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }



    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private float flingMin = 100;
        private float velocityMin = 100;
        @Override
        public boolean onDown(MotionEvent event) {
            //webView.onTouchEvent(event);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
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


            // not handling vertical motion.
//            else if(absVDiff>flingMin && absVelocityY>velocityMin){
//                if(verticalDiff>0) backward=true;
//                else forward=true;
//            }


            if(forward){
                if(endOfPage >= structureList.size()-1 ){
                    Log.d(TAG,"reached end of Book");
                    return true;
                }
                Log.d(TAG, "Moving forward.");
                readForward = true;
                startOfPage = endOfPage;
                firstSentenceId=lastSentenceId;

                currentPagePrefix="";
                previousPageSuffix="";
                webViewBuffer = new StringBuffer();
                chapterEnd=false;
                populateWebView(true);
            }
            //user is cycling backwards through pages
            else if(backward){
                Log.d(TAG, "Moving backward.");

                if(startOfPage<=0){
                    Log.d(TAG,"reached start of book");
                    return true;
                }

                readForward=false;
                endOfPage = startOfPage;

                // move start of page to the strucure element just before the firstSentenceId.

                lastSentenceId = firstSentenceId;

                currentPageSuffix="";
                nextPagePrefix="";

                webViewBuffer=new StringBuffer();
                chapterEnd=false;
                populateWebView(true);
            }
            return true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        super.dispatchTouchEvent(e);
        return this.gDetect.onTouchEvent(e);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return this.gDetect.onTouchEvent(event);
    }


    // This snippet hides the system bars.
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


    private void loadBookIntoMemory(long libraryItemId){
        DaoSession session = MyApplication.getSession();
        LibraryDao libraryDao = session.getLibraryDao();
        Library libraryItem = libraryDao.loadDeep(libraryItemId);
        Log.d(TAG,libraryItem.getSrcVersionId() + "   " +libraryItem.getSwychVersionId());
        Log.d(TAG,libraryItem.getTitle()+ "    " + libraryItem.getSrcVersion().getTitle() + "   " + libraryItem.getSrcVersion().getLanguage());
        srcVersionSentences = new HashMap<Long, Sentence>();
        List<Sentence> srcSentenceList =  libraryItem.getSrcVersion().getSentences();
        for(Sentence sentence:srcSentenceList){
            srcVersionSentences.put(sentence.getSentence_id(), sentence);
        }
        List<Structure> structure = libraryItem.getSrcVersion().getStructure();

        structureList = new ArrayList<Structure>();
        structureList.addAll(structure);


        // load destination version book into memory;
        destVersionSentences = new HashMap<>();
        List<Sentence> swychVersionSentenceList = libraryItem.getSwychVersion().getSentences();
        for (Sentence sentence : swychVersionSentenceList){
            destVersionSentences.put(sentence.getSentence_id(),sentence);
        }


        // load mappings into memory;
        mappings = new HashMap<>();
        List<Mapping> sentMappingsFromDB = libraryItem.getSentenceMappings();
        if(sentMappingsFromDB.size()==0){
            Log.i(TAG,"No mode 2 mappings for the book");
        }
        else{
            String mappingStr = sentMappingsFromDB.get(0).getStrMapping();
            StringTokenizer tokenizer = new StringTokenizer(mappingStr,";");
            String pair;
            String splits[];
            while(tokenizer.hasMoreTokens()){
                pair = tokenizer.nextToken();
                splits = pair.split(":");
                mappings.put(Long.parseLong(splits[0]),splits[1]);
            }
        }
        Log.d(TAG,"mappings loaded, num mappings = "+mappings.size());

        // testing book.
        mode = Mode.Mode2;

        //todo need to change this to last left position.
        startOfPage = 0;
        endOfPage = -1;
        Log.d(TAG, "completed loading sentences into memory");
    }




    private void rewindIterator(boolean start ,int sentenceNumber){

        if(start){
            Structure structure = structureList.get(startOfPage);
            if(structure.getSentenceId()==sentenceNumber){
                return;
            }
        }
        else{
            Structure structure = structureList.get(endOfPage);
            if(structure.getSentenceId()==sentenceNumber){
                return;
            }
        }

        while(true){
            Structure structure = moveStructureIterator(start,!readForward);
            if(structure==null ){
                //assuming it always moves backward.
                continue;
            }
            else if(structure.getSentenceId()==sentenceNumber){
                return;
            }
        }
    }

    private Structure moveStructureIterator(boolean start,boolean direction){
        //TODO remember to handle base cases. like start of book and end of book.
        Structure structure;
        if(endOfPage > structureList.size()-1 || startOfPage < 0){
            if(start){
                startOfPage=0;
            }
            else{
                endOfPage = structureList.size()-1;
            }
            return null;
        }
        if(start && direction){
            startOfPage++;
            structure = structureList.get(startOfPage);


        }
        else if(start && !direction){
            startOfPage--;
            if(startOfPage < 0){
                startOfPage=0;
                return null;
            }
            structure=structureList.get(startOfPage);


        }
        else if(!start && direction){
            endOfPage++;
            if(endOfPage >= structureList.size()){
                endOfPage = structureList.size()-1;
                return null;
            }
            structure=structureList.get(endOfPage);

        }
        else{
            endOfPage--;
            structure = structureList.get(endOfPage);

        }



        return structure;
    }

    private String getLines(int numLines, boolean newPage){
        Log.d(TAG,"in getlines: " + readForward);
        boolean read = true;
        StringBuffer buffer = new StringBuffer();

        Structure struct;

        if(newPage){
            if(readForward && nextPagePrefix.trim().length()>0){
                if(isLastSentenceMode2){
                    buffer.append(String.format(MODE2_FORMAT, lastSentenceNumString, nextPagePrefix));
                }
                else {
                    buffer.append(String.format(SENTENCE_FORMAT, lastSentenceId, nextPagePrefix));
                }
            }
            else if(!readForward && previousPageSuffix.trim().length()>0){
                if(isFirstSentenceMode2) {
                    buffer.append(String.format(MODE2_FORMAT, firstSentenceNumString, previousPageSuffix));
                }
                else{
                    buffer.append(String.format(SENTENCE_FORMAT, firstSentenceId, previousPageSuffix));
                }
            }
        }

        if(readForward) {
            while (numLines > 0) {
                // end of page.
                struct = moveStructureIterator(false,readForward);
                long sentenceId = struct.getSentenceId();
                if(struct==null){
                    Log.d(TAG,"reached end of book");
                    break;
                }
                if(struct.getType()==1){

                    if(mode==Mode.Mode2){
                        numLines--;
                        if(mappings.containsKey(sentenceId)){
                            String mappingString = mappings.get(sentenceId);
                            long[] mappingInfo = Utils.parseLongs(mappingString);
                            String sentenceIds = sentenceId+"";

                            for(int i=0;i<mappingInfo[0]-1;i++){
                                Structure tmp = moveStructureIterator(false,readForward);
                                if(tmp.getType()!=1){
                                    Log.e(TAG,"error in mapping format. Please verify mappings for this book");
                                }
                                numLines--;
                                sentenceIds = sentenceIds + "," +tmp.getSentenceId();
                            }
                            String sentence="";

                            for(int i=1;i<mappingInfo.length ;i++){
                                if(destVersionSentences.containsKey(mappingInfo[i])) {
                                    sentence = sentence + destVersionSentences.get(mappingInfo[i]).getContent();
                                }
                                else{
                                    Log.e(TAG,"couldnt not find sentence in destVersion " + mappingInfo[i]);
                                }
                            }

                            buffer.append(String.format(MODE2_FORMAT, sentenceIds, sentence));

                        }
                    }
                    else {
                        buffer.append(String.format(SENTENCE_FORMAT, sentenceId, srcVersionSentences.get(sentenceId).getContent()));
                        numLines--;
                    }

                }
                else if(struct.getType()==2){

                    buffer.append(PARAGRAPH_FORMAT);
                }
                else if(struct.getType()>2){
                    if(buffer.length()>0) {
                        chapterEnd=true;
                        moveStructureIterator(false,!readForward);
                        numLines=0;
                    }
                    else {
                        Log.d(TAG, struct.getSentenceId() + "   " + struct.getType());
                        buffer.append(String.format(CHAPTER_FORMAT,sentenceId, srcVersionSentences.get(sentenceId).getContent()));
                        chapterEnd=false;
                    }
                }

            }
        }
        else{
            while (numLines > 0) {
                struct = moveStructureIterator(true,readForward);

                if(struct==null){
                    Log.d(TAG,"reached start of book");
                    break;
                }
                long sentenceId = struct.getSentenceId();
                if(struct.getType()==1){
//                    Log.d(TAG,"adding sentence Id:  " + sentenceId);

                    if(mode == Mode.Mode2){
                        numLines--;
                        if(mappings.containsKey(sentenceId * -1)){
                            String mappingString = mappings.get(sentenceId * -1);
                            long[] mappingInfo = Utils.parseLongs(mappingString);
                            String sentenceIds = ""+sentenceId;

                            for(int i=0;i<mappingInfo[0]-1;i++){
                                Structure tmp = moveStructureIterator(false,readForward);
                                if(tmp.getType()!=1){
                                    Log.e(TAG,"error in mapping format. Please verify mappings for this book");
                                }
                                numLines--;
                                sentenceIds = tmp.getSentenceId()+ ","+sentenceIds;
                            }
                            String sentence="";

                            for(int i=1;i<mappingInfo.length ;i++){
                                if(destVersionSentences.containsKey(mappingInfo[i])) {
                                    sentence = sentence + destVersionSentences.get(mappingInfo[i]).getContent();
                                }
                                else{
                                    Log.e(TAG,"couldnt not find sentence in destVersion " + mappingInfo[i]);
                                }
                            }

                            buffer.insert(0, String.format(MODE2_FORMAT, sentenceIds, sentence));

                        }
                    }
                    else{
                        buffer.insert(0, String.format(SENTENCE_FORMAT, sentenceId, srcVersionSentences.get(sentenceId).getContent()));
                        numLines--;
                    }

                }
                else if(struct.getType()==2){
                    buffer.insert(0, PARAGRAPH_FORMAT);
                }
                else if(struct.getType()>2){
                    Log.d(TAG, struct.getType() + "   " + struct.getSentenceId());
                    buffer.insert(0, String.format(CHAPTER_FORMAT, sentenceId, srcVersionSentences.get(sentenceId).getContent()));
                        //moveStructureIterator(true,!readForward);
                        chapterEnd=true;
                        numLines=0;
                }

            }

        }

        return buffer.toString();
    }


//    private String getLinesForNewPage(int numLines){
//        boolean read = true;
//        StringBuffer buffer=new StringBuffer();
//        String lines = getLines(numLines);
//        if(readForward){
//            buffer.append(String.format(SENTENCE_FORMAT,lastSentenceId,nextPagePrefix));
//            buffer.append(lines);
//        }
//        else{
//            buffer.append(String.format(SENTENCE_FORMAT,firstSentenceId,previousPageSuffix));
//            buffer.insert(0,lines+" ");
//        }
//
//        return buffer.toString();
//    }

    private void populateWebView(boolean newPage) {
        String nextLine = getLines(15, newPage);

        if(readForward) {
            webViewBuffer.append(nextLine);
        }
        else{
            webViewBuffer.insert(0,nextLine);
        }
        Log.d(TAG,webViewBuffer.toString());
        String htmlContent;
        if(readForward) {
            htmlContent = templateFirstPartForward + webViewBuffer.toString() + templateSecondPart;
        }
        else{
            htmlContent = templateFirstPartBackward+webViewBuffer.toString()+templateSecondPart;
        }

        webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "utf-8", null);
    }


    private static int[] getNumFromString(String s){
        String[] splits = s.trim().split(",");
        int[] numbers = new int[splits.length];

        for(int i=0;i<splits.length;i++){
            numbers[i] = Integer.parseInt(splits[i]);
        }

        return numbers;
    }


}

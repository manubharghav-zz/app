package com.swych.mobile.activity;



import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;


import com.swych.mobile.R;

public class ReaderActivity2 extends AppCompatActivity {

    private static String TAG = "ReaderActivity2";
    private GestureDetectorCompat gDetect;
    private boolean AUTO_HIDE=true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static String Text = "After the death of Charles, who had never thought of designating a \n" +
            "successor, two claimants for the throne came forward; his sister \n" +
            "Ulrika and his nephew Carl Frederick. By intrigue with the nobles the \n" +
            "former secured the prize, promising to give up the almost absolute \n" +
            "power that had been wielded by the Vasa line. Two years later Ulrika \n" +
            "resigned the sovereignty to her husband, Prince Frederick of Hesse. \n" +
            "One of the most distressing chapters of Swedish history now begins. \n" +
            "Frederick I. was indolent and indifferent to the claims of his \n" +
            "position. When an energetic policy might at least have protected the \n" +
            "country, he looked on in apathy while party strife within and greed \n" +
            "of conquest from without nearly sundered the kingdom. Russia obtained \n" +
            "Ingermanland, Esthonia, Livonia, and part of Finland, and in effect \n" +
            "controlled the territory which it spared. After thirty years of such \n" +
            "virtual interregnum the throne was again mounted by an alien prince, \n" +
            "Adolf Frederick of Holstein. This was going from bad to worse. The new \n" +
            "Frederick was weaker, if not more indolent than his predecessor, and \n" +
            "in the twenty years of his authority the nation reached the bottom \n" +
            "of its helplessness and insignificance. In 1771, Gustaf III., son of \n" +
            "Adolf Frederick, was crowned. None of the father’s qualities appear in \n" +
            "this son. Born on Swedish soil, though of alien blood, he had early \n" +
            "imbibed the spirit of the Vasa monarchs, and set out to rival their \n" +
            "achievements. He at once overthrew the power of the council and assumed \n" +
            "again the reins of irresponsible authority. He became involved in a war \n" +
            "with Russia, then ruled by Catherine II., who effected an alliance with \n" +
            "Denmark against him. By the influence of Prussia and England Danish \n" +
            "co-operation with Russia was abandoned. After a few skirmishes Gustaf \n" +
            "was induced to close the campaign without accomplishing the results \n" +
            "attempted. It was clear the odds were too great. \n" +
            "\n" +
            "Sweden, now that all foreign differences were adjusted, was in \n" +
            "condition to enter upon a long period of prosperity. But to the \n" +
            "restless temper of the king, peace was impossible. He was ever \n" +
            "entertaining great schemes, and laid plans even for interfering with \n" +
            "the course of events in France, hoping to set aside the course of the \n" +
            "revolution and set up again the authority of the Bourbon family. Money \n" +
            "was solicited from the Diet for this purpose. The wildness of such \n" +
            "a project when the country was groaning under an accumulated burden \n" +
            "of debt, caused a strong revulsion of feeling against the king. A \n" +
            "conspiracy was formed to remove him, and on the 16th of March, 1792, \n" +
            "while attending a masked ball in Stockholm, he was assassinated.";

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

        WebView webView =(WebView) findViewById(R.id.reader);
        String template = "<html><body style=\"text-align:justify\"> %s </body></Html>";
        webView.loadData(String.format(template, Text), "text/html", "utf-8");

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



        // configuring gestures.

        gDetect = new GestureDetectorCompat(this,new GestureListener());
        delayedHide(5000);
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
                Log.d(TAG, "Moving forward.");
            }
            //user is cycling backwards through pages
            else if(backward){
                Log.d(TAG, "Moving backward.");
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
}

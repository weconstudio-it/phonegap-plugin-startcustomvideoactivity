package com.plugin.phonegap;

import com.plugin.phonegap.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;
import android.os.AsyncTask;
import android.content.res.Resources;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
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

    private VideoView vv;
    private Uri porcoizzio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String package_name = getApplication().getPackageName();
        Resources resources = getApplication().getResources();
        setContentView(resources.getIdentifier("activity_fullscreen", "layout", package_name));

        
        final View controlsView = findViewById(resources.getIdentifier("fullscreen_content_controls", "id", package_name));
        final View contentView = findViewById(resources.getIdentifier("fullscreen_content", "id", package_name));
        
        //setContentView(R.layout.activity_fullscreen);

        //final View controlsView = findViewById(R.id.fullscreen_content_controls);
        //final View contentView = findViewById(R.id.fullscreen_content);



        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.



        try {
            Uri cristelli  = (Uri)new Connection().execute().get();

            
            vv = (VideoView) findViewById(resources.getIdentifier("videoView", "id", package_name));
            //vv = (VideoView) findViewById(R.id.videoView);
            vv.setVideoURI(cristelli); //Run this on UiThread


            //vv.setVideoURI(Uri.parse("http://codww-vh.akamaihd.net/i/rsi/unrestricted/2014/04/01/436191.mp4/master.m3u8"));
            vv.start();
        }
        catch(Exception ex) {
            System.out.println (ex.toString());
        }



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);

        
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);


    }

    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {


            try {
                URL url = new URL("http://codww-vh.akamaihd.net/i/rsi/unrestricted/2014/04/01/436191.mp4/master.m3u8");
                InputStream M3U8 = (InputStream) url.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(M3U8));
                for (int i = 0; i < 2; ++i)
                    br.readLine();
                String target = br.readLine(); //this parses the third line of the playlist
                br.close();

                porcoizzio = Uri.parse(url.toString());
                //url = new URL(baseURL.concat(target));
//if the m3u8 url is relative, you have to concat it with the path
//Note: You have to do all this in a thread, you can't do network on UiThread



                return Uri.parse(url.toString());

            }
            catch (Exception ex) {
                System.out.println (ex.toString());

            }

            return null;
        }

    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
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
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}

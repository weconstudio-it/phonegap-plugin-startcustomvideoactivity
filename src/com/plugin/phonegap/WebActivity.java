package com.plugin.phonegap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class WebActivity extends Activity {

    ImageView imageViewBack;

    private int getR(String resourceType, String resourceName) {
        return getApplication().getResources().getIdentifier(resourceName, resourceType, getApplication().getPackageName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // animazioni
        getWindow().getAttributes().windowAnimations = getR("style","Fade");

        setContentView(getR("layout","activity_web"));

        imageViewBack = (ImageView)findViewById(getR("id","imageViewBackWeb"));

        /**
         * Gestione onclick tasto BACK
         */
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        String value = b.getString("url");

        WebView wv = (WebView)findViewById(getR("id", "webView"));
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl(value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(getR("menu","web"), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == getR("id","action_settings")) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

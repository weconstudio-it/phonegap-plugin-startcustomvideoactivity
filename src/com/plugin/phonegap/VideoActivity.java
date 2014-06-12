package com.plugin.phonegap;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import it.weconstudio.utilities.Flags;
import it.weconstudio.utilities.JsonParser;
import it.weconstudio.utilities.ParserEventsListener;
import it.weconstudio.utilities.WebViewStatus;


public class VideoActivity extends Activity implements ParserEventsListener {

    // oggetto video
    WeVideoView videoView = null;

    // oggetti immagine
    ImageView imageViewPoster = null, imageViewRSILogo = null, imageViewBackVideo = null, imageView = null;

    LinearLayout layoutCamera1 = null;
    ImageView imageViewCamera1 = null, imageViewCamera2 = null, imageViewCamera3 = null, imageViewCamera4 = null, imageViewMosaico = null;
    TextView textViewCamera1 = null, textViewCamera2 = null, textViewCamera3 = null, textViewCamera4 = null;

    // caricatore
    ProgressBar progressBarVideo = null;

    // camera corrente
    int currentCamera = 5;

    Uri uriVideo = Uri.EMPTY;

    Flags jsonFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // animazioni
        getWindow().getAttributes().windowAnimations = getR("style", "Fade");

        setContentView(getR("layout","activity_video"));

        videoView = (WeVideoView) findViewById(getR("id","videoView"));
        imageView = (ImageView) findViewById(getR("id","imageView"));
        imageViewPoster = (ImageView) findViewById(getR("id","imageViewPoster"));
        imageViewBackVideo = (ImageView) findViewById(getR("id","imageViewBackVideo"));
        imageViewRSILogo = (ImageView) findViewById(getR("id","imageViewRSILogoVideo"));
        progressBarVideo = (ProgressBar) findViewById(getR("id","progressBarVideo"));

        // pulsanti/immagini camera
        layoutCamera1 = (LinearLayout)findViewById(getR("id","linearLayout"));

        imageViewCamera1 = (ImageView)findViewById(getR("id","imageViewCamera1"));
        imageViewCamera2 = (ImageView)findViewById(getR("id","imageViewCamera2"));
        imageViewCamera3 = (ImageView)findViewById(getR("id","imageViewCamera3"));
        imageViewCamera4 = (ImageView)findViewById(getR("id","imageViewCamera4"));
        imageViewMosaico = (ImageView)findViewById(getR("id","imageViewMosaico"));

        // nomi delle camere
        textViewCamera1 = (TextView)findViewById(getR("id","textViewCamera1"));
        textViewCamera2 = (TextView)findViewById(getR("id","textViewCamera2"));
        textViewCamera3 = (TextView)findViewById(getR("id","textViewCamera3"));
        textViewCamera4 = (TextView)findViewById(getR("id","textViewCamera4"));

        jsonFlags = Flags.getInstance(Uri.parse(Flags.serverName() + "flags.json"));
        jsonFlags.reset(false);

        //Toast.makeText(getApplicationContext(), "CAMERA2", Toast.LENGTH_SHORT).show();

        imageViewBackVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    videoView.stopPlayback();
                    videoView.destroyDrawingCache();
                }

                if (jsonFlags.isDataAvailable()) {
                    jsonFlags.release();
                }

                finish();
            }
        });

        imageViewMosaico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraClick(5);
            }
        });

        imageViewCamera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraClick(1);
            }
        });

        imageViewCamera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraClick(2);
            }
        });

        imageViewCamera3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraClick(3);
            }
        });

        imageViewCamera4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraClick(4);
            }
        });

        /**
         * Gestore click su poster visualizzato dopo video promo
         */
        imageViewPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (jsonFlags.isDataAvailable())
                    openWebView(jsonFlags.get_promo_web());

            }
        });

        /**
         * Gestore click su video
         */
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jsonFlags.isDataAvailable()) {
                    if (jsonFlags.get_streaming_video()) {
                        // verifica coordinate click e gestione mosaico
                    } else {
                        openWebView(jsonFlags.get_promo_web());
                    }
                }
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if ( /*getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && */ currentCamera == 5) {
                    if (jsonFlags.isDataAvailable() && jsonFlags.get_streaming_video()) {
                        double w = view.getWidth() / 2.0;
                        double h = view.getHeight() / 2.0;
                        float curX = motionEvent.getX(), curY = motionEvent.getY();

                        int riquadro = 0;

                        // 1|2
                        // ---
                        // 3|4

                        if (curX < w && curY < h) {
                            riquadro = 1;
                        } else if (curX > w && curY < h) {
                            riquadro = 2;
                        } else if (curX < w && curY > h) {
                            riquadro = 3;
                        } else if (curX > w && curY > h) {
                            riquadro = 4;
                        }

                        cameraClick(riquadro);
                    } else if(!jsonFlags.get_streaming_video()){
                        openWebView(jsonFlags.get_promo_web());
                    }
                }

                return false;
            }
        });

        /**
         * Gestione click su Logo RSI
         */
        imageViewRSILogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jsonFlags.isDataAvailable())
                    openWebView(jsonFlags.get_link_rsi_browser());
            }
        });

        /**
         * Impostazione evento fine video -> caricamento Poster
         */
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.setVisibility(View.INVISIBLE);
                imageViewPoster.setVisibility(View.VISIBLE);

                new VideoPosterSetter().execute(jsonFlags.get_img_streaming_video_offline(), imageViewPoster);
            }
        });



        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progressBarVideo.setVisibility(View.INVISIBLE);


                int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
                videoView.setScreenSize(screenWidth, screenHeight);
                videoView.setMediaPlayer(mediaPlayer);
                videoView.progressBar = progressBarVideo;

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();

        manageOrientation(this.getResources().getConfiguration().orientation);
    }

    @Override
    protected void onStart() {
        super.onStart();

        jsonFlags.addListener(this);

        if (jsonFlags.isDataAvailable()) {
            startPlayVideo(uriVideo);
        }
    }

    // Check screen orientation or screen rotate event here
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        manageOrientation(newConfig.orientation);
    }

    @Override
    protected void onStop() {
        jsonFlags.removeListener(this);

        super.onStop();
    }

    private void cameraClick(int camera) {
        Boolean resyncVideo = currentCamera != camera;

        if (jsonFlags.isDataAvailable()) {

            Uri videoUri = jsonFlags.get_link_flussoandroid(camera);

            WebViewStatus status = WebViewStatus.TRUE;
            Uri webUri = Uri.EMPTY;

            if (camera > 0 && camera < 5) {
                webUri = jsonFlags.get_link_web(camera);
                status = jsonFlags.get_webview_attivo(camera);
            }

            switch (status) {
                case FALSE:
                    Toast.makeText(getApplicationContext(), "CAMERA INATTIVA", Toast.LENGTH_SHORT).show();
                    break;
                case ONCLICK:
                    openWebView(webUri);
                    break;
                case TRUE:
                    currentCamera = camera;

                    if (resyncVideo)
                        startPlayVideo(videoUri);
                    break;
            }

            manageOrientation(this.getResources().getConfiguration().orientation);
        }
    }

    /**
     * Apre l'activity "Browser"
     * @param uri
     */
    private void openWebView (Uri uri) {
        if (!uri.equals(Uri.EMPTY)) {
            Intent myIntent = new Intent(VideoActivity.this, WebActivity.class);

            Bundle b = new Bundle();
            b.putString("url", uri.toString());

            myIntent.putExtras(b);

            VideoActivity.this.startActivity(myIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Pagina non disponibile.", Toast.LENGTH_SHORT).show();
        }
    }

    public void startPlayVideo(Uri uri){
        uriVideo = uri;

        try {
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
                videoView.clearAnimation();
            }


            progressBarVideo.setVisibility(View.VISIBLE);
            imageViewPoster.setVisibility(View.INVISIBLE);

            videoView.setVideoURI(uriVideo);

            videoView.start();

            videoView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            videoView.setVisibility(View.INVISIBLE);
            Log.d("startPlayVideo", e.toString());
            e.printStackTrace();
        }
    }

    private int getR(String resourceType, String resourceName) {
        return getApplication().getResources().getIdentifier(resourceName, resourceType, getApplication().getPackageName());
    }

    private void setCameraStatus(int camera, String cameraText, String cameraWebText, WebViewStatus cameraStatus) {
        float DISABLED_ALPHA = 0.2f, CURRENT_ALPHA = 1.0f, AVAILABLE_ALPHA = 0.5f;

        TextView tV = (TextView) findViewById(getResources().getIdentifier(String.format("textViewCamera%d", camera), "id", getApplication().getPackageName()));
        ImageView iV = (ImageView) findViewById(getResources().getIdentifier(String.format("imageViewCamera%d", camera), "id", getApplication().getPackageName()));

        if (tV != null && iV != null) {
            switch (cameraStatus) {
                case TRUE:
                    if(Build.VERSION.SDK_INT >= 11) {
                        tV.setAlpha(currentCamera == camera ? CURRENT_ALPHA : AVAILABLE_ALPHA);
                        iV.setAlpha(currentCamera == camera ? CURRENT_ALPHA : AVAILABLE_ALPHA);
                    }
                    tV.setText(cameraText);
                    break;
                case FALSE:
                    if(Build.VERSION.SDK_INT >= 11) {
                        tV.setAlpha(DISABLED_ALPHA);
                        iV.setAlpha(DISABLED_ALPHA);
                    }
                    tV.setText(cameraText);
                    break;
                case ONCLICK:
                    if(Build.VERSION.SDK_INT >= 11) {
                        tV.setAlpha(AVAILABLE_ALPHA);
                        iV.setAlpha(AVAILABLE_ALPHA);
                    }
                    tV.setText(cameraWebText);
                    break;
            }
        }
    }

    public void dataAvailable(JsonParser obj) {
        manageOrientation(this.getResources().getConfiguration().orientation);

        Log.d("Data available", uriVideo.toString());
    }

    private void showCameraControls(Boolean showControls) {
        if (showControls) {
            imageViewCamera1.setVisibility(View.VISIBLE);
            imageViewCamera2.setVisibility(View.VISIBLE);
            imageViewCamera3.setVisibility(View.VISIBLE);
            imageViewCamera4.setVisibility(View.VISIBLE);
            imageViewMosaico.setVisibility(View.VISIBLE);

            textViewCamera1.setVisibility(View.VISIBLE);
            textViewCamera2.setVisibility(View.VISIBLE);
            textViewCamera3.setVisibility(View.VISIBLE);
            textViewCamera4.setVisibility(View.VISIBLE);
        } else {
            imageViewCamera1.setVisibility(View.INVISIBLE);
            imageViewCamera2.setVisibility(View.INVISIBLE);
            imageViewCamera3.setVisibility(View.INVISIBLE);
            imageViewCamera4.setVisibility(View.INVISIBLE);
            imageViewMosaico.setVisibility(View.INVISIBLE);

            textViewCamera1.setVisibility(View.INVISIBLE);
            textViewCamera2.setVisibility(View.INVISIBLE);
            textViewCamera3.setVisibility(View.INVISIBLE);
            textViewCamera4.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Gestisce le modifiche all'interfaccia in seguito a una rotazione
     * @param orientation
     */
    private void manageOrientation(final int orientation) {
        final Boolean showCameras = jsonFlags.isDataAvailable() && jsonFlags.get_streaming_video();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                if  (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // in verticale il video non è a full screen
                    imageView.setVisibility(View.VISIBLE);
                    imageViewBackVideo.setVisibility(View.VISIBLE);
                    imageViewRSILogo.setVisibility(View.VISIBLE);

                    // se siamo in verticale e gli stream sono attivi devo visualizzare i controlli
                    showCameraControls(showCameras);
                } else {
                    // in orizzontale il video è a full screen e tutti gli elementi devono essere nascosti
                    imageView.setVisibility(View.INVISIBLE);
                    imageViewBackVideo.setVisibility(View.INVISIBLE);
                    imageViewRSILogo.setVisibility(View.INVISIBLE);

                    // nascondiamo i controlli
                    showCameraControls(false);
                }

                if (showCameras) {
                    for (int i = 1; i <= 4; i++) {
                        setCameraStatus(i, jsonFlags.get_nome_flusso(i), jsonFlags.get_nome_web(i), jsonFlags.get_webview_attivo(i));
                    }

                    if (Build.VERSION.SDK_INT >= 11) {
                        if (currentCamera == 5) {
                            imageViewMosaico.setAlpha(1.0f);
                        } else {
                            imageViewMosaico.setAlpha(0.5f);
                        }
                    }
                }
            }
        });
    }

    public void transitionDetected(JsonParser obj) {
        try {
            Log.d("Transition", jsonFlags.get_streaming_video().toString());

            if (!jsonFlags.get_streaming_video()) {
                // STREAMING DISATTIVATO: visualizzazione promo
                uriVideo = jsonFlags.get_promoandroid();
            } else {
                currentCamera = 5;

                // STREAMING ATTIVATO: visualizzazione mosaico, ecc...
                uriVideo = jsonFlags.get_link_flussoandroid(currentCamera);
            }

            manageOrientation(this.getResources().getConfiguration().orientation);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startPlayVideo(uriVideo);
                }
            });
        } catch (Exception e) {
            Log.d("PD", e.toString());
        }
    }

    private class VideoPosterSetter extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            Bitmap bitmap = null;

            Uri imageUri = (Uri)objects[0];
            final ImageView target = (ImageView)objects[1];

            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUri.toString()).getContent());

                final Bitmap finalBitmap = bitmap;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        target.setImageBitmap(finalBitmap);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    private class PlayListReader extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            try {

                URL url = new URL(uriVideo.toString());
                InputStream M3U8 = (InputStream) url.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(M3U8));
                for (int i = 0; i < 2; ++i)
                    br.readLine();
                String target = br.readLine(); //this parses the third line of the playlist
                br.close();

                uriVideo = Uri.parse(url.toString());

                return Uri.parse(url.toString());

            }
            catch (Exception ex) {
                return uriVideo;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(getR("menu","video"), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == getR("id", "action_settings")) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
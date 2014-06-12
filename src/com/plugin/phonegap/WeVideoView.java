package com.plugin.phonegap;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

/**
 * Created by niko on 6/12/14.
 */
public class WeVideoView extends VideoView {

    public ProgressBar progressBar;

    private MediaPlayer mediaPlayer = null;

    private int screenWidth = 0, screenHeight = 0;


    public void setScreenSize(int screenWidth, int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer){
        this.mediaPlayer = mediaPlayer;
    }

    public WeVideoView(Context context) {
        super(context);
    }
    public WeVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public WeVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        try {


            if (this.mediaPlayer != null) {

                int videoWidth = this.mediaPlayer.getVideoWidth();
                int videoHeight = this.mediaPlayer.getVideoHeight();
                if(videoWidth == 0 || videoHeight == 0){
                    setMeasuredDimension(0, 0);
                    this.setBackgroundColor(Color.BLACK);
                    progressBar.setVisibility(VISIBLE);
                }else {
                    progressBar.setVisibility(INVISIBLE);
                    this.setBackgroundColor(Color.TRANSPARENT);
                    float videoProportion = (float) videoWidth / (float) videoHeight;


                    float screenProportion = (float) this.screenWidth / (float) this.screenHeight;

                    if (videoProportion > screenProportion) {
                        setMeasuredDimension(this.screenWidth, (int) ((float) this.screenWidth / videoProportion));
                    } else {
                        setMeasuredDimension((int) (videoProportion * (float) this.screenHeight), this.screenHeight);
                    }
                }

            }

        }catch (Exception ex){

        }

        /*

        RelativeLayout layoutVideoContainer =(RelativeLayout) findViewById(R.id.relativeLayout);

        android.view.ViewGroup.LayoutParams lp = layoutVideoContainer.getLayoutParams();
        lp.width = (int) (videoProportion * (float) screenHeight);
        lp.height = screenHeight;

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth/2, parentHeight);
        rl.set
        this.setLayoutParams(new RelativeLayout.LayoutParams(parentWidth/2,parentHeight));

        */

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
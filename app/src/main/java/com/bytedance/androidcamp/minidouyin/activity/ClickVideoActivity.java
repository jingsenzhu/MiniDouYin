package com.bytedance.androidcamp.minidouyin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bytedance.androidcamp.minidouyin.MainActivity;
import com.bytedance.androidcamp.minidouyin.R;
import com.bytedance.androidcamp.minidouyin.model.Video;

import cn.jzvd.JZVideoPlayerStandard;

import static cn.jzvd.JZVideoPlayer.CURRENT_STATE_NORMAL;


public class ClickVideoActivity extends AppCompatActivity {

    private JZVideoPlayerStandardLoopVideo mvideoPlayer;

    public static void launch(Activity activity, Video video){
        Intent i = new Intent(activity, ClickVideoActivity.class);
        i.putExtra("image_url",  video.getImageUrl());
        i.putExtra("video_url", video.getVideoUrl());
        activity.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_video);
        mvideoPlayer = (JZVideoPlayerStandardLoopVideo)findViewById(R.id.click_pop_video);
        String imgurl = getIntent().getStringExtra("image_url");
        String videourl = getIntent().getStringExtra("video_url");


        mvideoPlayer.setUp(videourl,JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN | CURRENT_STATE_NORMAL );
        mvideoPlayer.startVideo();
        Glide.with(this).load(imgurl).into(mvideoPlayer.thumbImageView);

    }

    // 按home键，回到app后在原位置播放
    @Override
    protected void onResume() {
        super.onResume();
        //home back
        JZVideoPlayerStandard.goOnPlayOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //     Jzvd.clearSavedProgress(this, null);
        //home back
        JZVideoPlayerStandard.goOnPlayOnPause();
    }



    @Override
    public void onBackPressed() {
        if (JZVideoPlayerStandard.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    public  class JZVideoPlayerStandardLoopVideo extends JZVideoPlayerStandard{
        public JZVideoPlayerStandardLoopVideo (Context context) {
            super(context);
        }

        public JZVideoPlayerStandardLoopVideo (Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void onAutoCompletion() {
            super.onAutoCompletion();
            startVideo();
        }
    }

}




package com.bytedance.androidcamp.minidouyin.activity;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.androidcamp.minidouyin.R;

import java.io.File;

public class UploadVideoActivity extends AppCompatActivity {

    private String mPath;
    private VideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload_video);

        mVideoView = findViewById(R.id.upload_videoview);
        mPath = getIntent().getStringExtra("path");
        mVideoView.setVideoPath(mPath);
        mVideoView.start();
        mVideoView.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        });

        findViewById(R.id.btn_tick).setOnClickListener(v ->{
            // 回传path
            Intent i  = new Intent();
            i.putExtra("path",mPath);
            setResult(RESULT_OK, i);
            finish();
        });

        findViewById(R.id.btn_close).setOnClickListener(v -> {
            File file = new File(mPath);
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    // 删除后相册重新扫描
                    MediaScannerConnection.scanFile(this, new String[]{mPath}, null, null);
                }
            }
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mVideoView.start();
        mVideoView.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        });
    }
}

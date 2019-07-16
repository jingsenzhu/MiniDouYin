package com.bytedance.androidcamp.minidouyin.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.utils.Utils;
import com.bytedance.androidcamp.minidouyin.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {
    private ImageView imgView;
    private Button btn_start;
    private File currentImageFile = null;
    private Uri outputFileUri;

    private static final int RECORD_VIDEO = 0;
    private static final int RECORD_VIDEO_SAVE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        bindViews();

    }

    private void bindViews() {
        imgView = (ImageView) findViewById(R.id.camera_img);
        btn_start = (Button) findViewById(R.id.camera_btn);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takevideo();

            }
        });
    }

    private  void takevideo_save()
    {
        // TODO 2:  无法创建文件
        try {
            File file = createMediaFile();
            outputFileUri = Uri.fromFile(file);
        }
        catch(Exception e){
            Toast.makeText(CameraActivity.this,
                    "createMediaFile fail!"+e.getMessage(), Toast.LENGTH_LONG).show();

        }
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, RECORD_VIDEO_SAVE);
    }
    private void takevideo() {
        //生成Intent.
        // TODO 1： 返回出错
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        //启动摄像头应用程序
        startActivityForResult(intent, RECORD_VIDEO);
    }

    private File createMediaFile() throws IOException {
        final String TAG = "createMediaFile";
        if (true) {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES), "CameraDemo");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "VID_" + timeStamp;
            String suffix = ".mp4";
            File mediaFile = new File(mediaStorageDir + File.separator + imageFileName + suffix);
            return mediaFile;
        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == ) {
////            Bundle bundle = data.getExtras();
////            Bitmap bitmap = (Bitmap) bundle.get("data");
////            img_show.setImageBitmap(bitmap);
//
//            imgView.setImageURI(Uri.fromFile(currentImageFile));
//        }
        if (requestCode == RECORD_VIDEO) {
//            VideoView videoView = (VideoView)findViewById(R.id.activity1_video1);
            Uri uri=data.getData();
//            videoView.setVideoURI(uri);
//            videoView.start();
            Log.v("系统录像", "直接返回视频数据"+uri.getPath());
        }


//        switch (requestCode){
//            case REQUEST_CODE_TAKE_PICTURE:
//                img_show.setImageURI(Uri.fromFile(currentImageFile));
//                break;
//        }
    }


}

package com.bytedance.androidcamp.minidouyin.activity;

import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Bundle;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.androidcamp.minidouyin.MainActivity;
import com.bytedance.androidcamp.minidouyin.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.media.MediaRecorder.VideoEncoder.H264;
import static com.bytedance.androidcamp.minidouyin.utils.Utils.MEDIA_TYPE_IMAGE;
import static com.bytedance.androidcamp.minidouyin.utils.Utils.MEDIA_TYPE_VIDEO;
import static com.bytedance.androidcamp.minidouyin.utils.Utils.getOutputMediaFile;

public class CustomCameraActivity extends AppCompatActivity {

    public static final int REQUEST_UPLOAD = 4567;
    private SurfaceView mSurfaceView;
    private Camera mCamera;

    private File mVideoFile;
    private File mPictureFile;
    private ImageView mRecordImageview;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;

    private int rotationDegree = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // TODO : add camera layout
        setContentView(R.layout.activity_custom_camera);
        // TODO : add surfaceview
        mSurfaceView = findViewById(R.id.camera_sv);
        //todo 给SurfaceHolder添加Callback
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                startPreview(holder);

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseCameraAndPreview();
            }
        });

//        findViewById(R.id.btn_picture).setOnClickListener(v -> {
//            //todo 拍一张照片, 调用mPicture 回调
//            mCamera.takePicture(null,null,mPicture);
//        });




        mRecordImageview = findViewById(R.id.btn_record);
        mRecordImageview.setOnClickListener(v -> {
            //todo 录制，第一次点击是start，第二次点击是stop
            if (isRecording) {
                //todo 停止录制
                stopRecord();

            } else {
                //todo 录制
                prepareVideoRecorder();
                isRecording = true;
                mRecordImageview.setImageDrawable(getResources().getDrawable(R.drawable.circle_stop_svg));
            }
        });



        findViewById(R.id.btn_facing).setOnClickListener(v -> {
            //todo 切换前后摄像头
            //releaseCameraAndPreview();
            if(CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK){
                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

            }else{
                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
                startPreview(mSurfaceView.getHolder());
        });

    }

    // 停止录制
    private void stopRecord(){
        releaseMediaRecorder();
        isRecording = false;
        mRecordImageview.setImageDrawable(getResources().getDrawable(R.drawable.circle_svg));
        Intent i = new Intent(this, UploadVideoActivity.class);
        i.putExtra("path", mVideoFile.getAbsolutePath());
        startActivityForResult(i, REQUEST_UPLOAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UPLOAD && resultCode == RESULT_OK && data != null) {
            String returnPath = data.getStringExtra("path");
            if (returnPath != null ) {
                Intent i = new Intent();
                i.putExtra("path",returnPath);
                setResult(RESULT_OK, i);
                finish();
            }
        }
    }

    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        Camera cam = Camera.open(position);

        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等

        rotationDegree = getCameraDisplayOrientation(position);
        cam.setDisplayOrientation(rotationDegree);
        Camera.Parameters parameters = cam.getParameters();
        size = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(),mSurfaceView.getWidth(),mSurfaceView.getHeight());
        parameters.setPreviewSize(size.width, size.height);

        // 开启自动调焦，首先需要判断手机是否支持
        List<String> modes = parameters.getSupportedFocusModes(); // 判断支持
        for(String mode : modes) {
            if(mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                break;
            }
        }
        cam.setParameters(parameters);

        return cam;
    }


    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        //todo 释放camera资源
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) {
        //todo 开始预览
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch (Exception e){
            Toast.makeText(CustomCameraActivity.this,
                    "mCamera.setPreviewDisplay failed!"+e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private MediaRecorder mMediaRecorder;

    private boolean prepareVideoRecorder() {
        //todo 准备MediaRecorder

        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // 最大录制时间 10 seconds
        mMediaRecorder.setMaxDuration(10*1000);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
        mVideoFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        mMediaRecorder.setOutputFile( mVideoFile.toString());

        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);

        try{
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            // 设置达到最大录制时间自动停止
            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
                    if(i==MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
                    {
                        stopRecord();
                    }
                }
            });

        }catch (Exception e){
            Toast.makeText(CustomCameraActivity.this,
                    "MediaRecorder prepare Failed!"+e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            releaseMediaRecorder();
            return false;
        }
        // 使得图片可以被相册找到
        MediaScannerConnection.scanFile(this, new String[] { mVideoFile.toString()},null,null);
        return true;
    }


    private void releaseMediaRecorder() {
        //todo 释放MediaRecorder
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
    }


    private Camera.PictureCallback mPicture = (data, camera) -> {
        mPictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (mPictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFile);
            fos.write(data);
            fos.close();
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
            //todo 如果存在预览方向改变，进行图片旋转
//            bitmap = Utils.rotateImage(bitmap, mPictureFile.getAbsolutePath());
//            FileOutputStream fos1 = new FileOutputStream(mPictureFile);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos1.flush();
//            fos1.close();
        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }
        // 使得图片可以被相册找到
        MediaScannerConnection.scanFile(this, new String[] { mPictureFile.toString()},null,null);
        mCamera.startPreview();
    };


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

}

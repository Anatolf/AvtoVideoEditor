package com.example.avtovideoeditor;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.example.avtovideoeditor.gpuvideoandroid.PortraitCameraActivity;
import com.iknow.android.features.select.VideoSelectActivity;


public class MainActivity extends Activity {

    SurfaceView previewSurfaceView;
    Camera camera;
    MediaRecorder mediaRecorder;
    Button testButton;

    final int CAMERA_ID = 1; // 0 back camera
    static long countVideo;
    File videoFile;
    File picturesDir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        testButton = findViewById(R.id.record_btn);

        picturesDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        videoFile = new File(picturesDir, "myvideo_" + countVideo + ".mp4"); //3gp _ тут просто объявляем фаил

        previewSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        SurfaceHolder holder = previewSurfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {
                camera.stopPreview();
                setCameraDisplayOrientation(CAMERA_ID);
                try {
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // do nothing
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();
        if (camera != null)
            camera.release();
        camera = null;
    }

//    public void onClickTest(View v){
//        startActivity(new Intent(this, MultipleCommandsExampleActivity.class));
//    }

    public void onClickRecord(View v) {
        PortraitCameraActivity.startActivity(this);
    }


    //        public  void onClickTrim(View v){
//        startActivity(new Intent(this, BPMCounterActivity.class));
//    }
    public void onClickTrim(View v) {
        //startActivity(new Intent(this, VideoSelectActivity.class));
        startActivity(new Intent(this, VideoSelectActivity.class));
    }


    public void onClickStartRecord(View view) {
        if (prepareVideoRecorder()) {
            countVideo++;
            videoFile = new File(picturesDir, "myvideo_" + countVideo + ".mp4"); //3gp
            mediaRecorder.start();
        } else {
            releaseMediaRecorder();
        }
    }

    public void onClickStopRecord(View view) {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            releaseMediaRecorder();
        }
    }

    private boolean prepareVideoRecorder() {

        camera.unlock();
        mediaRecorder = new MediaRecorder();

        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile
                .get(CamcorderProfile.QUALITY_HIGH));
        mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
        mediaRecorder.setPreviewDisplay(previewSurfaceView.getHolder().getSurface());

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.lock();
        }
    }


    /// по повороту картинки ///
    void setCameraDisplayOrientation(int cameraId) {
        // определяем насколько повернут экран от нормального положения
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = 0;

        // получаем инфо по камере cameraId
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        // задняя камера
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            result = ((360 - degrees) + info.orientation);
        } else
            // передняя камера
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = ((360 - degrees) - info.orientation);
                result += 360;
            }
        result = result % 360;
        camera.setDisplayOrientation(result);
    }
}
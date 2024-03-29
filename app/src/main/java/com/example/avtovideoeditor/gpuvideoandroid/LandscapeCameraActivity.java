package com.example.avtovideoeditor.gpuvideoandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.avtovideoeditor.R;

public class LandscapeCameraActivity extends BaseCameraActivity {

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, LandscapeCameraActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_landscape);
        onCreateActivity();
        videoWidth = 1280;
        videoHeight = 720;
        cameraWidth = 1280;
        cameraHeight = 720;
    }
}


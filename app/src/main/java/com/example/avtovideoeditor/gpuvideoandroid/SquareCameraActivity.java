package com.example.avtovideoeditor.gpuvideoandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.avtovideoeditor.R;

public class SquareCameraActivity extends BaseCameraActivity {

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, SquareCameraActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_square);
        onCreateActivity();
    }
}


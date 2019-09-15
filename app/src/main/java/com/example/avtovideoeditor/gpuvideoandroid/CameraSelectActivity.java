package com.example.avtovideoeditor.gpuvideoandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;



import com.example.avtovideoeditor.R;

public class CameraSelectActivity extends AppCompatActivity {

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, CameraSelectActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        findViewById(R.id.portrate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PortraitCameraActivity.startActivity(CameraSelectActivity.this);
            }
        });

        findViewById(R.id.landscape).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LandscapeCameraActivity.startActivity(CameraSelectActivity.this);
            }
        });

        findViewById(R.id.square).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SquareCameraActivity.startActivity(CameraSelectActivity.this);
            }
        });
    }
}

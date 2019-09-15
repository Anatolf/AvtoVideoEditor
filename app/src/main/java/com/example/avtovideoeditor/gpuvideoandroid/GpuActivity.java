package com.example.avtovideoeditor.gpuvideoandroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;





import com.example.avtovideoeditor.R;

public class GpuActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 88888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gpu);
        findViewById(R.id.movie_preview).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlayerActivity.startActivity(GpuActivity.this);
                    }
                }
        );
        findViewById(R.id.camera_record).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CameraSelectActivity.startActivity(GpuActivity.this);
                    }
                }
        );
        findViewById(R.id.mp4_compose).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Mp4ComposeActivity.startActivity(GpuActivity.this);
                    }
                }
        );
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        // request camera permission if it has not been grunted.
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(GpuActivity.this, "permission has been grunted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GpuActivity.this, "[WARN] permission is not grunted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

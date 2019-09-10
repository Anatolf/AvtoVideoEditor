package com.example.avtovideoeditor.china_video_trimmer.features.record;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.avtovideoeditor.china_video_trimmer.features.common.ui.BaseActivity;
import com.example.avtovideoeditor.china_video_trimmer.features.record.view.PreviewSurfaceView;
import com.example.avtovideoeditor.R;
//import com.iknow.android.R;
//import com.iknow.android.features.common.ui.BaseActivity;
//import com.iknow.android.features.record.view.PreviewSurfaceView;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2019/02/22 4:24 PM
 * version: 1.0
 * description:
 */
public class VideoRecordActivity extends BaseActivity implements View.OnClickListener {
    private PreviewSurfaceView cameraPreview;
    private ImageView mIvRecordBtn, mIvSwitchCameraBtn, mIvRecordBack;

    public static void call(Context context) {
        context.startActivity(new Intent(context, VideoRecordActivity.class));
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_video_recording);
        cameraPreview = this.findViewById(R.id.glView);
        mIvRecordBtn = this.findViewById(R.id.ivRecord);
        mIvSwitchCameraBtn = this.findViewById(R.id.ivSwitch);
        mIvRecordBack = this.findViewById(R.id.ivRecordBack);
        mIvRecordBtn.setOnClickListener(this);
        mIvSwitchCameraBtn.setOnClickListener(this);
        mIvRecordBack.setOnClickListener(this);
        cameraPreview.startPreview();

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivRecord:
                Toast.makeText(this, "Кнопка Rec нажата!", Toast.LENGTH_SHORT).show();
                cameraPreview.startPreview();
                //RecordStopExpandCamera.class.
                break;
            case R.id.ivSwitch:
                Toast.makeText(this, "Разворот камеры", Toast.LENGTH_SHORT).show();
                cameraPreview.switchCamera();
                break;
            case R.id.ivRecordBack:
                Toast.makeText(this, "Кнопка назад", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
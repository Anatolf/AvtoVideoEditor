package com.iknow.android.features.select;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.avtovideoeditor.R;
import com.iknow.android.features.record.VideoRecordActivity;
import com.iknow.android.features.record.view.CameraPreviewLayout;
import com.iknow.android.features.record.view.PreviewSurfaceView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import iknow.android.utils.callback.SimpleCallback;

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class VideoSelectActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoSelectAdapter mVideoSelectAdapter;
    private VideoLoadManager mVideoLoadManager;
    private PreviewSurfaceView mSurfaceView;
    private CameraPreviewLayout cameraPreviewLayout;
    private ImageView mBtnBack;
    private RelativeLayout cameraPreviewLy;
    private LinearLayout openCameraPermissionLy;
    private GridView videoGridview;
    private TextView mOpenCameraPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_select);
        mVideoLoadManager = new VideoLoadManager();
        mVideoLoadManager.setLoader(new VideoCursorLoader());
        cameraPreviewLayout = findViewById(R.id.capturePreview);
        mBtnBack = findViewById(R.id.mBtnBack);

        videoGridview = findViewById(R.id.video_gridview);
        cameraPreviewLy = findViewById(R.id.cameraPreviewLy);
        openCameraPermissionLy = findViewById(R.id.openCameraPermissionLy);
        cameraPreviewLy = findViewById(R.id.cameraPreviewLy);
        mOpenCameraPermission = findViewById(R.id.mOpenCameraPermission);

        mBtnBack.setOnClickListener(this);

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(granted -> {
            if (granted) { // Always true pre-M
                mVideoLoadManager.load(this, new SimpleCallback() {
                    @Override
                    public void success(Object obj) {
                        if (mVideoSelectAdapter == null) {
                            mVideoSelectAdapter = new VideoSelectAdapter(VideoSelectActivity.this, (Cursor) obj);
                        } else {
                            mVideoSelectAdapter.swapCursor((Cursor) obj);
                        }
                        if (videoGridview.getAdapter() == null) {
                            videoGridview.setAdapter(mVideoSelectAdapter);
                        }
                        mVideoSelectAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                finish();
            }
        });
        if (rxPermissions.isGranted(Manifest.permission.CAMERA)) {
            initCameraPreview();
        } else {
            cameraPreviewLy.setVisibility(View.GONE);
            openCameraPermissionLy.setVisibility(View.VISIBLE);
            mOpenCameraPermission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rxPermissions.request(Manifest.permission.CAMERA).subscribe(granted -> {
                        if (granted) {
                            initCameraPreview();
                        }
                    });
                }
            });
        }
    }

    private void initCameraPreview() {
        mSurfaceView = new PreviewSurfaceView(this);
        cameraPreviewLy.setVisibility(View.VISIBLE);
        openCameraPermissionLy.setVisibility(View.GONE);
        cameraPreviewLayout.show(mSurfaceView);
        mSurfaceView.startPreview();
        cameraPreviewLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoRecordActivity.call(VideoSelectActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBtnBack.getId()) {
            finish();
        }
    }
}

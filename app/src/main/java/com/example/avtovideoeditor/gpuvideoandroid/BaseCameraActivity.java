package com.example.avtovideoeditor.gpuvideoandroid;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLException;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.daasuu.gpuv.camerarecorder.CameraRecordListener;
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorderBuilder;
import com.daasuu.gpuv.camerarecorder.LensFacing;
import com.example.avtovideoeditor.R;
import com.example.avtovideoeditor.gpuvideoandroid.widget.SampleCameraGLView;
import com.iknow.android.interfaces.VideoTrimListener;
import com.iknow.android.utils.ToastUtil;
import com.iknow.android.widget.VideoTrimmerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

public class BaseCameraActivity extends AppCompatActivity implements VideoTrimListener {

    private SampleCameraGLView sampleGLView;
    protected com.daasuu.gpuv.camerarecorder.GPUCameraRecorder GPUCameraRecorder;
    private String filepath;
    private TextView recordBtn;
    protected LensFacing lensFacing = LensFacing.BACK;
    protected int cameraWidth = 1280;
    protected int cameraHeight = 720;
    protected int videoWidth = 720;
    protected int videoHeight = 720;
    private boolean toggleClick = false;

    private FrameLayout cameraPreviewLayout;
    private VideoTrimmerView trimmerView;
    private ProgressDialog mProgressDialog;

    private boolean recordState = false;
    private ListView effectsList;


    protected void onCreateActivity() {
        getSupportActionBar().hide();

        //wrap_view
        cameraPreviewLayout = findViewById(R.id.record_preview_layout);
        trimmerView = findViewById(R.id.trimmer_view);

        recordBtn = findViewById(R.id.btn_record);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recordState) {
                    filepath = getVideoFilePath();
                    GPUCameraRecorder.start(filepath);
                    recordBtn.setText(getString(R.string.app_stop_record));
                    effectsList.setVisibility(View.GONE);
                    recordState = true;

                    cameraPreviewLayout.setVisibility(View.VISIBLE);
                    if (sampleGLView != null) {
                        sampleGLView.setVisibility(View.VISIBLE);
                        //cameraPreviewLayout.addView(sampleGLView);  // отобразить превью
                    }
                    trimmerView.setVisibility(View.GONE);


                } else {
                    recordState = false;
                    GPUCameraRecorder.stop();
                    recordBtn.setText(getString(R.string.app_record));
                    effectsList.setVisibility(View.VISIBLE);

                    sampleGLView.setVisibility(View.GONE);
                    cameraPreviewLayout.setVisibility(View.GONE);
                    cameraPreviewLayout.removeAllViews();
                    trimmerView.setVisibility(View.VISIBLE);
                    if (trimmerView != null) {
                        trimmerView.setOnTrimVideoListener(BaseCameraActivity.this);
                        trimmerView.initVideoByURI(Uri.parse(filepath));
                    }
                }
            }
        });


        findViewById(R.id.btn_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GPUCameraRecorder != null && GPUCameraRecorder.isFlashSupport()) {
                    GPUCameraRecorder.switchFlashMode();
                    GPUCameraRecorder.changeAutoFocus();
                }
            }
        });

        findViewById(R.id.btn_switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseCamera();
                if (lensFacing == LensFacing.BACK) {
                    lensFacing = LensFacing.FRONT;
                } else {
                    lensFacing = LensFacing.BACK;
                }
                toggleClick = true;
            }
        });


        findViewById(R.id.btn_image_capture).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                captureBitmap(new BitmapReadyCallbacks() {
                    @Override
                    public void onBitmapReady(final Bitmap bitmap) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                String imagePath = getImageFilePath();
                                saveAsPngImage(bitmap, imagePath);
                                exportPngToGallery(getApplicationContext(), imagePath);
                            }
                        });
                    }
                });
            }
        });

        effectsList = findViewById(R.id.filter_list);

        final List<FilterType> filterTypes = FilterType.createFilterList();
        effectsList.setAdapter(new FilterAdapter(this, R.layout.row_white_text, filterTypes).whiteMode());
        effectsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (GPUCameraRecorder != null) {
                    GPUCameraRecorder.setFilter(FilterType.createGlFilter(filterTypes.get(position), getApplicationContext()));
                }
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (trimmerView != null) {
            trimmerView.onVideoPause();
            trimmerView.setRestoreState(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (trimmerView != null) {
            trimmerView.onDestroy();
        }
    }

    private void releaseCamera() {
        if (sampleGLView != null) {
            sampleGLView.onPause();
        }

        if (GPUCameraRecorder != null) {
            GPUCameraRecorder.stop();
            GPUCameraRecorder.release();
            GPUCameraRecorder = null;
        }

        if (sampleGLView != null) {
            ((FrameLayout) findViewById(R.id.record_preview_layout)).removeView(sampleGLView);
            sampleGLView = null;
        }
    }


    private void setUpCameraView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cameraPreviewLayout.removeAllViews();
                sampleGLView = null;
                sampleGLView = new SampleCameraGLView(getApplicationContext());
                sampleGLView.setTouchListener(new SampleCameraGLView.TouchListener() {

                    @Override
                    public void onTouch(MotionEvent event, int width, int height) {
                        if (GPUCameraRecorder == null) {
                            return;
                        }
                        GPUCameraRecorder.changeManualFocusPoint(event.getX(), event.getY(), width, height);
                    }
                });
                cameraPreviewLayout.addView(sampleGLView);
            }
        });
    }


    private void setUpCamera() {
        setUpCameraView();

        GPUCameraRecorder = new GPUCameraRecorderBuilder(this, sampleGLView)
                //.recordNoFilter(true)
                .cameraRecordListener(new CameraRecordListener() {
                    @Override
                    public void onGetFlashSupport(final boolean flashSupport) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.btn_flash).setEnabled(flashSupport);
                            }
                        });
                    }

                    @Override
                    public void onRecordComplete() {
                        exportMp4ToGallery(getApplicationContext(), filepath);
                    }

                    @Override
                    public void onRecordStart() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                effectsList.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e("GPUCameraRecorder", exception.toString());
                    }

                    @Override
                    public void onCameraThreadFinish() {
                        if (toggleClick) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setUpCamera();
                                }
                            });
                        }
                        toggleClick = false;
                    }

                    @Override
                    public void onVideoFileReady() {

                    }
                })
                .videoSize(videoWidth, videoHeight)
                .cameraSize(cameraWidth, cameraHeight)
                .lensFacing(lensFacing)
                .build();


    }

    @Override
    public void onStartTrim() {
        buildDialog(getResources().getString(R.string.trimming)).show();
    }

    @Override
    public void onFinishTrim(String in) {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        ToastUtil.longShow(this, getString(R.string.trimmed_done));
        finish();
        //TODO: please handle your trimmed video url here!!!
        //String out = StorageUtil.getCacheDir() + File.separator + COMPRESSED_VIDEO_FILE_NAME;
        //buildDialog(getResources().getString(R.string.compressing)).show();
        //VideoCompressor.compress(this, in, out, new VideoCompressListener() {
        //  @Override public void onSuccess(String message) {
        //  }
        //
        //  @Override public void onFailure(String message) {
        //  }
        //
        //  @Override public void onFinish() {
        //    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        //    finish();
        //  }
        //});
    }

    @Override
    public void onCancel() {
        trimmerView.onDestroy();
        finish();
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "", msg);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }
//    private void changeFilter(Filters filters) {
//        GPUCameraRecorder.setFilter(Filters.getFilterInstance(filters, getApplicationContext()));
//    }


    interface BitmapReadyCallbacks {
        void onBitmapReady(Bitmap bitmap);
    }

    private void captureBitmap(final BitmapReadyCallbacks bitmapReadyCallbacks) {
        sampleGLView.queueEvent(new Runnable() {
            @Override
            public void run() {
                EGL10 egl = (EGL10) EGLContext.getEGL();
                GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
                final Bitmap snapshotBitmap = createBitmapFromGLSurface(
                        sampleGLView.getMeasuredWidth(), sampleGLView.getMeasuredHeight(), gl);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bitmapReadyCallbacks.onBitmapReady(snapshotBitmap);
                    }
                });
            }
        });
    }

    private Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) {

        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2, texturePixel, blue, red, pixel;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    texturePixel = bitmapBuffer[offset1 + j];
                    blue = (texturePixel >> 16) & 0xff;
                    red = (texturePixel << 16) & 0x00ff0000;
                    pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            Log.e("CreateBitmap", "createBitmapFromGLSurface: " + e.getMessage(), e);
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    public void saveAsPngImage(Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void exportMp4ToGallery(Context context, String filePath) {
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filePath)));
    }

    public static String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "GPUCameraRecorder.mp4";
    }

    public static File getAndroidMoviesFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

    private static void exportPngToGallery(Context context, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static String getImageFilePath() {
        return getAndroidImageFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "GPUCameraRecorder.png";
    }

    public static File getAndroidImageFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

}

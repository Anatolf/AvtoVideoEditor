package com.example.avtovideoeditor.gpuvideoandroid.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;





/**
 * Aspect 16 : 9 of View
 */
public class MovieWrapperView extends FrameLayout {

    public MovieWrapperView(Context context) {
        super(context);
    }

    public MovieWrapperView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MovieWrapperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, measuredWidth / 16 * 9);
    }
}


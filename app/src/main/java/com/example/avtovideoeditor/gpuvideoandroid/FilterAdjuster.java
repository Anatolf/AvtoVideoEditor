package com.example.avtovideoeditor.gpuvideoandroid;

import com.daasuu.gpuv.egl.filter.GlFilter;

interface FilterAdjuster {
    void adjust(GlFilter filter, int percentage);
}


package com.shockwave.pdf_scaner;

import android.app.Application;

import me.pqpo.smartcropperlib.SmartCropper;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SmartCropper.buildImageDetector(this);
    }
}

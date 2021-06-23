package com.shockwave.pdf_scaner.model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.shockwave.pdf_scaner.util.FileUtils;

import java.util.ArrayList;

public class PhotoModel {
    public String path = "";
    public boolean isSelected = false;

    public PhotoModel(String path) {
        this.path = path;
    }

    public PhotoModel(String path, boolean isSelected) {
        this.path = path;
        this.isSelected = isSelected;
    }
}

package com.shockwave.pdf_scaner.util;

import android.content.Context;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.model.ColorModel;

import java.io.File;
import java.util.ArrayList;

public class ParamUtils {

    private static String PACKAGE_PREFIX = "PDF_Scanner";
    public static String pngExtension = ".png";
    public static String jpgExtension = ".jpg";
    public static String LINK = "LINK";
    public static String IS_ORC = "IS_ORC";
    public static String IS_ONLY = "IS_ONLY";
    public static String LINK_SIGNATURE = "LINK_SIGNATURE";
    public static String PDF_FOLDER = Environment.getExternalStorageDirectory().toString() + File.separator + PACKAGE_PREFIX;

    public static ArrayList<ColorModel> getDefaultColors(Context context) {
        ArrayList<ColorModel> colorPickerColors = new ArrayList<>();
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.white), true));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.darker_gray)));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.holo_orange_dark)));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.holo_orange_light)));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.holo_red_light)));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.holo_blue_dark)));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.holo_blue_bright)));
        return colorPickerColors;
    }

    public static ArrayList<ColorModel> getDefaultColors2(Context context) {
        ArrayList<ColorModel> colorPickerColors = new ArrayList<>();
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.white), true));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.darker_gray)));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, R.color.black)));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.holo_orange_dark)));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.holo_orange_light)));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.holo_red_light)));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.holo_blue_dark)));
        colorPickerColors.add(new ColorModel(ContextCompat.getColor(context, android.R.color.holo_blue_bright)));
        return colorPickerColors;
    }
}

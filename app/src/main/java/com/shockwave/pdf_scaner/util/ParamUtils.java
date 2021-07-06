package com.shockwave.pdf_scaner.util;

import android.content.Context;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import com.shockwave.pdf_scaner.R;
import com.shockwave.pdf_scaner.model.ColorModel;
import com.shockwave.pdf_scaner.model.PageModel;

import java.io.File;
import java.util.ArrayList;

public class ParamUtils {

    private static String PACKAGE_PREFIX = "PDF_Scanner";
    public static String pngExtension = ".png";
    public static String jpgExtension = ".jpg";
    public static String URI = "URI";
    public static String LINK = "LINK";
    public static String IS_ORC = "IS_ORC";
    public static String IS_ONLY = "IS_ONLY";
    public static String PAGE_SIZE = "PAGE_SIZE";
    public static String PAGE_LANDSCAPE = "PAGE_LANDSCAPE";
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

    public static ArrayList<PageModel> getListPage() {
        ArrayList<PageModel> listPage = new ArrayList<>();
        listPage.add(new PageModel("A3", "29,7 x 42,0 cm"));
        listPage.add(new PageModel("A4", "21,0 x 29,7 cm"));
        listPage.add(new PageModel("A5", "14,8 x 21,0 cm"));
        listPage.add(new PageModel("B4", "25,0 x 35,3 cm"));
        listPage.add(new PageModel("B5", "17,6 x 25,0 cm"));
        listPage.add(new PageModel("Letter", "21,6 x 27,9 cm"));
        listPage.add(new PageModel("Legal", "21,6 x 35,6cm"));
        listPage.add(new PageModel("Executive", "18,4 x 29,7 cm"));
        return listPage;
    }
}

package com.shockwave.pdf_scaner.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class PixelUtil {

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * getPixelScaleFactor(context));
    }

    public static int pxToDp(Context context, int px) {
        return (int) (px / getPixelScaleFactor(context));
    }

    private static float getPixelScaleFactor(Context context) {
        return context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT;
    }
}

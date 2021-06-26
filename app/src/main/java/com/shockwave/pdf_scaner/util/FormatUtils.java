package com.shockwave.pdf_scaner.util;

import java.text.DecimalFormat;

public class FormatUtils {

    public static String formatPageSize(float f, int i) {
        DecimalFormat decimalFormat;
        if (i == 0) {
            decimalFormat = new DecimalFormat("0");
        } else if (i == 1) {
            decimalFormat = new DecimalFormat("0.0");
        } else if (i != 2) {
            return String.valueOf(f);
        } else {
            decimalFormat = new DecimalFormat("0.00");
        }
        String format = decimalFormat.format(Float.valueOf(f));
        return format;
    }
}

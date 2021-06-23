package com.shockwave.pdf_scaner.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    public static String formatUTCToLocalDateTime(long timeLong, String format) {
        long value = convertUTCToLocalTime(timeLong);
        DateFormat oldFormatter = new SimpleDateFormat(format, Locale.US);
        return oldFormatter.format(value);
    }

    private static long convertUTCToLocalTime(long timestampMs) {
        TimeZone localZone = TimeZone.getDefault();
        long offset = localZone.getOffset(timestampMs);
        return timestampMs + offset;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime(String format) {
        DateFormat time = new SimpleDateFormat(format);
        return time.format(new Date());
    }

    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }
    }

    public static String convertToVideoDate(long date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            return simpleDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            return "";
        }
    }
}

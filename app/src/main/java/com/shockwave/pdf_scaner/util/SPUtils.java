package com.shockwave.pdf_scaner.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SPUtils {

    public static void saveString(Context context, String key, String value) {
        if (context == null) return;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public static String getString(Context context, String key) {
        if (context == null) return "";
        String value = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getString(key, "");
        }
        return value;
    }

    public static void saveLong(Context context, String key, long value) {
        if (context == null) return;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(key, value);
            editor.commit();
        }
    }

    public static long getLong(Context context, String key) {
        if (context == null) return 0;
        long value = 0;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getLong(key, 0);
        }
        return value;
    }

    public static void saveInt(Context context, String key, int value) {
        if (context == null) return;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public static int getInt(Context context, String key) {
        if (context == null) return 0;
        int value = 0;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getInt(key, 0);
        }
        return value;
    }


    public static void saveBoolean(Context context, String key, Boolean value) {
        if (context == null) return;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public static boolean getBoolean(Context context, String key) {
        if (context == null) return false;
        boolean value = false;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getBoolean(key, false);
        }
        return value;
    }

    public static void save(Context context, String key, String value) {
        if (context == null) return;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public static String read(Context context, String key) {
        if (context == null) return "";
        String value = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getString(key, null);
        }
        return value;
    }

    public static <T> void put(Context context, String key, T value) {
        if (value == null || context == null) {
            return;
        }
        String json = new Gson().toJson(value);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key, json);
        editor.commit();
    }

    public static <T> void put(Context context, Class sClass, T value) {
        if (value == null || context == null) {
            return;
        }
        String json = new Gson().toJson(value);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(sClass.getName(), json);
        editor.commit();
    }

    public static <T> T get(Context context, String key, Class<T> type) {
        if (context == null) return null;
        String json = PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
        if (!TextUtils.isEmpty(json)) {
            return new Gson().fromJson(json, type);
        } else {
            return null;
        }
    }

    public static void remove(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void removeAllKey(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Map<String, ?> prefs = sharedPreferences.getAll();
        for (Map.Entry<String, ?> prefToReset : prefs.entrySet()) {
            editor.remove(prefToReset.getKey()).commit();
        }
        editor.clear();
        editor.commit();
    }

    public static <T> void putArray(Context context, String key, List<T> value) {
        if (value == null || context == null) {
            return;
        }
        String json = new Gson().toJson(value);
        String className = List.class.getName() + key;
        saveString(context,className,json);
    }

    public static <T> List<T> getArray(Context context, Class t) {
        if (context == null) return new ArrayList<>();
        String className = List.class.getName() + t.getName();
        String json = getString(context,className);
        if (!TextUtils.isEmpty(json)) {
            Type type = new TypeToken<Class<T[]>>() {}.getType();
            T[] data = new Gson().fromJson(json, type);
            return Arrays.asList(data);
        } else {
            return new ArrayList<>();
        }
    }
}

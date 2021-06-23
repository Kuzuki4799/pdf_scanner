package com.shockwave.pdf_scaner.util;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.shockwave.pdf_scaner.R;

public class ShareUtils {
    private static final String KEY_OPEN_APP = "prefOpenApp";

    public static void putOpenApp(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int value = preferences.getInt(KEY_OPEN_APP, 0) + 1;
        preferences.edit().putInt(KEY_OPEN_APP, value).apply();
    }

    public static int getOpenApp(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(KEY_OPEN_APP, 0);
    }

    public static void showMoreApp(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:thanhios68")));
        } catch (ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=thanhios68")));
        }
    }

    public static void share(Context context) {
        String str1 = context.getPackageName();
        String str2 = context.getResources().getString(R.string.app_name);
        String str3 = "New " + str2 + " on GOOGLE PLAY Download Now \n " + str2 + " \n https://play.google.com/store/apps/details?id=" + str1;
        Intent localIntent2 = new Intent("android.intent.action.SEND");
        localIntent2.setType("text/plain");
        localIntent2.putExtra("android.intent.extra.SUBJECT", str2);
        localIntent2.putExtra("android.intent.extra.TEXT", str3);
        context.startActivity(localIntent2);
    }

    public static void likeFB(Context context, String fbUrl, String fbId) {
        try {
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String facebookUrl = getFacebookPageURL(context, fbUrl, fbId);
            facebookIntent.setData(Uri.parse(facebookUrl));
            context.startActivity(facebookIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("IntentReset")
    public static void sendFeedback(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("message/rfc822");
        intent.setData(Uri.parse("mailto:"));
        String str2 = context.getString(R.string.app_name);
        String str3 = "";
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"thanhbg211@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, str2);
        intent.putExtra(Intent.EXTRA_TEXT, str3);
        try {
            context.startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (ActivityNotFoundException ex) {
            //Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void launchMarket(Context context, String paramString) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + paramString)));
        } catch (ActivityNotFoundException localActivityNotFoundException) {
            try {
                context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + paramString)));
            } catch (Exception localException) {

                Toast toast = Toast.makeText(context, "unable to find market app", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private static String getFacebookPageURL(Context context, String fbUrl, String fbId) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + fbUrl;
            } else { //older versions of fb app
                return "fb://page/" + fbId;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return fbUrl; //normal web url
        }
    }
}

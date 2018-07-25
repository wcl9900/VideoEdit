//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wcl.videoedit.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public ToastUtils() {
    }

    public static void show(Context context, int resId) {
        show(context, context.getResources().getText(resId), 0);
    }

    public static void show(Context context, int resId, int duration) {
        show(context, context.getResources().getText(resId), duration);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, 0);
    }

    public static void show(final Context context, final CharSequence text, final int duration) {
        if(context instanceof Activity) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, text, duration).show();
                }
            });
        }
        else {
            Toast.makeText(context, text, duration).show();
        }
    }

    public static void show(Context context, int resId, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), 0);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), 0);
    }

    public static void show(Context context, int resId, int duration, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), duration);
    }

    public static void show(Context context, String format, int duration, Object... args) {
        show(context, String.format(format, args), duration);
    }

    public static void showShort(Context context, int resId) {
        show(context, context.getResources().getString(resId), 0);
    }

    public static void showShort(Context context, String msg) {
        show(context, msg, 0);
    }

    public static void showLong(Context context, int resId) {
        show(context, context.getResources().getString(resId), 1);
    }

    public static void showLong(Context context, String msg) {
        show(context, msg, 1);
    }
}

package com.blankj.utilcode.util;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Locale;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public class ViewUtils {
    public static void setViewEnabled(View view, boolean enabled) {
        setViewEnabled(view, enabled, null);
    }

    public static void setViewEnabled(View view, boolean enabled, View... excludes) {
        if (view == null) {
            return;
        }
        if (excludes != null) {
            for (View exclude : excludes) {
                if (view == exclude) {
                    return;
                }
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                setViewEnabled(viewGroup.getChildAt(i), enabled, excludes);
            }
        }
        view.setEnabled(enabled);
    }

    public static void runOnUiThread(Runnable runnable) {
        UtilsBridge.runOnUiThread(runnable);
    }

    public static void runOnUiThreadDelayed(Runnable runnable, long delayMillis) {
        UtilsBridge.runOnUiThreadDelayed(runnable, delayMillis);
    }

    public static boolean isLayoutRtl() {
        Locale primaryLocale = Utils.getApp().getResources().getConfiguration().getLocales().get(0);
        return TextUtils.getLayoutDirectionFromLocale(primaryLocale) == 1;
    }

    public static void fixScrollViewTopping(View view) {
        view.setFocusable(false);
        ViewGroup viewGroup = null;
        if (view instanceof ViewGroup) {
            viewGroup = (ViewGroup) view;
        }
        if (viewGroup == null) {
            return;
        }
        int n = viewGroup.getChildCount();
        for (int i = 0; i < n; i++) {
            View childAt = viewGroup.getChildAt(i);
            childAt.setFocusable(false);
            if (childAt instanceof ViewGroup) {
                fixScrollViewTopping(childAt);
            }
        }
    }

    public static View layoutId2View(int layoutId) {
        LayoutInflater inflate = (LayoutInflater) Utils.getApp().getSystemService("layout_inflater");
        return inflate.inflate(layoutId, (ViewGroup) null);
    }
}

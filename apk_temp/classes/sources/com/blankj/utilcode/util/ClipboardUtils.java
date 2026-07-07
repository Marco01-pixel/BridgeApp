package com.blankj.utilcode.util;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class ClipboardUtils {
    private ClipboardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void copyText(CharSequence text) {
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService("clipboard");
        cm.setPrimaryClip(ClipData.newPlainText(Utils.getApp().getPackageName(), text));
    }

    public static void copyText(CharSequence label, CharSequence text) {
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService("clipboard");
        cm.setPrimaryClip(ClipData.newPlainText(label, text));
    }

    public static void clear() {
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService("clipboard");
        cm.setPrimaryClip(ClipData.newPlainText(null, ""));
    }

    public static CharSequence getLabel() {
        CharSequence label;
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService("clipboard");
        ClipDescription des = cm.getPrimaryClipDescription();
        if (des == null || (label = des.getLabel()) == null) {
            return "";
        }
        return label;
    }

    public static CharSequence getText() {
        CharSequence text;
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService("clipboard");
        ClipData clip = cm.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0 && (text = clip.getItemAt(0).coerceToText(Utils.getApp())) != null) {
            return text;
        }
        return "";
    }

    public static void addChangedListener(ClipboardManager.OnPrimaryClipChangedListener listener) {
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService("clipboard");
        cm.addPrimaryClipChangedListener(listener);
    }

    public static void removeChangedListener(ClipboardManager.OnPrimaryClipChangedListener listener) {
        ClipboardManager cm = (ClipboardManager) Utils.getApp().getSystemService("clipboard");
        cm.removePrimaryClipChangedListener(listener);
    }
}

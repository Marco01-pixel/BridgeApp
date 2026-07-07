package com.blankj.utilcode.util;

import android.media.AudioManager;
import android.os.Build;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public class VolumeUtils {
    public static int getVolume(int streamType) {
        AudioManager am = (AudioManager) Utils.getApp().getSystemService("audio");
        return am.getStreamVolume(streamType);
    }

    public static void setVolume(int streamType, int volume, int flags) {
        AudioManager am = (AudioManager) Utils.getApp().getSystemService("audio");
        try {
            am.setStreamVolume(streamType, volume, flags);
        } catch (SecurityException e) {
        }
    }

    public static int getMaxVolume(int streamType) {
        AudioManager am = (AudioManager) Utils.getApp().getSystemService("audio");
        return am.getStreamMaxVolume(streamType);
    }

    public static int getMinVolume(int streamType) {
        AudioManager am = (AudioManager) Utils.getApp().getSystemService("audio");
        if (Build.VERSION.SDK_INT >= 28) {
            return am.getStreamMinVolume(streamType);
        }
        return 0;
    }
}

package com.blankj.utilcode.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class CacheDiskStaticUtils {
    private static CacheDiskUtils sDefaultCacheDiskUtils;

    public static void setDefaultCacheDiskUtils(CacheDiskUtils cacheDiskUtils) {
        sDefaultCacheDiskUtils = cacheDiskUtils;
    }

    public static void put(String key, byte[] value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    public static void put(String key, byte[] value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    public static byte[] getBytes(String key) {
        return getBytes(key, getDefaultCacheDiskUtils());
    }

    public static byte[] getBytes(String key, byte[] defaultValue) {
        return getBytes(key, defaultValue, getDefaultCacheDiskUtils());
    }

    public static void put(String key, String value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    public static void put(String key, String value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    public static String getString(String key) {
        return getString(key, getDefaultCacheDiskUtils());
    }

    public static String getString(String key, String defaultValue) {
        return getString(key, defaultValue, getDefaultCacheDiskUtils());
    }

    public static void put(String key, JSONObject value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    public static void put(String key, JSONObject value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    public static JSONObject getJSONObject(String key) {
        return getJSONObject(key, getDefaultCacheDiskUtils());
    }

    public static JSONObject getJSONObject(String key, JSONObject defaultValue) {
        return getJSONObject(key, defaultValue, getDefaultCacheDiskUtils());
    }

    public static void put(String key, JSONArray value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    public static void put(String key, JSONArray value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    public static JSONArray getJSONArray(String key) {
        return getJSONArray(key, getDefaultCacheDiskUtils());
    }

    public static JSONArray getJSONArray(String key, JSONArray defaultValue) {
        return getJSONArray(key, defaultValue, getDefaultCacheDiskUtils());
    }

    public static void put(String key, Bitmap value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    public static void put(String key, Bitmap value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    public static Bitmap getBitmap(String key) {
        return getBitmap(key, getDefaultCacheDiskUtils());
    }

    public static Bitmap getBitmap(String key, Bitmap defaultValue) {
        return getBitmap(key, defaultValue, getDefaultCacheDiskUtils());
    }

    public static void put(String key, Drawable value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    public static void put(String key, Drawable value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    public static Drawable getDrawable(String key) {
        return getDrawable(key, getDefaultCacheDiskUtils());
    }

    public static Drawable getDrawable(String key, Drawable defaultValue) {
        return getDrawable(key, defaultValue, getDefaultCacheDiskUtils());
    }

    public static void put(String key, Parcelable value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    public static void put(String key, Parcelable value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    public static <T> T getParcelable(String str, Parcelable.Creator<T> creator) {
        return (T) getParcelable(str, (Parcelable.Creator) creator, getDefaultCacheDiskUtils());
    }

    public static <T> T getParcelable(String str, Parcelable.Creator<T> creator, T t) {
        return (T) getParcelable(str, creator, t, getDefaultCacheDiskUtils());
    }

    public static void put(String key, Serializable value) {
        put(key, value, getDefaultCacheDiskUtils());
    }

    public static void put(String key, Serializable value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDiskUtils());
    }

    public static Object getSerializable(String key) {
        return getSerializable(key, getDefaultCacheDiskUtils());
    }

    public static Object getSerializable(String key, Object defaultValue) {
        return getSerializable(key, defaultValue, getDefaultCacheDiskUtils());
    }

    public static long getCacheSize() {
        return getCacheSize(getDefaultCacheDiskUtils());
    }

    public static int getCacheCount() {
        return getCacheCount(getDefaultCacheDiskUtils());
    }

    public static boolean remove(String key) {
        return remove(key, getDefaultCacheDiskUtils());
    }

    public static boolean clear() {
        return clear(getDefaultCacheDiskUtils());
    }

    public static void put(String key, byte[] value, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    public static void put(String key, byte[] value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    public static byte[] getBytes(String key, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getBytes(key);
    }

    public static byte[] getBytes(String key, byte[] defaultValue, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getBytes(key, defaultValue);
    }

    public static void put(String key, String value, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    public static void put(String key, String value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    public static String getString(String key, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getString(key);
    }

    public static String getString(String key, String defaultValue, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getString(key, defaultValue);
    }

    public static void put(String key, JSONObject value, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    public static void put(String key, JSONObject value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    public static JSONObject getJSONObject(String key, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getJSONObject(key);
    }

    public static JSONObject getJSONObject(String key, JSONObject defaultValue, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getJSONObject(key, defaultValue);
    }

    public static void put(String key, JSONArray value, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    public static void put(String key, JSONArray value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    public static JSONArray getJSONArray(String key, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getJSONArray(key);
    }

    public static JSONArray getJSONArray(String key, JSONArray defaultValue, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getJSONArray(key, defaultValue);
    }

    public static void put(String key, Bitmap value, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    public static void put(String key, Bitmap value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    public static Bitmap getBitmap(String key, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getBitmap(key);
    }

    public static Bitmap getBitmap(String key, Bitmap defaultValue, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getBitmap(key, defaultValue);
    }

    public static void put(String key, Drawable value, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    public static void put(String key, Drawable value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    public static Drawable getDrawable(String key, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getDrawable(key);
    }

    public static Drawable getDrawable(String key, Drawable defaultValue, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getDrawable(key, defaultValue);
    }

    public static void put(String key, Parcelable value, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    public static void put(String key, Parcelable value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    public static <T> T getParcelable(String str, Parcelable.Creator<T> creator, CacheDiskUtils cacheDiskUtils) {
        return (T) cacheDiskUtils.getParcelable(str, creator);
    }

    public static <T> T getParcelable(String str, Parcelable.Creator<T> creator, T t, CacheDiskUtils cacheDiskUtils) {
        return (T) cacheDiskUtils.getParcelable(str, creator, t);
    }

    public static void put(String key, Serializable value, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value);
    }

    public static void put(String key, Serializable value, int saveTime, CacheDiskUtils cacheDiskUtils) {
        cacheDiskUtils.put(key, value, saveTime);
    }

    public static Object getSerializable(String key, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getSerializable(key);
    }

    public static Object getSerializable(String key, Object defaultValue, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getSerializable(key, defaultValue);
    }

    public static long getCacheSize(CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getCacheSize();
    }

    public static int getCacheCount(CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.getCacheCount();
    }

    public static boolean remove(String key, CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.remove(key);
    }

    public static boolean clear(CacheDiskUtils cacheDiskUtils) {
        return cacheDiskUtils.clear();
    }

    private static CacheDiskUtils getDefaultCacheDiskUtils() {
        CacheDiskUtils cacheDiskUtils = sDefaultCacheDiskUtils;
        return cacheDiskUtils != null ? cacheDiskUtils : CacheDiskUtils.getInstance();
    }
}

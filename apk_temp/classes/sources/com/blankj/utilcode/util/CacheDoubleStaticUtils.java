package com.blankj.utilcode.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class CacheDoubleStaticUtils {
    private static CacheDoubleUtils sDefaultCacheDoubleUtils;

    public static void setDefaultCacheDoubleUtils(CacheDoubleUtils cacheDoubleUtils) {
        sDefaultCacheDoubleUtils = cacheDoubleUtils;
    }

    public static void put(String key, byte[] value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, byte[] value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    public static byte[] getBytes(String key) {
        return getBytes(key, getDefaultCacheDoubleUtils());
    }

    public static byte[] getBytes(String key, byte[] defaultValue) {
        return getBytes(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, String value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, String value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    public static String getString(String key) {
        return getString(key, getDefaultCacheDoubleUtils());
    }

    public static String getString(String key, String defaultValue) {
        return getString(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, JSONObject value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, JSONObject value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    public static JSONObject getJSONObject(String key) {
        return getJSONObject(key, getDefaultCacheDoubleUtils());
    }

    public static JSONObject getJSONObject(String key, JSONObject defaultValue) {
        return getJSONObject(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, JSONArray value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, JSONArray value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    public static JSONArray getJSONArray(String key) {
        return getJSONArray(key, getDefaultCacheDoubleUtils());
    }

    public static JSONArray getJSONArray(String key, JSONArray defaultValue) {
        return getJSONArray(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, Bitmap value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, Bitmap value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    public static Bitmap getBitmap(String key) {
        return getBitmap(key, getDefaultCacheDoubleUtils());
    }

    public static Bitmap getBitmap(String key, Bitmap defaultValue) {
        return getBitmap(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, Drawable value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, Drawable value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    public static Drawable getDrawable(String key) {
        return getDrawable(key, getDefaultCacheDoubleUtils());
    }

    public static Drawable getDrawable(String key, Drawable defaultValue) {
        return getDrawable(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, Parcelable value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, Parcelable value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    public static <T> T getParcelable(String str, Parcelable.Creator<T> creator) {
        return (T) getParcelable(str, (Parcelable.Creator) creator, getDefaultCacheDoubleUtils());
    }

    public static <T> T getParcelable(String str, Parcelable.Creator<T> creator, T t) {
        return (T) getParcelable(str, creator, t, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, Serializable value) {
        put(key, value, getDefaultCacheDoubleUtils());
    }

    public static void put(String key, Serializable value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheDoubleUtils());
    }

    public static Object getSerializable(String key) {
        return getSerializable(key, getDefaultCacheDoubleUtils());
    }

    public static Object getSerializable(String key, Object defaultValue) {
        return getSerializable(key, defaultValue, getDefaultCacheDoubleUtils());
    }

    public static long getCacheDiskSize() {
        return getCacheDiskSize(getDefaultCacheDoubleUtils());
    }

    public static int getCacheDiskCount() {
        return getCacheDiskCount(getDefaultCacheDoubleUtils());
    }

    public static int getCacheMemoryCount() {
        return getCacheMemoryCount(getDefaultCacheDoubleUtils());
    }

    public static void remove(String key) {
        remove(key, getDefaultCacheDoubleUtils());
    }

    public static void clear() {
        clear(getDefaultCacheDoubleUtils());
    }

    public static void put(String key, byte[] value, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    public static void put(String key, byte[] value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    public static byte[] getBytes(String key, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getBytes(key);
    }

    public static byte[] getBytes(String key, byte[] defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getBytes(key, defaultValue);
    }

    public static void put(String key, String value, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    public static void put(String key, String value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    public static String getString(String key, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getString(key);
    }

    public static String getString(String key, String defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getString(key, defaultValue);
    }

    public static void put(String key, JSONObject value, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    public static void put(String key, JSONObject value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    public static JSONObject getJSONObject(String key, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getJSONObject(key);
    }

    public static JSONObject getJSONObject(String key, JSONObject defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getJSONObject(key, defaultValue);
    }

    public static void put(String key, JSONArray value, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    public static void put(String key, JSONArray value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    public static JSONArray getJSONArray(String key, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getJSONArray(key);
    }

    public static JSONArray getJSONArray(String key, JSONArray defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getJSONArray(key, defaultValue);
    }

    public static void put(String key, Bitmap value, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    public static void put(String key, Bitmap value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    public static Bitmap getBitmap(String key, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getBitmap(key);
    }

    public static Bitmap getBitmap(String key, Bitmap defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getBitmap(key, defaultValue);
    }

    public static void put(String key, Drawable value, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    public static void put(String key, Drawable value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    public static Drawable getDrawable(String key, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getDrawable(key);
    }

    public static Drawable getDrawable(String key, Drawable defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getDrawable(key, defaultValue);
    }

    public static void put(String key, Parcelable value, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    public static void put(String key, Parcelable value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    public static <T> T getParcelable(String str, Parcelable.Creator<T> creator, CacheDoubleUtils cacheDoubleUtils) {
        return (T) cacheDoubleUtils.getParcelable(str, creator);
    }

    public static <T> T getParcelable(String str, Parcelable.Creator<T> creator, T t, CacheDoubleUtils cacheDoubleUtils) {
        return (T) cacheDoubleUtils.getParcelable(str, creator, t);
    }

    public static void put(String key, Serializable value, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value);
    }

    public static void put(String key, Serializable value, int saveTime, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.put(key, value, saveTime);
    }

    public static Object getSerializable(String key, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getSerializable(key);
    }

    public static Object getSerializable(String key, Object defaultValue, CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getSerializable(key, defaultValue);
    }

    public static long getCacheDiskSize(CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getCacheDiskSize();
    }

    public static int getCacheDiskCount(CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getCacheDiskCount();
    }

    public static int getCacheMemoryCount(CacheDoubleUtils cacheDoubleUtils) {
        return cacheDoubleUtils.getCacheMemoryCount();
    }

    public static void remove(String key, CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.remove(key);
    }

    public static void clear(CacheDoubleUtils cacheDoubleUtils) {
        cacheDoubleUtils.clear();
    }

    private static CacheDoubleUtils getDefaultCacheDoubleUtils() {
        CacheDoubleUtils cacheDoubleUtils = sDefaultCacheDoubleUtils;
        return cacheDoubleUtils != null ? cacheDoubleUtils : CacheDoubleUtils.getInstance();
    }
}

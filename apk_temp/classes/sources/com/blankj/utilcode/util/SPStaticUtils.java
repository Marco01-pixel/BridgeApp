package com.blankj.utilcode.util;

import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class SPStaticUtils {
    private static SPUtils sDefaultSPUtils;

    public static void setDefaultSPUtils(SPUtils spUtils) {
        sDefaultSPUtils = spUtils;
    }

    public static void put(String key, String value) {
        put(key, value, getDefaultSPUtils());
    }

    public static void put(String key, String value, boolean isCommit) {
        put(key, value, isCommit, getDefaultSPUtils());
    }

    public static String getString(String key) {
        return getString(key, getDefaultSPUtils());
    }

    public static String getString(String key, String defaultValue) {
        return getString(key, defaultValue, getDefaultSPUtils());
    }

    public static void put(String key, int value) {
        put(key, value, getDefaultSPUtils());
    }

    public static void put(String key, int value, boolean isCommit) {
        put(key, value, isCommit, getDefaultSPUtils());
    }

    public static int getInt(String key) {
        return getInt(key, getDefaultSPUtils());
    }

    public static int getInt(String key, int defaultValue) {
        return getInt(key, defaultValue, getDefaultSPUtils());
    }

    public static void put(String key, long value) {
        put(key, value, getDefaultSPUtils());
    }

    public static void put(String key, long value, boolean isCommit) {
        put(key, value, isCommit, getDefaultSPUtils());
    }

    public static long getLong(String key) {
        return getLong(key, getDefaultSPUtils());
    }

    public static long getLong(String key, long defaultValue) {
        return getLong(key, defaultValue, getDefaultSPUtils());
    }

    public static void put(String key, float value) {
        put(key, value, getDefaultSPUtils());
    }

    public static void put(String key, float value, boolean isCommit) {
        put(key, value, isCommit, getDefaultSPUtils());
    }

    public static float getFloat(String key) {
        return getFloat(key, getDefaultSPUtils());
    }

    public static float getFloat(String key, float defaultValue) {
        return getFloat(key, defaultValue, getDefaultSPUtils());
    }

    public static void put(String key, boolean value) {
        put(key, value, getDefaultSPUtils());
    }

    public static void put(String key, boolean value, boolean isCommit) {
        put(key, value, isCommit, getDefaultSPUtils());
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, getDefaultSPUtils());
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key, defaultValue, getDefaultSPUtils());
    }

    public static void put(String key, Set<String> value) {
        put(key, value, getDefaultSPUtils());
    }

    public static void put(String key, Set<String> value, boolean isCommit) {
        put(key, value, isCommit, getDefaultSPUtils());
    }

    public static Set<String> getStringSet(String key) {
        return getStringSet(key, getDefaultSPUtils());
    }

    public static Set<String> getStringSet(String key, Set<String> defaultValue) {
        return getStringSet(key, defaultValue, getDefaultSPUtils());
    }

    public static Map<String, ?> getAll() {
        return getAll(getDefaultSPUtils());
    }

    public static boolean contains(String key) {
        return contains(key, getDefaultSPUtils());
    }

    public static void remove(String key) {
        remove(key, getDefaultSPUtils());
    }

    public static void remove(String key, boolean isCommit) {
        remove(key, isCommit, getDefaultSPUtils());
    }

    public static void clear() {
        clear(getDefaultSPUtils());
    }

    public static void clear(boolean isCommit) {
        clear(isCommit, getDefaultSPUtils());
    }

    public static void put(String key, String value, SPUtils spUtils) {
        spUtils.put(key, value);
    }

    public static void put(String key, String value, boolean isCommit, SPUtils spUtils) {
        spUtils.put(key, value, isCommit);
    }

    public static String getString(String key, SPUtils spUtils) {
        return spUtils.getString(key);
    }

    public static String getString(String key, String defaultValue, SPUtils spUtils) {
        return spUtils.getString(key, defaultValue);
    }

    public static void put(String key, int value, SPUtils spUtils) {
        spUtils.put(key, value);
    }

    public static void put(String key, int value, boolean isCommit, SPUtils spUtils) {
        spUtils.put(key, value, isCommit);
    }

    public static int getInt(String key, SPUtils spUtils) {
        return spUtils.getInt(key);
    }

    public static int getInt(String key, int defaultValue, SPUtils spUtils) {
        return spUtils.getInt(key, defaultValue);
    }

    public static void put(String key, long value, SPUtils spUtils) {
        spUtils.put(key, value);
    }

    public static void put(String key, long value, boolean isCommit, SPUtils spUtils) {
        spUtils.put(key, value, isCommit);
    }

    public static long getLong(String key, SPUtils spUtils) {
        return spUtils.getLong(key);
    }

    public static long getLong(String key, long defaultValue, SPUtils spUtils) {
        return spUtils.getLong(key, defaultValue);
    }

    public static void put(String key, float value, SPUtils spUtils) {
        spUtils.put(key, value);
    }

    public static void put(String key, float value, boolean isCommit, SPUtils spUtils) {
        spUtils.put(key, value, isCommit);
    }

    public static float getFloat(String key, SPUtils spUtils) {
        return spUtils.getFloat(key);
    }

    public static float getFloat(String key, float defaultValue, SPUtils spUtils) {
        return spUtils.getFloat(key, defaultValue);
    }

    public static void put(String key, boolean value, SPUtils spUtils) {
        spUtils.put(key, value);
    }

    public static void put(String key, boolean value, boolean isCommit, SPUtils spUtils) {
        spUtils.put(key, value, isCommit);
    }

    public static boolean getBoolean(String key, SPUtils spUtils) {
        return spUtils.getBoolean(key);
    }

    public static boolean getBoolean(String key, boolean defaultValue, SPUtils spUtils) {
        return spUtils.getBoolean(key, defaultValue);
    }

    public static void put(String key, Set<String> value, SPUtils spUtils) {
        spUtils.put(key, value);
    }

    public static void put(String key, Set<String> value, boolean isCommit, SPUtils spUtils) {
        spUtils.put(key, value, isCommit);
    }

    public static Set<String> getStringSet(String key, SPUtils spUtils) {
        return spUtils.getStringSet(key);
    }

    public static Set<String> getStringSet(String key, Set<String> defaultValue, SPUtils spUtils) {
        return spUtils.getStringSet(key, defaultValue);
    }

    public static Map<String, ?> getAll(SPUtils spUtils) {
        return spUtils.getAll();
    }

    public static boolean contains(String key, SPUtils spUtils) {
        return spUtils.contains(key);
    }

    public static void remove(String key, SPUtils spUtils) {
        spUtils.remove(key);
    }

    public static void remove(String key, boolean isCommit, SPUtils spUtils) {
        spUtils.remove(key, isCommit);
    }

    public static void clear(SPUtils spUtils) {
        spUtils.clear();
    }

    public static void clear(boolean isCommit, SPUtils spUtils) {
        spUtils.clear(isCommit);
    }

    private static SPUtils getDefaultSPUtils() {
        SPUtils sPUtils = sDefaultSPUtils;
        return sPUtils != null ? sPUtils : SPUtils.getInstance();
    }
}

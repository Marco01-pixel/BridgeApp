package com.blankj.utilcode.util;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class CacheMemoryStaticUtils {
    private static CacheMemoryUtils sDefaultCacheMemoryUtils;

    public static void setDefaultCacheMemoryUtils(CacheMemoryUtils cacheMemoryUtils) {
        sDefaultCacheMemoryUtils = cacheMemoryUtils;
    }

    public static void put(String key, Object value) {
        put(key, value, getDefaultCacheMemoryUtils());
    }

    public static void put(String key, Object value, int saveTime) {
        put(key, value, saveTime, getDefaultCacheMemoryUtils());
    }

    public static <T> T get(String str) {
        return (T) get(str, getDefaultCacheMemoryUtils());
    }

    public static <T> T get(String str, T t) {
        return (T) get(str, t, getDefaultCacheMemoryUtils());
    }

    public static int getCacheCount() {
        return getCacheCount(getDefaultCacheMemoryUtils());
    }

    public static Object remove(String key) {
        return remove(key, getDefaultCacheMemoryUtils());
    }

    public static void clear() {
        clear(getDefaultCacheMemoryUtils());
    }

    public static void put(String key, Object value, CacheMemoryUtils cacheMemoryUtils) {
        cacheMemoryUtils.put(key, value);
    }

    public static void put(String key, Object value, int saveTime, CacheMemoryUtils cacheMemoryUtils) {
        cacheMemoryUtils.put(key, value, saveTime);
    }

    public static <T> T get(String str, CacheMemoryUtils cacheMemoryUtils) {
        return (T) cacheMemoryUtils.get(str);
    }

    public static <T> T get(String str, T t, CacheMemoryUtils cacheMemoryUtils) {
        return (T) cacheMemoryUtils.get(str, t);
    }

    public static int getCacheCount(CacheMemoryUtils cacheMemoryUtils) {
        return cacheMemoryUtils.getCacheCount();
    }

    public static Object remove(String key, CacheMemoryUtils cacheMemoryUtils) {
        return cacheMemoryUtils.remove(key);
    }

    public static void clear(CacheMemoryUtils cacheMemoryUtils) {
        cacheMemoryUtils.clear();
    }

    private static CacheMemoryUtils getDefaultCacheMemoryUtils() {
        CacheMemoryUtils cacheMemoryUtils = sDefaultCacheMemoryUtils;
        return cacheMemoryUtils != null ? cacheMemoryUtils : CacheMemoryUtils.getInstance();
    }
}

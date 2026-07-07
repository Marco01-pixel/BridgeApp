package com.blankj.utilcode.util;

import androidx.collection.LruCache;
import com.blankj.utilcode.constant.CacheConstants;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class CacheMemoryUtils implements CacheConstants {
    private static final Map<String, CacheMemoryUtils> CACHE_MAP = new HashMap();
    private static final int DEFAULT_MAX_COUNT = 256;
    private final String mCacheKey;
    private final LruCache<String, CacheValue> mMemoryCache;

    public static CacheMemoryUtils getInstance() {
        return getInstance(256);
    }

    public static CacheMemoryUtils getInstance(int maxCount) {
        return getInstance(String.valueOf(maxCount), maxCount);
    }

    public static CacheMemoryUtils getInstance(String cacheKey, int maxCount) {
        Map<String, CacheMemoryUtils> map = CACHE_MAP;
        CacheMemoryUtils cache = map.get(cacheKey);
        if (cache == null) {
            synchronized (CacheMemoryUtils.class) {
                cache = map.get(cacheKey);
                if (cache == null) {
                    cache = new CacheMemoryUtils(cacheKey, new LruCache(maxCount));
                    map.put(cacheKey, cache);
                }
            }
        }
        return cache;
    }

    private CacheMemoryUtils(String cacheKey, LruCache<String, CacheValue> memoryCache) {
        this.mCacheKey = cacheKey;
        this.mMemoryCache = memoryCache;
    }

    public String toString() {
        return this.mCacheKey + "@" + Integer.toHexString(hashCode());
    }

    public void put(String key, Object value) {
        put(key, value, -1);
    }

    public void put(String key, Object value, int saveTime) {
        if (value == null) {
            return;
        }
        long dueTime = saveTime < 0 ? -1L : System.currentTimeMillis() + ((long) (saveTime * 1000));
        this.mMemoryCache.put(key, new CacheValue(dueTime, value));
    }

    public <T> T get(String str) {
        return (T) get(str, null);
    }

    public <T> T get(String str, T t) {
        CacheValue cacheValue = this.mMemoryCache.get(str);
        if (cacheValue == null) {
            return t;
        }
        if (cacheValue.dueTime == -1 || cacheValue.dueTime >= System.currentTimeMillis()) {
            return (T) cacheValue.value;
        }
        this.mMemoryCache.remove(str);
        return t;
    }

    public int getCacheCount() {
        return this.mMemoryCache.size();
    }

    public Object remove(String key) {
        CacheValue remove = this.mMemoryCache.remove(key);
        if (remove == null) {
            return null;
        }
        return remove.value;
    }

    public void clear() {
        this.mMemoryCache.evictAll();
    }

    private static final class CacheValue {
        long dueTime;
        Object value;

        CacheValue(long dueTime, Object value) {
            this.dueTime = dueTime;
            this.value = value;
        }
    }
}

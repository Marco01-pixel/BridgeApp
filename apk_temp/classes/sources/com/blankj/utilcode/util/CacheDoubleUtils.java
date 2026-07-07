package com.blankj.utilcode.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import com.blankj.utilcode.constant.CacheConstants;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class CacheDoubleUtils implements CacheConstants {
    private static final Map<String, CacheDoubleUtils> CACHE_MAP = new HashMap();
    private final CacheDiskUtils mCacheDiskUtils;
    private final CacheMemoryUtils mCacheMemoryUtils;

    public static CacheDoubleUtils getInstance() {
        return getInstance(CacheMemoryUtils.getInstance(), CacheDiskUtils.getInstance());
    }

    public static CacheDoubleUtils getInstance(CacheMemoryUtils cacheMemoryUtils, CacheDiskUtils cacheDiskUtils) {
        String cacheKey = cacheDiskUtils.toString() + "_" + cacheMemoryUtils.toString();
        Map<String, CacheDoubleUtils> map = CACHE_MAP;
        CacheDoubleUtils cache = map.get(cacheKey);
        if (cache == null) {
            synchronized (CacheDoubleUtils.class) {
                cache = map.get(cacheKey);
                if (cache == null) {
                    cache = new CacheDoubleUtils(cacheMemoryUtils, cacheDiskUtils);
                    map.put(cacheKey, cache);
                }
            }
        }
        return cache;
    }

    private CacheDoubleUtils(CacheMemoryUtils cacheMemoryUtils, CacheDiskUtils cacheUtils) {
        this.mCacheMemoryUtils = cacheMemoryUtils;
        this.mCacheDiskUtils = cacheUtils;
    }

    public void put(String key, byte[] value) {
        put(key, value, -1);
    }

    public void put(String key, byte[] value, int saveTime) {
        this.mCacheMemoryUtils.put(key, value, saveTime);
        this.mCacheDiskUtils.put(key, value, saveTime);
    }

    public byte[] getBytes(String key) {
        return getBytes(key, null);
    }

    public byte[] getBytes(String key, byte[] defaultValue) {
        byte[] obj = (byte[]) this.mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        byte[] bytes = this.mCacheDiskUtils.getBytes(key);
        if (bytes != null) {
            this.mCacheMemoryUtils.put(key, bytes);
            return bytes;
        }
        return defaultValue;
    }

    public void put(String key, String value) {
        put(key, value, -1);
    }

    public void put(String key, String value, int saveTime) {
        this.mCacheMemoryUtils.put(key, value, saveTime);
        this.mCacheDiskUtils.put(key, value, saveTime);
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        String obj = (String) this.mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        String string = this.mCacheDiskUtils.getString(key);
        if (string != null) {
            this.mCacheMemoryUtils.put(key, string);
            return string;
        }
        return defaultValue;
    }

    public void put(String key, JSONObject value) {
        put(key, value, -1);
    }

    public void put(String key, JSONObject value, int saveTime) {
        this.mCacheMemoryUtils.put(key, value, saveTime);
        this.mCacheDiskUtils.put(key, value, saveTime);
    }

    public JSONObject getJSONObject(String key) {
        return getJSONObject(key, null);
    }

    public JSONObject getJSONObject(String key, JSONObject defaultValue) {
        JSONObject obj = (JSONObject) this.mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        JSONObject jsonObject = this.mCacheDiskUtils.getJSONObject(key);
        if (jsonObject != null) {
            this.mCacheMemoryUtils.put(key, jsonObject);
            return jsonObject;
        }
        return defaultValue;
    }

    public void put(String key, JSONArray value) {
        put(key, value, -1);
    }

    public void put(String key, JSONArray value, int saveTime) {
        this.mCacheMemoryUtils.put(key, value, saveTime);
        this.mCacheDiskUtils.put(key, value, saveTime);
    }

    public JSONArray getJSONArray(String key) {
        return getJSONArray(key, null);
    }

    public JSONArray getJSONArray(String key, JSONArray defaultValue) {
        JSONArray obj = (JSONArray) this.mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        JSONArray jsonArray = this.mCacheDiskUtils.getJSONArray(key);
        if (jsonArray != null) {
            this.mCacheMemoryUtils.put(key, jsonArray);
            return jsonArray;
        }
        return defaultValue;
    }

    public void put(String key, Bitmap value) {
        put(key, value, -1);
    }

    public void put(String key, Bitmap value, int saveTime) {
        this.mCacheMemoryUtils.put(key, value, saveTime);
        this.mCacheDiskUtils.put(key, value, saveTime);
    }

    public Bitmap getBitmap(String key) {
        return getBitmap(key, null);
    }

    public Bitmap getBitmap(String key, Bitmap defaultValue) {
        Bitmap obj = (Bitmap) this.mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        Bitmap bitmap = this.mCacheDiskUtils.getBitmap(key);
        if (bitmap != null) {
            this.mCacheMemoryUtils.put(key, bitmap);
            return bitmap;
        }
        return defaultValue;
    }

    public void put(String key, Drawable value) {
        put(key, value, -1);
    }

    public void put(String key, Drawable value, int saveTime) {
        this.mCacheMemoryUtils.put(key, value, saveTime);
        this.mCacheDiskUtils.put(key, value, saveTime);
    }

    public Drawable getDrawable(String key) {
        return getDrawable(key, null);
    }

    public Drawable getDrawable(String key, Drawable defaultValue) {
        Drawable obj = (Drawable) this.mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        Drawable drawable = this.mCacheDiskUtils.getDrawable(key);
        if (drawable != null) {
            this.mCacheMemoryUtils.put(key, drawable);
            return drawable;
        }
        return defaultValue;
    }

    public void put(String key, Parcelable value) {
        put(key, value, -1);
    }

    public void put(String key, Parcelable value, int saveTime) {
        this.mCacheMemoryUtils.put(key, value, saveTime);
        this.mCacheDiskUtils.put(key, value, saveTime);
    }

    public <T> T getParcelable(String str, Parcelable.Creator<T> creator) {
        return (T) getParcelable(str, creator, null);
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public <T> T getParcelable(String str, Parcelable.Creator<T> creator, T t) {
        T t2 = (T) this.mCacheMemoryUtils.get(str);
        if (t2 != null) {
            return t2;
        }
        T t3 = (T) this.mCacheDiskUtils.getParcelable(str, creator);
        if (t3 != null) {
            this.mCacheMemoryUtils.put(str, t3);
            return t3;
        }
        return t;
    }

    public void put(String key, Serializable value) {
        put(key, value, -1);
    }

    public void put(String key, Serializable value, int saveTime) {
        this.mCacheMemoryUtils.put(key, value, saveTime);
        this.mCacheDiskUtils.put(key, value, saveTime);
    }

    public Object getSerializable(String key) {
        return getSerializable(key, null);
    }

    public Object getSerializable(String key, Object defaultValue) {
        Object obj = this.mCacheMemoryUtils.get(key);
        if (obj != null) {
            return obj;
        }
        Object serializable = this.mCacheDiskUtils.getSerializable(key);
        if (serializable != null) {
            this.mCacheMemoryUtils.put(key, serializable);
            return serializable;
        }
        return defaultValue;
    }

    public long getCacheDiskSize() {
        return this.mCacheDiskUtils.getCacheSize();
    }

    public int getCacheDiskCount() {
        return this.mCacheDiskUtils.getCacheCount();
    }

    public int getCacheMemoryCount() {
        return this.mCacheMemoryUtils.getCacheCount();
    }

    public void remove(String key) {
        this.mCacheMemoryUtils.remove(key);
        this.mCacheDiskUtils.remove(key);
    }

    public void clear() {
        this.mCacheMemoryUtils.clear();
        this.mCacheDiskUtils.clear();
    }
}

package com.blankj.utilcode.util;

import android.content.SharedPreferences;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class SPUtils {
    private static final Map<String, SPUtils> SP_UTILS_MAP = new HashMap();
    private SharedPreferences sp;

    public static SPUtils getInstance() {
        return getInstance("", 0);
    }

    public static SPUtils getInstance(int mode) {
        return getInstance("", mode);
    }

    public static SPUtils getInstance(String spName) {
        return getInstance(spName, 0);
    }

    public static SPUtils getInstance(String spName, int mode) {
        if (isSpace(spName)) {
            spName = "spUtils";
        }
        Map<String, SPUtils> map = SP_UTILS_MAP;
        SPUtils spUtils = map.get(spName);
        if (spUtils == null) {
            synchronized (SPUtils.class) {
                spUtils = map.get(spName);
                if (spUtils == null) {
                    spUtils = new SPUtils(spName, mode);
                    map.put(spName, spUtils);
                }
            }
        }
        return spUtils;
    }

    private SPUtils(String spName) {
        this.sp = Utils.getApp().getSharedPreferences(spName, 0);
    }

    private SPUtils(String spName, int mode) {
        this.sp = Utils.getApp().getSharedPreferences(spName, mode);
    }

    public void put(String key, String value) {
        put(key, value, false);
    }

    public void put(String key, String value, boolean isCommit) {
        if (isCommit) {
            this.sp.edit().putString(key, value).commit();
        } else {
            this.sp.edit().putString(key, value).apply();
        }
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return this.sp.getString(key, defaultValue);
    }

    public void put(String key, int value) {
        put(key, value, false);
    }

    public void put(String key, int value, boolean isCommit) {
        if (isCommit) {
            this.sp.edit().putInt(key, value).commit();
        } else {
            this.sp.edit().putInt(key, value).apply();
        }
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }

    public int getInt(String key, int defaultValue) {
        return this.sp.getInt(key, defaultValue);
    }

    public void put(String key, long value) {
        put(key, value, false);
    }

    public void put(String key, long value, boolean isCommit) {
        if (isCommit) {
            this.sp.edit().putLong(key, value).commit();
        } else {
            this.sp.edit().putLong(key, value).apply();
        }
    }

    public long getLong(String key) {
        return getLong(key, -1L);
    }

    public long getLong(String key, long defaultValue) {
        return this.sp.getLong(key, defaultValue);
    }

    public void put(String key, float value) {
        put(key, value, false);
    }

    public void put(String key, float value, boolean isCommit) {
        if (isCommit) {
            this.sp.edit().putFloat(key, value).commit();
        } else {
            this.sp.edit().putFloat(key, value).apply();
        }
    }

    public float getFloat(String key) {
        return getFloat(key, -1.0f);
    }

    public float getFloat(String key, float defaultValue) {
        return this.sp.getFloat(key, defaultValue);
    }

    public void put(String key, boolean value) {
        put(key, value, false);
    }

    public void put(String key, boolean value, boolean isCommit) {
        if (isCommit) {
            this.sp.edit().putBoolean(key, value).commit();
        } else {
            this.sp.edit().putBoolean(key, value).apply();
        }
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.sp.getBoolean(key, defaultValue);
    }

    public void put(String key, Set<String> value) {
        put(key, value, false);
    }

    public void put(String key, Set<String> value, boolean isCommit) {
        if (isCommit) {
            this.sp.edit().putStringSet(key, value).commit();
        } else {
            this.sp.edit().putStringSet(key, value).apply();
        }
    }

    public Set<String> getStringSet(String key) {
        return getStringSet(key, Collections.emptySet());
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return this.sp.getStringSet(key, defaultValue);
    }

    public Map<String, ?> getAll() {
        return this.sp.getAll();
    }

    public boolean contains(String key) {
        return this.sp.contains(key);
    }

    public void remove(String key) {
        remove(key, false);
    }

    public void remove(String key, boolean isCommit) {
        if (isCommit) {
            this.sp.edit().remove(key).commit();
        } else {
            this.sp.edit().remove(key).apply();
        }
    }

    public void clear() {
        clear(false);
    }

    public void clear(boolean isCommit) {
        if (isCommit) {
            this.sp.edit().clear().commit();
        } else {
            this.sp.edit().clear().apply();
        }
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}

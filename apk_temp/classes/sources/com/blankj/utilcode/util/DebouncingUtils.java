package com.blankj.utilcode.util;

import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public class DebouncingUtils {
    private static final int CACHE_SIZE = 64;
    private static final long DEBOUNCING_DEFAULT_VALUE = 1000;
    private static final Map<String, Long> KEY_MILLIS_MAP = new ConcurrentHashMap(64);

    private DebouncingUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isValid(View view) {
        return isValid(view, DEBOUNCING_DEFAULT_VALUE);
    }

    public static boolean isValid(View view, long duration) {
        return isValid(String.valueOf(view.hashCode()), duration);
    }

    public static boolean isValid(String key, long duration) {
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("The key is null.");
        }
        if (duration < 0) {
            throw new IllegalArgumentException("The duration is less than 0.");
        }
        long curTime = SystemClock.elapsedRealtime();
        clearIfNecessary(curTime);
        Map<String, Long> map = KEY_MILLIS_MAP;
        Long validTime = map.get(key);
        if (validTime == null || curTime >= validTime.longValue()) {
            map.put(key, Long.valueOf(curTime + duration));
            return true;
        }
        return false;
    }

    private static void clearIfNecessary(long curTime) {
        Map<String, Long> map = KEY_MILLIS_MAP;
        if (map.size() < 64) {
            return;
        }
        Iterator<Map.Entry<String, Long>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> entry = it.next();
            Long validTime = entry.getValue();
            if (curTime >= validTime.longValue()) {
                it.remove();
            }
        }
    }
}

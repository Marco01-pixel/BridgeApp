package com.blankj.utilcode.util;

import android.util.Log;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class ApiUtils {
    private static final String TAG = "ApiUtils";
    private Map<Class, BaseApi> mApiMap;
    private Map<Class, Class> mInjectApiImplMap;

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.CLASS)
    public @interface Api {
        boolean isMock() default false;
    }

    public static class BaseApi {
    }

    private ApiUtils() {
        this.mApiMap = new ConcurrentHashMap();
        this.mInjectApiImplMap = new HashMap();
        init();
    }

    private void init() {
    }

    private void registerImpl(Class implClass) {
        this.mInjectApiImplMap.put(implClass.getSuperclass(), implClass);
    }

    public static <T extends BaseApi> T getApi(Class<T> apiClass) {
        return (T) getInstance().getApiInner(apiClass);
    }

    public static void register(Class<? extends BaseApi> implClass) {
        if (implClass == null) {
            return;
        }
        getInstance().registerImpl(implClass);
    }

    public static String toString_() {
        return getInstance().toString();
    }

    public String toString() {
        return "ApiUtils: " + this.mInjectApiImplMap;
    }

    private static ApiUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <Result> Result getApiInner(Class cls) {
        Result result = (Result) ((BaseApi) this.mApiMap.get(cls));
        if (result != null) {
            return result;
        }
        synchronized (cls) {
            Result result2 = (Result) ((BaseApi) this.mApiMap.get(cls));
            if (result2 != null) {
                return result2;
            }
            Class cls2 = this.mInjectApiImplMap.get(cls);
            if (cls2 != null) {
                try {
                    Result result3 = (Result) ((BaseApi) cls2.newInstance());
                    this.mApiMap.put(cls, (BaseApi) result3);
                    return result3;
                } catch (Exception e) {
                    Log.e(TAG, "The <" + cls2 + "> has no parameterless constructor.");
                    return null;
                }
            }
            Log.e(TAG, "The <" + cls + "> doesn't implement.");
            return null;
        }
    }

    private static class LazyHolder {
        private static final ApiUtils INSTANCE = new ApiUtils();

        private LazyHolder() {
        }
    }
}

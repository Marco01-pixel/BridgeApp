package com.blankj.utilcode.util;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class GsonUtils {
    private static final Map<String, Gson> GSONS = new ConcurrentHashMap();
    private static final String KEY_DEFAULT = "defaultGson";
    private static final String KEY_DELEGATE = "delegateGson";
    private static final String KEY_LOG_UTILS = "logUtilsGson";

    private GsonUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void setGsonDelegate(Gson delegate) {
        if (delegate == null) {
            return;
        }
        GSONS.put(KEY_DELEGATE, delegate);
    }

    public static void setGson(String key, Gson gson) {
        if (TextUtils.isEmpty(key) || gson == null) {
            return;
        }
        GSONS.put(key, gson);
    }

    public static Gson getGson(String key) {
        return GSONS.get(key);
    }

    public static Gson getGson() {
        Map<String, Gson> map = GSONS;
        Gson gsonDelegate = map.get(KEY_DELEGATE);
        if (gsonDelegate != null) {
            return gsonDelegate;
        }
        Gson gsonDefault = map.get(KEY_DEFAULT);
        if (gsonDefault == null) {
            Gson gsonDefault2 = createGson();
            map.put(KEY_DEFAULT, gsonDefault2);
            return gsonDefault2;
        }
        return gsonDefault;
    }

    public static String toJson(Object object) {
        return toJson(getGson(), object);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return toJson(getGson(), src, typeOfSrc);
    }

    public static String toJson(Gson gson, Object object) {
        return gson.toJson(object);
    }

    public static String toJson(Gson gson, Object src, Type typeOfSrc) {
        return gson.toJson(src, typeOfSrc);
    }

    public static <T> T fromJson(String str, Class<T> cls) {
        return (T) fromJson(getGson(), str, (Class) cls);
    }

    public static <T> T fromJson(String str, Type type) {
        return (T) fromJson(getGson(), str, type);
    }

    public static <T> T fromJson(Reader reader, Class<T> cls) {
        return (T) fromJson(getGson(), reader, (Class) cls);
    }

    public static <T> T fromJson(Reader reader, Type type) {
        return (T) fromJson(getGson(), reader, type);
    }

    public static <T> T fromJson(Gson gson, String str, Class<T> cls) {
        return (T) gson.fromJson(str, (Class) cls);
    }

    public static <T> T fromJson(Gson gson, String str, Type type) {
        return (T) gson.fromJson(str, type);
    }

    public static <T> T fromJson(Gson gson, Reader reader, Class<T> cls) {
        return (T) gson.fromJson(reader, (Class) cls);
    }

    public static <T> T fromJson(Gson gson, Reader reader, Type type) {
        return (T) gson.fromJson(reader, type);
    }

    public static Type getListType(Type type) {
        return TypeToken.getParameterized(List.class, type).getType();
    }

    public static Type getSetType(Type type) {
        return TypeToken.getParameterized(Set.class, type).getType();
    }

    public static Type getMapType(Type keyType, Type valueType) {
        return TypeToken.getParameterized(Map.class, keyType, valueType).getType();
    }

    public static Type getArrayType(Type type) {
        return TypeToken.getArray(type).getType();
    }

    public static Type getType(Type rawType, Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }

    static Gson getGson4LogUtils() {
        Map<String, Gson> map = GSONS;
        Gson gson4LogUtils = map.get(KEY_LOG_UTILS);
        if (gson4LogUtils == null) {
            Gson gson4LogUtils2 = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            map.put(KEY_LOG_UTILS, gson4LogUtils2);
            return gson4LogUtils2;
        }
        return gson4LogUtils;
    }

    private static Gson createGson() {
        return new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    }
}

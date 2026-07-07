package com.blankj.utilcode.util;

import android.graphics.drawable.Drawable;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class ResourceUtils {
    private static final int BUFFER_SIZE = 8192;

    private ResourceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Drawable getDrawable(int id) {
        return ContextCompat.getDrawable(Utils.getApp(), id);
    }

    public static int getIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "id", Utils.getApp().getPackageName());
    }

    public static int getStringIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, TypedValues.Custom.S_STRING, Utils.getApp().getPackageName());
    }

    public static int getColorIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, TypedValues.Custom.S_COLOR, Utils.getApp().getPackageName());
    }

    public static int getDimenIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "dimen", Utils.getApp().getPackageName());
    }

    public static int getDrawableIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "drawable", Utils.getApp().getPackageName());
    }

    public static int getMipmapIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "mipmap", Utils.getApp().getPackageName());
    }

    public static int getLayoutIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "layout", Utils.getApp().getPackageName());
    }

    public static int getStyleIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "style", Utils.getApp().getPackageName());
    }

    public static int getAnimIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "anim", Utils.getApp().getPackageName());
    }

    public static int getMenuIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "menu", Utils.getApp().getPackageName());
    }

    public static boolean copyFileFromAssets(String assetsFilePath, String destFilePath) {
        boolean res = true;
        try {
            String[] assets = Utils.getApp().getAssets().list(assetsFilePath);
            if (assets != null && assets.length > 0) {
                for (String asset : assets) {
                    res &= copyFileFromAssets(assetsFilePath + "/" + asset, destFilePath + "/" + asset);
                }
                return res;
            }
            boolean res2 = UtilsBridge.writeFileFromIS(destFilePath, Utils.getApp().getAssets().open(assetsFilePath));
            return res2;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readAssets2String(String assetsFilePath) {
        return readAssets2String(assetsFilePath, null);
    }

    public static String readAssets2String(String assetsFilePath, String charsetName) {
        try {
            InputStream is = Utils.getApp().getAssets().open(assetsFilePath);
            byte[] bytes = UtilsBridge.inputStream2Bytes(is);
            if (bytes == null) {
                return "";
            }
            if (UtilsBridge.isSpace(charsetName)) {
                return new String(bytes);
            }
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public static List<String> readAssets2List(String assetsPath) {
        return readAssets2List(assetsPath, "");
    }

    public static List<String> readAssets2List(String assetsPath, String charsetName) {
        try {
            return UtilsBridge.inputStream2Lines(Utils.getApp().getResources().getAssets().open(assetsPath), charsetName);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static boolean copyFileFromRaw(int resId, String destFilePath) {
        return UtilsBridge.writeFileFromIS(destFilePath, Utils.getApp().getResources().openRawResource(resId));
    }

    public static String readRaw2String(int resId) {
        return readRaw2String(resId, null);
    }

    public static String readRaw2String(int resId, String charsetName) {
        InputStream is = Utils.getApp().getResources().openRawResource(resId);
        byte[] bytes = UtilsBridge.inputStream2Bytes(is);
        if (bytes == null) {
            return null;
        }
        if (UtilsBridge.isSpace(charsetName)) {
            return new String(bytes);
        }
        try {
            return new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static List<String> readRaw2List(int resId) {
        return readRaw2List(resId, "");
    }

    public static List<String> readRaw2List(int resId, String charsetName) {
        return UtilsBridge.inputStream2Lines(Utils.getApp().getResources().openRawResource(resId), charsetName);
    }
}

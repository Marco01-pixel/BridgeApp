package com.blankj.utilcode.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class AdaptScreenUtils {
    private static List<Field> sMetricsFields;

    private AdaptScreenUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Resources adaptWidth(Resources resources, int designWidth) {
        float newXdpi = (resources.getDisplayMetrics().widthPixels * 72.0f) / designWidth;
        applyDisplayMetrics(resources, newXdpi);
        return resources;
    }

    public static Resources adaptHeight(Resources resources, int designHeight) {
        return adaptHeight(resources, designHeight, false);
    }

    public static Resources adaptHeight(Resources resources, int designHeight, boolean includeNavBar) {
        float screenHeight = (resources.getDisplayMetrics().heightPixels + (includeNavBar ? getNavBarHeight(resources) : 0)) * 72.0f;
        float newXdpi = screenHeight / designHeight;
        applyDisplayMetrics(resources, newXdpi);
        return resources;
    }

    private static int getNavBarHeight(Resources resources) {
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static Resources closeAdapt(Resources resources) {
        float newXdpi = Resources.getSystem().getDisplayMetrics().density * 72.0f;
        applyDisplayMetrics(resources, newXdpi);
        return resources;
    }

    public static int pt2Px(float ptValue) {
        DisplayMetrics metrics = Utils.getApp().getResources().getDisplayMetrics();
        return (int) (((double) ((metrics.xdpi * ptValue) / 72.0f)) + 0.5d);
    }

    public static int px2Pt(float pxValue) {
        DisplayMetrics metrics = Utils.getApp().getResources().getDisplayMetrics();
        return (int) (((double) ((72.0f * pxValue) / metrics.xdpi)) + 0.5d);
    }

    private static void applyDisplayMetrics(Resources resources, float newXdpi) {
        resources.getDisplayMetrics().xdpi = newXdpi;
        Utils.getApp().getResources().getDisplayMetrics().xdpi = newXdpi;
        applyOtherDisplayMetrics(resources, newXdpi);
    }

    static Runnable getPreLoadRunnable() {
        return new Runnable() { // from class: com.blankj.utilcode.util.AdaptScreenUtils.1
            @Override // java.lang.Runnable
            public void run() {
                AdaptScreenUtils.preLoad();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void preLoad() {
        applyDisplayMetrics(Resources.getSystem(), Resources.getSystem().getDisplayMetrics().xdpi);
    }

    private static void applyOtherDisplayMetrics(Resources resources, float newXdpi) {
        if (sMetricsFields == null) {
            sMetricsFields = new ArrayList();
            Class<?> superclass = resources.getClass();
            Field[] declaredFields = superclass.getDeclaredFields();
            while (declaredFields != null && declaredFields.length > 0) {
                for (Field field : declaredFields) {
                    if (field.getType().isAssignableFrom(DisplayMetrics.class)) {
                        field.setAccessible(true);
                        DisplayMetrics tmpDm = getMetricsFromField(resources, field);
                        if (tmpDm != null) {
                            sMetricsFields.add(field);
                            tmpDm.xdpi = newXdpi;
                        }
                    }
                }
                superclass = superclass.getSuperclass();
                if (superclass != null) {
                    declaredFields = superclass.getDeclaredFields();
                } else {
                    return;
                }
            }
            return;
        }
        applyMetricsFields(resources, newXdpi);
    }

    private static void applyMetricsFields(Resources resources, float newXdpi) {
        for (Field metricsField : sMetricsFields) {
            try {
                DisplayMetrics dm = (DisplayMetrics) metricsField.get(resources);
                if (dm != null) {
                    dm.xdpi = newXdpi;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static DisplayMetrics getMetricsFromField(Resources resources, Field field) {
        try {
            return (DisplayMetrics) field.get(resources);
        } catch (Exception e) {
            return null;
        }
    }
}

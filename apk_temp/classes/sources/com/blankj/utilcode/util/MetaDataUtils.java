package com.blankj.utilcode.util;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class MetaDataUtils {
    private MetaDataUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String getMetaDataInApp(String key) {
        PackageManager pm = Utils.getApp().getPackageManager();
        String packageName = Utils.getApp().getPackageName();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 128);
            String value = String.valueOf(ai.metaData.get(key));
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMetaDataInActivity(Activity activity, String key) {
        return getMetaDataInActivity((Class<? extends Activity>) activity.getClass(), key);
    }

    public static String getMetaDataInActivity(Class<? extends Activity> clz, String key) {
        PackageManager pm = Utils.getApp().getPackageManager();
        ComponentName componentName = new ComponentName(Utils.getApp(), clz);
        try {
            ActivityInfo ai = pm.getActivityInfo(componentName, 128);
            String value = String.valueOf(ai.metaData.get(key));
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMetaDataInService(Service service, String key) {
        return getMetaDataInService((Class<? extends Service>) service.getClass(), key);
    }

    public static String getMetaDataInService(Class<? extends Service> clz, String key) {
        PackageManager pm = Utils.getApp().getPackageManager();
        ComponentName componentName = new ComponentName(Utils.getApp(), clz);
        try {
            ServiceInfo info = pm.getServiceInfo(componentName, 128);
            String value = String.valueOf(info.metaData.get(key));
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMetaDataInReceiver(BroadcastReceiver receiver, String key) {
        return getMetaDataInReceiver((Class<? extends BroadcastReceiver>) receiver.getClass(), key);
    }

    public static String getMetaDataInReceiver(Class<? extends BroadcastReceiver> clz, String key) {
        PackageManager pm = Utils.getApp().getPackageManager();
        ComponentName componentName = new ComponentName(Utils.getApp(), clz);
        try {
            ActivityInfo info = pm.getReceiverInfo(componentName, 128);
            String value = String.valueOf(info.metaData.get(key));
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}

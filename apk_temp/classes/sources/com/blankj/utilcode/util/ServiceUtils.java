package com.blankj.utilcode.util;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.ServiceConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class ServiceUtils {
    private ServiceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Set<String> getAllRunningServices() {
        ActivityManager am = (ActivityManager) Utils.getApp().getSystemService("activity");
        List<ActivityManager.RunningServiceInfo> info = am.getRunningServices(Integer.MAX_VALUE);
        Set<String> names = new HashSet<>();
        if (info == null || info.size() == 0) {
            return null;
        }
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            names.add(aInfo.service.getClassName());
        }
        return names;
    }

    public static void startService(String className) {
        try {
            startService(Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startService(Class<?> cls) {
        startService(new Intent(Utils.getApp(), cls));
    }

    public static void startService(Intent intent) {
        try {
            intent.setFlags(32);
            Utils.getApp().startForegroundService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean stopService(String className) {
        try {
            return stopService(Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean stopService(Class<?> cls) {
        return stopService(new Intent(Utils.getApp(), cls));
    }

    public static boolean stopService(Intent intent) {
        try {
            return Utils.getApp().stopService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void bindService(String className, ServiceConnection conn, int flags) {
        try {
            bindService(Class.forName(className), conn, flags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bindService(Class<?> cls, ServiceConnection conn, int flags) {
        bindService(new Intent(Utils.getApp(), cls), conn, flags);
    }

    public static void bindService(Intent intent, ServiceConnection conn, int flags) {
        try {
            Utils.getApp().bindService(intent, conn, flags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unbindService(ServiceConnection conn) {
        Utils.getApp().unbindService(conn);
    }

    public static boolean isServiceRunning(Class<?> cls) {
        return isServiceRunning(cls.getName());
    }

    public static boolean isServiceRunning(String className) {
        try {
            ActivityManager am = (ActivityManager) Utils.getApp().getSystemService("activity");
            List<ActivityManager.RunningServiceInfo> info = am.getRunningServices(Integer.MAX_VALUE);
            if (info != null && info.size() != 0) {
                for (ActivityManager.RunningServiceInfo aInfo : info) {
                    if (className.equals(aInfo.service.getClassName())) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}

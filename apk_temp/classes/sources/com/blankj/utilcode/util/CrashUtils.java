package com.blankj.utilcode.util;

import com.blankj.utilcode.util.UtilsBridge;
import java.io.File;
import java.lang.Thread;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class CrashUtils {
    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final Thread.UncaughtExceptionHandler DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = Thread.getDefaultUncaughtExceptionHandler();

    public interface OnCrashListener {
        void onCrash(CrashInfo crashInfo);
    }

    private CrashUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init() {
        init("");
    }

    public static void init(File crashDir) {
        init(crashDir.getAbsolutePath(), (OnCrashListener) null);
    }

    public static void init(String crashDirPath) {
        init(crashDirPath, (OnCrashListener) null);
    }

    public static void init(OnCrashListener onCrashListener) {
        init("", onCrashListener);
    }

    public static void init(File crashDir, OnCrashListener onCrashListener) {
        init(crashDir.getAbsolutePath(), onCrashListener);
    }

    public static void init(String crashDirPath, OnCrashListener onCrashListener) {
        String dirPath;
        if (UtilsBridge.isSpace(crashDirPath)) {
            if (UtilsBridge.isSDCardEnableByEnvironment() && Utils.getApp().getExternalFilesDir(null) != null) {
                StringBuilder sbAppend = new StringBuilder().append(Utils.getApp().getExternalFilesDir(null));
                String str = FILE_SEP;
                dirPath = sbAppend.append(str).append("crash").append(str).toString();
            } else {
                StringBuilder sbAppend2 = new StringBuilder().append(Utils.getApp().getFilesDir());
                String str2 = FILE_SEP;
                dirPath = sbAppend2.append(str2).append("crash").append(str2).toString();
            }
        } else {
            String dirPath2 = FILE_SEP;
            dirPath = crashDirPath.endsWith(dirPath2) ? crashDirPath : crashDirPath + dirPath2;
        }
        Thread.setDefaultUncaughtExceptionHandler(getUncaughtExceptionHandler(dirPath, onCrashListener));
    }

    private static Thread.UncaughtExceptionHandler getUncaughtExceptionHandler(final String dirPath, final OnCrashListener onCrashListener) {
        return new Thread.UncaughtExceptionHandler() { // from class: com.blankj.utilcode.util.CrashUtils.1
            @Override // java.lang.Thread.UncaughtExceptionHandler
            public void uncaughtException(Thread t, Throwable e) {
                String time = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(new Date());
                CrashInfo info = new CrashInfo(time, e);
                String crashFile = dirPath + time + ".txt";
                UtilsBridge.writeFileFromString(crashFile, info.toString(), true);
                if (CrashUtils.DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null) {
                    CrashUtils.DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(t, e);
                }
                OnCrashListener onCrashListener2 = onCrashListener;
                if (onCrashListener2 != null) {
                    onCrashListener2.onCrash(info);
                }
            }
        };
    }

    public static final class CrashInfo {
        private UtilsBridge.FileHead mFileHeadProvider;
        private Throwable mThrowable;

        private CrashInfo(String time, Throwable throwable) {
            this.mThrowable = throwable;
            UtilsBridge.FileHead fileHead = new UtilsBridge.FileHead("Crash");
            this.mFileHeadProvider = fileHead;
            fileHead.addFirst("Time Of Crash", time);
        }

        public final void addExtraHead(Map<String, String> extraHead) {
            this.mFileHeadProvider.append(extraHead);
        }

        public final void addExtraHead(String key, String value) {
            this.mFileHeadProvider.append(key, value);
        }

        public final Throwable getThrowable() {
            return this.mThrowable;
        }

        public String toString() {
            return this.mFileHeadProvider.toString() + UtilsBridge.getFullStackTrace(this.mThrowable);
        }
    }
}

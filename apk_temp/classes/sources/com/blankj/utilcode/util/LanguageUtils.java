package com.blankj.utilcode.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import com.blankj.utilcode.util.Utils;
import java.util.Locale;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public class LanguageUtils {
    private static final String KEY_LOCALE = "KEY_LOCALE";
    private static final String VALUE_FOLLOW_SYSTEM = "VALUE_FOLLOW_SYSTEM";

    private LanguageUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void applySystemLanguage() {
        applySystemLanguage(false);
    }

    public static void applySystemLanguage(boolean isRelaunchApp) {
        applyLanguageReal(null, isRelaunchApp);
    }

    public static void applyLanguage(Locale locale) {
        applyLanguage(locale, false);
    }

    public static void applyLanguage(Locale locale, boolean isRelaunchApp) {
        applyLanguageReal(locale, isRelaunchApp);
    }

    private static void applyLanguageReal(Locale locale, final boolean isRelaunchApp) {
        if (locale == null) {
            UtilsBridge.getSpUtils4Utils().put(KEY_LOCALE, VALUE_FOLLOW_SYSTEM, true);
        } else {
            UtilsBridge.getSpUtils4Utils().put(KEY_LOCALE, locale2String(locale), true);
        }
        Locale destLocal = locale == null ? getLocal(Resources.getSystem().getConfiguration()) : locale;
        updateAppContextLanguage(destLocal, new Utils.Consumer<Boolean>() { // from class: com.blankj.utilcode.util.LanguageUtils.1
            @Override // com.blankj.utilcode.util.Utils.Consumer
            public void accept(Boolean success) {
                if (success.booleanValue()) {
                    LanguageUtils.restart(isRelaunchApp);
                } else {
                    UtilsBridge.relaunchApp();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void restart(boolean isRelaunchApp) {
        if (isRelaunchApp) {
            UtilsBridge.relaunchApp();
            return;
        }
        for (Activity activity : UtilsBridge.getActivityList()) {
            activity.recreate();
        }
    }

    public static boolean isAppliedLanguage() {
        return getAppliedLanguage() != null;
    }

    public static boolean isAppliedLanguage(Locale locale) {
        Locale appliedLocale = getAppliedLanguage();
        if (appliedLocale == null) {
            return false;
        }
        return isSameLocale(locale, appliedLocale);
    }

    public static Locale getAppliedLanguage() {
        String spLocaleStr = UtilsBridge.getSpUtils4Utils().getString(KEY_LOCALE);
        if (TextUtils.isEmpty(spLocaleStr) || VALUE_FOLLOW_SYSTEM.equals(spLocaleStr)) {
            return null;
        }
        return string2Locale(spLocaleStr);
    }

    public static Locale getContextLanguage(Context context) {
        return getLocal(context.getResources().getConfiguration());
    }

    public static Locale getAppContextLanguage() {
        return getContextLanguage(Utils.getApp());
    }

    public static Locale getSystemLanguage() {
        return getLocal(Resources.getSystem().getConfiguration());
    }

    public static void updateAppContextLanguage(Locale destLocale, Utils.Consumer<Boolean> consumer) {
        pollCheckAppContextLocal(destLocale, 0, consumer);
    }

    static void pollCheckAppContextLocal(final Locale destLocale, final int index, final Utils.Consumer<Boolean> consumer) {
        Resources appResources = Utils.getApp().getResources();
        Configuration appConfig = appResources.getConfiguration();
        Locale appLocal = getLocal(appConfig);
        setLocal(appConfig, destLocale);
        Utils.getApp().getResources().updateConfiguration(appConfig, appResources.getDisplayMetrics());
        if (consumer == null) {
            return;
        }
        if (isSameLocale(appLocal, destLocale)) {
            consumer.accept(true);
        } else if (index < 20) {
            UtilsBridge.runOnUiThreadDelayed(new Runnable() { // from class: com.blankj.utilcode.util.LanguageUtils.2
                @Override // java.lang.Runnable
                public void run() {
                    LanguageUtils.pollCheckAppContextLocal(destLocale, index + 1, consumer);
                }
            }, 16L);
        } else {
            Log.e("LanguageUtils", "appLocal didn't update.");
            consumer.accept(false);
        }
    }

    public static Context attachBaseContext(Context context) {
        Locale settingsLocale;
        String spLocaleStr = UtilsBridge.getSpUtils4Utils().getString(KEY_LOCALE);
        if (TextUtils.isEmpty(spLocaleStr) || VALUE_FOLLOW_SYSTEM.equals(spLocaleStr) || (settingsLocale = string2Locale(spLocaleStr)) == null) {
            return context;
        }
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        setLocal(config, settingsLocale);
        return context.createConfigurationContext(config);
    }

    static void applyLanguage(Activity activity) {
        Locale destLocal;
        String spLocale = UtilsBridge.getSpUtils4Utils().getString(KEY_LOCALE);
        if (TextUtils.isEmpty(spLocale)) {
            return;
        }
        if (VALUE_FOLLOW_SYSTEM.equals(spLocale)) {
            destLocal = getLocal(Resources.getSystem().getConfiguration());
        } else {
            destLocal = string2Locale(spLocale);
        }
        if (destLocal == null) {
            return;
        }
        updateConfiguration(activity, destLocal);
        updateConfiguration(Utils.getApp(), destLocal);
    }

    private static void updateConfiguration(Context context, Locale destLocal) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        setLocal(config, destLocal);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private static String locale2String(Locale locale) {
        String localLanguage = locale.getLanguage();
        String localCountry = locale.getCountry();
        return localLanguage + "$" + localCountry;
    }

    private static Locale string2Locale(String str) {
        Locale locale = string2LocaleReal(str);
        if (locale == null) {
            Log.e("LanguageUtils", "The string of " + str + " is not in the correct format.");
            UtilsBridge.getSpUtils4Utils().remove(KEY_LOCALE);
        }
        return locale;
    }

    private static Locale string2LocaleReal(String str) {
        if (!isRightFormatLocalStr(str)) {
            return null;
        }
        try {
            int splitIndex = str.indexOf("$");
            return new Locale(str.substring(0, splitIndex), str.substring(splitIndex + 1));
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isRightFormatLocalStr(String localStr) {
        char[] chars = localStr.toCharArray();
        int count = 0;
        for (char c : chars) {
            if (c == '$') {
                if (count >= 1) {
                    return false;
                }
                count++;
            }
        }
        return count == 1;
    }

    private static boolean isSameLocale(Locale l0, Locale l1) {
        return UtilsBridge.equals(l1.getLanguage(), l0.getLanguage()) && UtilsBridge.equals(l1.getCountry(), l0.getCountry());
    }

    private static Locale getLocal(Configuration configuration) {
        return configuration.getLocales().get(0);
    }

    private static void setLocal(Configuration configuration, Locale locale) {
        configuration.setLocale(locale);
    }
}

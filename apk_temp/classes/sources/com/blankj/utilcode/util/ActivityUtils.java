package com.blankj.utilcode.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import com.blankj.utilcode.util.Utils;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class ActivityUtils {
    private ActivityUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void addActivityLifecycleCallbacks(Utils.ActivityLifecycleCallbacks callbacks) {
        UtilsBridge.addActivityLifecycleCallbacks(callbacks);
    }

    public static void addActivityLifecycleCallbacks(Activity activity, Utils.ActivityLifecycleCallbacks callbacks) {
        UtilsBridge.addActivityLifecycleCallbacks(activity, callbacks);
    }

    public static void removeActivityLifecycleCallbacks(Utils.ActivityLifecycleCallbacks callbacks) {
        UtilsBridge.removeActivityLifecycleCallbacks(callbacks);
    }

    public static void removeActivityLifecycleCallbacks(Activity activity) {
        UtilsBridge.removeActivityLifecycleCallbacks(activity);
    }

    public static void removeActivityLifecycleCallbacks(Activity activity, Utils.ActivityLifecycleCallbacks callbacks) {
        UtilsBridge.removeActivityLifecycleCallbacks(activity, callbacks);
    }

    public static Activity getActivityByContext(Context context) {
        if (context == null) {
            return null;
        }
        Activity activity = getActivityByContextInner(context);
        if (!isActivityAlive(activity)) {
            return null;
        }
        return activity;
    }

    private static Activity getActivityByContextInner(Context context) {
        if (context == null) {
            return null;
        }
        List<Context> list = new ArrayList<>();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            Activity activity = getActivityFromDecorContext(context);
            if (activity != null) {
                return activity;
            }
            list.add(context);
            context = ((ContextWrapper) context).getBaseContext();
            if (context == null || list.contains(context)) {
                return null;
            }
        }
        return null;
    }

    private static Activity getActivityFromDecorContext(Context context) {
        if (context != null && context.getClass().getName().equals("com.android.internal.policy.DecorContext")) {
            try {
                Field mActivityContextField = context.getClass().getDeclaredField("mActivityContext");
                mActivityContextField.setAccessible(true);
                return (Activity) ((WeakReference) mActivityContextField.get(context)).get();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static boolean isActivityExists(String pkg, String cls) {
        Intent intent = new Intent();
        intent.setClassName(pkg, cls);
        PackageManager pm = Utils.getApp().getPackageManager();
        return (pm.resolveActivity(intent, 0) == null || intent.resolveActivity(pm) == null || pm.queryIntentActivities(intent, 0).size() == 0) ? false : true;
    }

    public static void startActivity(Class<? extends Activity> clz) {
        Context context = getTopActivityOrApp();
        startActivity(context, (Bundle) null, context.getPackageName(), clz.getName(), (Bundle) null);
    }

    public static void startActivity(Class<? extends Activity> clz, Bundle options) {
        Context context = getTopActivityOrApp();
        startActivity(context, (Bundle) null, context.getPackageName(), clz.getName(), options);
    }

    public static void startActivity(Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        Context context = getTopActivityOrApp();
        startActivity(context, (Bundle) null, context.getPackageName(), clz.getName(), getOptionsBundle(context, enterAnim, exitAnim));
    }

    public static void startActivity(Activity activity, Class<? extends Activity> clz) {
        startActivity(activity, (Bundle) null, activity.getPackageName(), clz.getName(), (Bundle) null);
    }

    public static void startActivity(Activity activity, Class<? extends Activity> clz, Bundle options) {
        startActivity(activity, (Bundle) null, activity.getPackageName(), clz.getName(), options);
    }

    public static void startActivity(Activity activity, Class<? extends Activity> clz, View... sharedElements) {
        startActivity(activity, (Bundle) null, activity.getPackageName(), clz.getName(), getOptionsBundle(activity, sharedElements));
    }

    public static void startActivity(Activity activity, Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        startActivity(activity, (Bundle) null, activity.getPackageName(), clz.getName(), getOptionsBundle(activity, enterAnim, exitAnim));
    }

    public static void startActivity(Bundle extras, Class<? extends Activity> clz) {
        Context context = getTopActivityOrApp();
        startActivity(context, extras, context.getPackageName(), clz.getName(), (Bundle) null);
    }

    public static void startActivity(Bundle extras, Class<? extends Activity> clz, Bundle options) {
        Context context = getTopActivityOrApp();
        startActivity(context, extras, context.getPackageName(), clz.getName(), options);
    }

    public static void startActivity(Bundle extras, Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        Context context = getTopActivityOrApp();
        startActivity(context, extras, context.getPackageName(), clz.getName(), getOptionsBundle(context, enterAnim, exitAnim));
    }

    public static void startActivity(Bundle extras, Activity activity, Class<? extends Activity> clz) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(), (Bundle) null);
    }

    public static void startActivity(Bundle extras, Activity activity, Class<? extends Activity> clz, Bundle options) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(), options);
    }

    public static void startActivity(Bundle extras, Activity activity, Class<? extends Activity> clz, View... sharedElements) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(), getOptionsBundle(activity, sharedElements));
    }

    public static void startActivity(Bundle extras, Activity activity, Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(), getOptionsBundle(activity, enterAnim, exitAnim));
    }

    public static void startActivity(String pkg, String cls) {
        startActivity(getTopActivityOrApp(), (Bundle) null, pkg, cls, (Bundle) null);
    }

    public static void startActivity(String pkg, String cls, Bundle options) {
        startActivity(getTopActivityOrApp(), (Bundle) null, pkg, cls, options);
    }

    public static void startActivity(String pkg, String cls, int enterAnim, int exitAnim) {
        Context context = getTopActivityOrApp();
        startActivity(context, (Bundle) null, pkg, cls, getOptionsBundle(context, enterAnim, exitAnim));
    }

    public static void startActivity(Activity activity, String pkg, String cls) {
        startActivity(activity, (Bundle) null, pkg, cls, (Bundle) null);
    }

    public static void startActivity(Activity activity, String pkg, String cls, Bundle options) {
        startActivity(activity, (Bundle) null, pkg, cls, options);
    }

    public static void startActivity(Activity activity, String pkg, String cls, View... sharedElements) {
        startActivity(activity, (Bundle) null, pkg, cls, getOptionsBundle(activity, sharedElements));
    }

    public static void startActivity(Activity activity, String pkg, String cls, int enterAnim, int exitAnim) {
        startActivity(activity, (Bundle) null, pkg, cls, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    public static void startActivity(Bundle extras, String pkg, String cls) {
        startActivity(getTopActivityOrApp(), extras, pkg, cls, (Bundle) null);
    }

    public static void startActivity(Bundle extras, String pkg, String cls, Bundle options) {
        startActivity(getTopActivityOrApp(), extras, pkg, cls, options);
    }

    public static void startActivity(Bundle extras, String pkg, String cls, int enterAnim, int exitAnim) {
        Context context = getTopActivityOrApp();
        startActivity(context, extras, pkg, cls, getOptionsBundle(context, enterAnim, exitAnim));
    }

    public static void startActivity(Bundle extras, Activity activity, String pkg, String cls) {
        startActivity(activity, extras, pkg, cls, (Bundle) null);
    }

    public static void startActivity(Bundle extras, Activity activity, String pkg, String cls, Bundle options) {
        startActivity(activity, extras, pkg, cls, options);
    }

    public static void startActivity(Bundle extras, Activity activity, String pkg, String cls, View... sharedElements) {
        startActivity(activity, extras, pkg, cls, getOptionsBundle(activity, sharedElements));
    }

    public static void startActivity(Bundle extras, Activity activity, String pkg, String cls, int enterAnim, int exitAnim) {
        startActivity(activity, extras, pkg, cls, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    public static boolean startActivity(Intent intent) {
        return startActivity(intent, getTopActivityOrApp(), (Bundle) null);
    }

    public static boolean startActivity(Intent intent, Bundle options) {
        return startActivity(intent, getTopActivityOrApp(), options);
    }

    public static boolean startActivity(Intent intent, int enterAnim, int exitAnim) {
        Context context = getTopActivityOrApp();
        boolean isSuccess = startActivity(intent, context, getOptionsBundle(context, enterAnim, exitAnim));
        return isSuccess;
    }

    public static void startActivity(Activity activity, Intent intent) {
        startActivity(intent, activity, (Bundle) null);
    }

    public static void startActivity(Activity activity, Intent intent, Bundle options) {
        startActivity(intent, activity, options);
    }

    public static void startActivity(Activity activity, Intent intent, View... sharedElements) {
        startActivity(intent, activity, getOptionsBundle(activity, sharedElements));
    }

    public static void startActivity(Activity activity, Intent intent, int enterAnim, int exitAnim) {
        startActivity(intent, activity, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    public static void startActivityForResult(Activity activity, Class<? extends Activity> clz, int requestCode) {
        startActivityForResult(activity, (Bundle) null, activity.getPackageName(), clz.getName(), requestCode, (Bundle) null);
    }

    public static void startActivityForResult(Activity activity, Class<? extends Activity> clz, int requestCode, Bundle options) {
        startActivityForResult(activity, (Bundle) null, activity.getPackageName(), clz.getName(), requestCode, options);
    }

    public static void startActivityForResult(Activity activity, Class<? extends Activity> clz, int requestCode, View... sharedElements) {
        startActivityForResult(activity, (Bundle) null, activity.getPackageName(), clz.getName(), requestCode, getOptionsBundle(activity, sharedElements));
    }

    public static void startActivityForResult(Activity activity, Class<? extends Activity> clz, int requestCode, int enterAnim, int exitAnim) {
        startActivityForResult(activity, (Bundle) null, activity.getPackageName(), clz.getName(), requestCode, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    public static void startActivityForResult(Bundle extras, Activity activity, Class<? extends Activity> clz, int requestCode) {
        startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(), requestCode, (Bundle) null);
    }

    public static void startActivityForResult(Bundle extras, Activity activity, Class<? extends Activity> clz, int requestCode, Bundle options) {
        startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(), requestCode, options);
    }

    public static void startActivityForResult(Bundle extras, Activity activity, Class<? extends Activity> clz, int requestCode, View... sharedElements) {
        startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(), requestCode, getOptionsBundle(activity, sharedElements));
    }

    public static void startActivityForResult(Bundle extras, Activity activity, Class<? extends Activity> clz, int requestCode, int enterAnim, int exitAnim) {
        startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(), requestCode, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    public static void startActivityForResult(Bundle extras, Activity activity, String pkg, String cls, int requestCode) {
        startActivityForResult(activity, extras, pkg, cls, requestCode, (Bundle) null);
    }

    public static void startActivityForResult(Bundle extras, Activity activity, String pkg, String cls, int requestCode, Bundle options) {
        startActivityForResult(activity, extras, pkg, cls, requestCode, options);
    }

    public static void startActivityForResult(Bundle extras, Activity activity, String pkg, String cls, int requestCode, View... sharedElements) {
        startActivityForResult(activity, extras, pkg, cls, requestCode, getOptionsBundle(activity, sharedElements));
    }

    public static void startActivityForResult(Bundle extras, Activity activity, String pkg, String cls, int requestCode, int enterAnim, int exitAnim) {
        startActivityForResult(activity, extras, pkg, cls, requestCode, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        startActivityForResult(intent, activity, requestCode, (Bundle) null);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, Bundle options) {
        startActivityForResult(intent, activity, requestCode, options);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, View... sharedElements) {
        startActivityForResult(intent, activity, requestCode, getOptionsBundle(activity, sharedElements));
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, int enterAnim, int exitAnim) {
        startActivityForResult(intent, activity, requestCode, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    public static void startActivityForResult(Fragment fragment, Class<? extends Activity> clz, int requestCode) {
        startActivityForResult(fragment, (Bundle) null, Utils.getApp().getPackageName(), clz.getName(), requestCode, (Bundle) null);
    }

    public static void startActivityForResult(Fragment fragment, Class<? extends Activity> clz, int requestCode, Bundle options) {
        startActivityForResult(fragment, (Bundle) null, Utils.getApp().getPackageName(), clz.getName(), requestCode, options);
    }

    public static void startActivityForResult(Fragment fragment, Class<? extends Activity> clz, int requestCode, View... sharedElements) {
        startActivityForResult(fragment, (Bundle) null, Utils.getApp().getPackageName(), clz.getName(), requestCode, getOptionsBundle(fragment, sharedElements));
    }

    public static void startActivityForResult(Fragment fragment, Class<? extends Activity> clz, int requestCode, int enterAnim, int exitAnim) {
        startActivityForResult(fragment, (Bundle) null, Utils.getApp().getPackageName(), clz.getName(), requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, Class<? extends Activity> clz, int requestCode) {
        startActivityForResult(fragment, extras, Utils.getApp().getPackageName(), clz.getName(), requestCode, (Bundle) null);
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, Class<? extends Activity> clz, int requestCode, Bundle options) {
        startActivityForResult(fragment, extras, Utils.getApp().getPackageName(), clz.getName(), requestCode, options);
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, Class<? extends Activity> clz, int requestCode, View... sharedElements) {
        startActivityForResult(fragment, extras, Utils.getApp().getPackageName(), clz.getName(), requestCode, getOptionsBundle(fragment, sharedElements));
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, Class<? extends Activity> clz, int requestCode, int enterAnim, int exitAnim) {
        startActivityForResult(fragment, extras, Utils.getApp().getPackageName(), clz.getName(), requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, String pkg, String cls, int requestCode) {
        startActivityForResult(fragment, extras, pkg, cls, requestCode, (Bundle) null);
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, String pkg, String cls, int requestCode, Bundle options) {
        startActivityForResult(fragment, extras, pkg, cls, requestCode, options);
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, String pkg, String cls, int requestCode, View... sharedElements) {
        startActivityForResult(fragment, extras, pkg, cls, requestCode, getOptionsBundle(fragment, sharedElements));
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, String pkg, String cls, int requestCode, int enterAnim, int exitAnim) {
        startActivityForResult(fragment, extras, pkg, cls, requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
    }

    public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode) {
        startActivityForResult(intent, fragment, requestCode, (Bundle) null);
    }

    public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode, Bundle options) {
        startActivityForResult(intent, fragment, requestCode, options);
    }

    public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode, View... sharedElements) {
        startActivityForResult(intent, fragment, requestCode, getOptionsBundle(fragment, sharedElements));
    }

    public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode, int enterAnim, int exitAnim) {
        startActivityForResult(intent, fragment, requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
    }

    public static void startActivities(Intent[] intents) {
        startActivities(intents, getTopActivityOrApp(), (Bundle) null);
    }

    public static void startActivities(Intent[] intents, Bundle options) {
        startActivities(intents, getTopActivityOrApp(), options);
    }

    public static void startActivities(Intent[] intents, int enterAnim, int exitAnim) {
        Context context = getTopActivityOrApp();
        startActivities(intents, context, getOptionsBundle(context, enterAnim, exitAnim));
    }

    public static void startActivities(Activity activity, Intent[] intents) {
        startActivities(intents, activity, (Bundle) null);
    }

    public static void startActivities(Activity activity, Intent[] intents, Bundle options) {
        startActivities(intents, activity, options);
    }

    public static void startActivities(Activity activity, Intent[] intents, int enterAnim, int exitAnim) {
        startActivities(intents, activity, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    public static void startHomeActivity() {
        Intent homeIntent = new Intent("android.intent.action.MAIN");
        homeIntent.addCategory("android.intent.category.HOME");
        homeIntent.setFlags(268435456);
        startActivity(homeIntent);
    }

    public static void startLauncherActivity() {
        startLauncherActivity(Utils.getApp().getPackageName());
    }

    public static void startLauncherActivity(String pkg) {
        String launcherActivity = getLauncherActivity(pkg);
        if (TextUtils.isEmpty(launcherActivity)) {
            return;
        }
        startActivity(pkg, launcherActivity);
    }

    public static List<Activity> getActivityList() {
        return UtilsBridge.getActivityList();
    }

    public static String getLauncherActivity() {
        return getLauncherActivity(Utils.getApp().getPackageName());
    }

    public static String getLauncherActivity(String pkg) {
        if (UtilsBridge.isSpace(pkg)) {
            return "";
        }
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(pkg);
        PackageManager pm = Utils.getApp().getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        return (info == null || info.size() == 0) ? "" : info.get(0).activityInfo.name;
    }

    public static List<String> getMainActivities() {
        return getMainActivities(Utils.getApp().getPackageName());
    }

    public static List<String> getMainActivities(String pkg) {
        List<String> ret = new ArrayList<>();
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.setPackage(pkg);
        PackageManager pm = Utils.getApp().getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        int size = info.size();
        if (size == 0) {
            return ret;
        }
        for (int i = 0; i < size; i++) {
            ResolveInfo ri = info.get(i);
            if (ri.activityInfo.processName.equals(pkg)) {
                ret.add(ri.activityInfo.name);
            }
        }
        return ret;
    }

    public static Activity getTopActivity() {
        return UtilsBridge.getTopActivity();
    }

    public static boolean isActivityAlive(Context context) {
        return isActivityAlive(getActivityByContext(context));
    }

    public static boolean isActivityAlive(Activity activity) {
        return (activity == null || activity.isFinishing() || activity.isDestroyed()) ? false : true;
    }

    public static boolean isActivityExistsInStack(Activity activity) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (Activity aActivity : activities) {
            if (aActivity.equals(activity)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isActivityExistsInStack(Class<? extends Activity> clz) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (Activity aActivity : activities) {
            if (aActivity.getClass().equals(clz)) {
                return true;
            }
        }
        return false;
    }

    public static void finishActivity(Activity activity) {
        finishActivity(activity, false);
    }

    public static void finishActivity(Activity activity, boolean isLoadAnim) {
        activity.finish();
        if (!isLoadAnim) {
            activity.overridePendingTransition(0, 0);
        }
    }

    public static void finishActivity(Activity activity, int enterAnim, int exitAnim) {
        activity.finish();
        activity.overridePendingTransition(enterAnim, exitAnim);
    }

    public static void finishActivity(Class<? extends Activity> clz) {
        finishActivity(clz, false);
    }

    public static void finishActivity(Class<? extends Activity> clz, boolean isLoadAnim) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (Activity activity : activities) {
            if (activity.getClass().equals(clz)) {
                activity.finish();
                if (!isLoadAnim) {
                    activity.overridePendingTransition(0, 0);
                }
            }
        }
    }

    public static void finishActivity(Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (Activity activity : activities) {
            if (activity.getClass().equals(clz)) {
                activity.finish();
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        }
    }

    public static boolean finishToActivity(Activity activity, boolean isIncludeSelf) {
        return finishToActivity(activity, isIncludeSelf, false);
    }

    public static boolean finishToActivity(Activity activity, boolean isIncludeSelf, boolean isLoadAnim) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (Activity act : activities) {
            if (act.equals(activity)) {
                if (isIncludeSelf) {
                    finishActivity(act, isLoadAnim);
                    return true;
                }
                return true;
            }
            finishActivity(act, isLoadAnim);
        }
        return false;
    }

    public static boolean finishToActivity(Activity activity, boolean isIncludeSelf, int enterAnim, int exitAnim) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (Activity act : activities) {
            if (act.equals(activity)) {
                if (isIncludeSelf) {
                    finishActivity(act, enterAnim, exitAnim);
                    return true;
                }
                return true;
            }
            finishActivity(act, enterAnim, exitAnim);
        }
        return false;
    }

    public static boolean finishToActivity(Class<? extends Activity> clz, boolean isIncludeSelf) {
        return finishToActivity(clz, isIncludeSelf, false);
    }

    public static boolean finishToActivity(Class<? extends Activity> clz, boolean isIncludeSelf, boolean isLoadAnim) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (Activity act : activities) {
            if (act.getClass().equals(clz)) {
                if (isIncludeSelf) {
                    finishActivity(act, isLoadAnim);
                    return true;
                }
                return true;
            }
            finishActivity(act, isLoadAnim);
        }
        return false;
    }

    public static boolean finishToActivity(Class<? extends Activity> clz, boolean isIncludeSelf, int enterAnim, int exitAnim) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (Activity act : activities) {
            if (act.getClass().equals(clz)) {
                if (isIncludeSelf) {
                    finishActivity(act, enterAnim, exitAnim);
                    return true;
                }
                return true;
            }
            finishActivity(act, enterAnim, exitAnim);
        }
        return false;
    }

    public static void finishOtherActivities(Class<? extends Activity> clz) {
        finishOtherActivities(clz, false);
    }

    public static void finishOtherActivities(Class<? extends Activity> clz, boolean isLoadAnim) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (Activity act : activities) {
            if (!act.getClass().equals(clz)) {
                finishActivity(act, isLoadAnim);
            }
        }
    }

    public static void finishOtherActivities(Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (Activity act : activities) {
            if (!act.getClass().equals(clz)) {
                finishActivity(act, enterAnim, exitAnim);
            }
        }
    }

    public static void finishAllActivities() {
        finishAllActivities(false);
    }

    public static void finishAllActivities(boolean isLoadAnim) {
        List<Activity> activityList = UtilsBridge.getActivityList();
        for (Activity act : activityList) {
            act.finish();
            if (!isLoadAnim) {
                act.overridePendingTransition(0, 0);
            }
        }
    }

    public static void finishAllActivities(int enterAnim, int exitAnim) {
        List<Activity> activityList = UtilsBridge.getActivityList();
        for (Activity act : activityList) {
            act.finish();
            act.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    public static void finishAllActivitiesExceptNewest() {
        finishAllActivitiesExceptNewest(false);
    }

    public static void finishAllActivitiesExceptNewest(boolean isLoadAnim) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (int i = 1; i < activities.size(); i++) {
            finishActivity(activities.get(i), isLoadAnim);
        }
    }

    public static void finishAllActivitiesExceptNewest(int enterAnim, int exitAnim) {
        List<Activity> activities = UtilsBridge.getActivityList();
        for (int i = 1; i < activities.size(); i++) {
            finishActivity(activities.get(i), enterAnim, exitAnim);
        }
    }

    public static Drawable getActivityIcon(Activity activity) {
        return getActivityIcon(activity.getComponentName());
    }

    public static Drawable getActivityIcon(Class<? extends Activity> clz) {
        return getActivityIcon(new ComponentName(Utils.getApp(), clz));
    }

    public static Drawable getActivityIcon(ComponentName activityName) {
        PackageManager pm = Utils.getApp().getPackageManager();
        try {
            return pm.getActivityIcon(activityName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Drawable getActivityLogo(Activity activity) {
        return getActivityLogo(activity.getComponentName());
    }

    public static Drawable getActivityLogo(Class<? extends Activity> clz) {
        return getActivityLogo(new ComponentName(Utils.getApp(), clz));
    }

    public static Drawable getActivityLogo(ComponentName activityName) {
        PackageManager pm = Utils.getApp().getPackageManager();
        try {
            return pm.getActivityLogo(activityName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void startActivity(Context context, Bundle extras, String pkg, String cls, Bundle options) {
        Intent intent = new Intent();
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setComponent(new ComponentName(pkg, cls));
        startActivity(intent, context, options);
    }

    private static boolean startActivity(Intent intent, Context context, Bundle options) {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable");
            return false;
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(268435456);
        }
        if (options != null) {
            context.startActivity(intent, options);
            return true;
        }
        context.startActivity(intent);
        return true;
    }

    private static boolean isIntentAvailable(Intent intent) {
        return true;
    }

    private static boolean startActivityForResult(Activity activity, Bundle extras, String pkg, String cls, int requestCode, Bundle options) {
        Intent intent = new Intent();
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setComponent(new ComponentName(pkg, cls));
        return startActivityForResult(intent, activity, requestCode, options);
    }

    private static boolean startActivityForResult(Intent intent, Activity activity, int requestCode, Bundle options) {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable");
            return false;
        }
        if (options != null) {
            activity.startActivityForResult(intent, requestCode, options);
            return true;
        }
        activity.startActivityForResult(intent, requestCode);
        return true;
    }

    private static void startActivities(Intent[] intents, Context context, Bundle options) {
        if (!(context instanceof Activity)) {
            for (Intent intent : intents) {
                intent.addFlags(268435456);
            }
        }
        if (options != null) {
            context.startActivities(intents, options);
        } else {
            context.startActivities(intents);
        }
    }

    private static boolean startActivityForResult(Fragment fragment, Bundle extras, String pkg, String cls, int requestCode, Bundle options) {
        Intent intent = new Intent();
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setComponent(new ComponentName(pkg, cls));
        return startActivityForResult(intent, fragment, requestCode, options);
    }

    private static boolean startActivityForResult(Intent intent, Fragment fragment, int requestCode, Bundle options) {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable");
            return false;
        }
        if (fragment.getActivity() == null) {
            Log.e("ActivityUtils", "Fragment " + fragment + " not attached to Activity");
            return false;
        }
        if (options != null) {
            fragment.startActivityForResult(intent, requestCode, options);
            return true;
        }
        fragment.startActivityForResult(intent, requestCode);
        return true;
    }

    private static Bundle getOptionsBundle(Fragment fragment, int enterAnim, int exitAnim) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return null;
        }
        return ActivityOptionsCompat.makeCustomAnimation(activity, enterAnim, exitAnim).toBundle();
    }

    private static Bundle getOptionsBundle(Context context, int enterAnim, int exitAnim) {
        return ActivityOptionsCompat.makeCustomAnimation(context, enterAnim, exitAnim).toBundle();
    }

    private static Bundle getOptionsBundle(Fragment fragment, View[] sharedElements) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return null;
        }
        return getOptionsBundle(activity, sharedElements);
    }

    private static Bundle getOptionsBundle(Activity activity, View[] sharedElements) {
        int len;
        if (sharedElements == null || (len = sharedElements.length) <= 0) {
            return null;
        }
        Pair<View, String>[] pairs = new Pair[len];
        for (int i = 0; i < len; i++) {
            pairs[i] = Pair.create(sharedElements[i], sharedElements[i].getTransitionName());
        }
        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs).toBundle();
    }

    private static Context getTopActivityOrApp() {
        if (UtilsBridge.isAppForeground()) {
            Activity topActivity = getTopActivity();
            return topActivity == null ? Utils.getApp() : topActivity;
        }
        return Utils.getApp();
    }
}

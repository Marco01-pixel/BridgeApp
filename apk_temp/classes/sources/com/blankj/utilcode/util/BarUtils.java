package com.blankj.utilcode.util;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class BarUtils {
    private static final int KEY_OFFSET = -123;
    private static final String TAG_OFFSET = "TAG_OFFSET";
    private static final String TAG_STATUS_BAR = "TAG_STATUS_BAR";

    private BarUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static void setStatusBarVisibility(Activity activity, boolean isVisible) {
        setStatusBarVisibility(activity.getWindow(), isVisible);
    }

    public static void setStatusBarVisibility(Window window, boolean isVisible) {
        if (isVisible) {
            window.clearFlags(1024);
            showStatusBarView(window);
            addMarginTopEqualStatusBarHeight(window);
        } else {
            window.addFlags(1024);
            hideStatusBarView(window);
            subtractMarginTopEqualStatusBarHeight(window);
        }
    }

    public static boolean isStatusBarVisible(Activity activity) {
        int flags = activity.getWindow().getAttributes().flags;
        return (flags & 1024) == 0;
    }

    public static void setStatusBarLightMode(Activity activity, boolean isLightMode) {
        setStatusBarLightMode(activity.getWindow(), isLightMode);
    }

    public static void setStatusBarLightMode(Window window, boolean isLightMode) {
        int vis;
        View decorView = window.getDecorView();
        int vis2 = decorView.getSystemUiVisibility();
        if (isLightMode) {
            vis = vis2 | 8192;
        } else {
            vis = vis2 & (-8193);
        }
        decorView.setSystemUiVisibility(vis);
    }

    public static boolean isStatusBarLightMode(Activity activity) {
        return isStatusBarLightMode(activity.getWindow());
    }

    public static boolean isStatusBarLightMode(Window window) {
        View decorView = window.getDecorView();
        int vis = decorView.getSystemUiVisibility();
        return (vis & 8192) != 0;
    }

    public static void addMarginTopEqualStatusBarHeight(View view) {
        view.setTag(TAG_OFFSET);
        Object haveSetOffset = view.getTag(KEY_OFFSET);
        if (haveSetOffset == null || !((Boolean) haveSetOffset).booleanValue()) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + getStatusBarHeight(), layoutParams.rightMargin, layoutParams.bottomMargin);
            view.setTag(KEY_OFFSET, true);
        }
    }

    public static void subtractMarginTopEqualStatusBarHeight(View view) {
        Object haveSetOffset = view.getTag(KEY_OFFSET);
        if (haveSetOffset == null || !((Boolean) haveSetOffset).booleanValue()) {
            return;
        }
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin - getStatusBarHeight(), layoutParams.rightMargin, layoutParams.bottomMargin);
        view.setTag(KEY_OFFSET, false);
    }

    private static void addMarginTopEqualStatusBarHeight(Window window) {
        View withTag = window.getDecorView().findViewWithTag(TAG_OFFSET);
        if (withTag == null) {
            return;
        }
        addMarginTopEqualStatusBarHeight(withTag);
    }

    private static void subtractMarginTopEqualStatusBarHeight(Window window) {
        View withTag = window.getDecorView().findViewWithTag(TAG_OFFSET);
        if (withTag == null) {
            return;
        }
        subtractMarginTopEqualStatusBarHeight(withTag);
    }

    public static View setStatusBarColor(Activity activity, int color) {
        return setStatusBarColor(activity, color, false);
    }

    public static View setStatusBarColor(Activity activity, int color, boolean isDecor) {
        transparentStatusBar(activity);
        return applyStatusBarColor(activity, color, isDecor);
    }

    public static View setStatusBarColor(Window window, int color) {
        return setStatusBarColor(window, color, false);
    }

    public static View setStatusBarColor(Window window, int color, boolean isDecor) {
        transparentStatusBar(window);
        return applyStatusBarColor(window, color, isDecor);
    }

    public static void setStatusBarColor(View fakeStatusBar, int color) {
        Activity activity = UtilsBridge.getActivityByContext(fakeStatusBar.getContext());
        if (activity == null) {
            return;
        }
        transparentStatusBar(activity);
        fakeStatusBar.setVisibility(0);
        ViewGroup.LayoutParams layoutParams = fakeStatusBar.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = getStatusBarHeight();
        fakeStatusBar.setBackgroundColor(color);
    }

    public static void setStatusBarCustom(View fakeStatusBar) {
        Activity activity = UtilsBridge.getActivityByContext(fakeStatusBar.getContext());
        if (activity == null) {
            return;
        }
        transparentStatusBar(activity);
        fakeStatusBar.setVisibility(0);
        ViewGroup.LayoutParams layoutParams = fakeStatusBar.getLayoutParams();
        if (layoutParams == null) {
            fakeStatusBar.setLayoutParams(new ViewGroup.LayoutParams(-1, getStatusBarHeight()));
        } else {
            layoutParams.width = -1;
            layoutParams.height = getStatusBarHeight();
        }
    }

    public static void setStatusBarColor4Drawer(DrawerLayout drawer, View fakeStatusBar, int color) {
        setStatusBarColor4Drawer(drawer, fakeStatusBar, color, false);
    }

    public static void setStatusBarColor4Drawer(DrawerLayout drawer, View fakeStatusBar, int color, boolean isTop) {
        Activity activity = UtilsBridge.getActivityByContext(fakeStatusBar.getContext());
        if (activity == null) {
            return;
        }
        transparentStatusBar(activity);
        drawer.setFitsSystemWindows(false);
        setStatusBarColor(fakeStatusBar, color);
        int count = drawer.getChildCount();
        for (int i = 0; i < count; i++) {
            drawer.getChildAt(i).setFitsSystemWindows(false);
        }
        if (isTop) {
            hideStatusBarView(activity);
        } else {
            setStatusBarColor(activity, color, false);
        }
    }

    private static View applyStatusBarColor(Activity activity, int color, boolean isDecor) {
        return applyStatusBarColor(activity.getWindow(), color, isDecor);
    }

    private static View applyStatusBarColor(Window window, int color, boolean isDecor) {
        ViewGroup parent;
        if (isDecor) {
            parent = (ViewGroup) window.getDecorView();
        } else {
            parent = (ViewGroup) window.findViewById(R.id.content);
        }
        View fakeStatusBarView = parent.findViewWithTag(TAG_STATUS_BAR);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == 8) {
                fakeStatusBarView.setVisibility(0);
            }
            fakeStatusBarView.setBackgroundColor(color);
            return fakeStatusBarView;
        }
        View fakeStatusBarView2 = createStatusBarView(window.getContext(), color);
        parent.addView(fakeStatusBarView2);
        return fakeStatusBarView2;
    }

    private static void hideStatusBarView(Activity activity) {
        hideStatusBarView(activity.getWindow());
    }

    private static void hideStatusBarView(Window window) {
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View fakeStatusBarView = decorView.findViewWithTag(TAG_STATUS_BAR);
        if (fakeStatusBarView == null) {
            return;
        }
        fakeStatusBarView.setVisibility(8);
    }

    private static void showStatusBarView(Window window) {
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View fakeStatusBarView = decorView.findViewWithTag(TAG_STATUS_BAR);
        if (fakeStatusBarView == null) {
            return;
        }
        fakeStatusBarView.setVisibility(0);
    }

    private static View createStatusBarView(Context context, int color) {
        View statusBarView = new View(context);
        statusBarView.setLayoutParams(new ViewGroup.LayoutParams(-1, getStatusBarHeight()));
        statusBarView.setBackgroundColor(color);
        statusBarView.setTag(TAG_STATUS_BAR);
        return statusBarView;
    }

    public static void transparentStatusBar(Activity activity) {
        transparentStatusBar(activity.getWindow());
    }

    public static void transparentStatusBar(Window window) {
        window.clearFlags(AccessibilityEventCompat.TYPE_VIEW_TARGETED_BY_SCROLL);
        window.addFlags(Integer.MIN_VALUE);
        int vis = window.getDecorView().getSystemUiVisibility();
        window.getDecorView().setSystemUiVisibility(1280 | vis);
        window.setStatusBarColor(0);
    }

    public static int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (Utils.getApp().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, Utils.getApp().getResources().getDisplayMetrics());
        }
        return 0;
    }

    public static void setNotificationBarVisibility(boolean isVisible) {
        String methodName;
        if (isVisible) {
            methodName = "expandNotificationsPanel";
        } else {
            methodName = "collapsePanels";
        }
        invokePanels(methodName);
    }

    private static void invokePanels(String methodName) {
        try {
            Object service = Utils.getApp().getSystemService("statusbar");
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusBarManager.getMethod(methodName, new Class[0]);
            expand.invoke(service, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getNavBarHeight() {
        Resources res = Resources.getSystem();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return res.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static void setNavBarVisibility(Activity activity, boolean isVisible) {
        setNavBarVisibility(activity.getWindow(), isVisible);
    }

    public static void setNavBarVisibility(Window window, boolean isVisible) {
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        int count = decorView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = decorView.getChildAt(i);
            int id = child.getId();
            if (id != -1) {
                String resourceEntryName = getResNameById(id);
                if ("navigationBarBackground".equals(resourceEntryName)) {
                    child.setVisibility(isVisible ? 0 : 4);
                }
            }
        }
        if (isVisible) {
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & (-4611));
        } else {
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | 4610);
        }
    }

    public static boolean isNavBarVisible(Activity activity) {
        return isNavBarVisible(activity.getWindow());
    }

    public static boolean isNavBarVisible(Window window) {
        boolean isVisible = false;
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        int i = 0;
        int count = decorView.getChildCount();
        while (true) {
            if (i >= count) {
                break;
            }
            View child = decorView.getChildAt(i);
            int id = child.getId();
            if (id != -1) {
                String resourceEntryName = getResNameById(id);
                if ("navigationBarBackground".equals(resourceEntryName) && child.getVisibility() == 0) {
                    isVisible = true;
                    break;
                }
            }
            i++;
        }
        if (isVisible) {
            if (UtilsBridge.isSamsung() && Build.VERSION.SDK_INT < 29) {
                try {
                    return Settings.Global.getInt(Utils.getApp().getContentResolver(), "navigationbar_hide_bar_enabled") == 0;
                } catch (Exception e) {
                }
            }
            int visibility = decorView.getSystemUiVisibility();
            boolean isVisible2 = (visibility & 2) == 0;
            return isVisible2;
        }
        return isVisible;
    }

    private static String getResNameById(int id) {
        try {
            return Utils.getApp().getResources().getResourceEntryName(id);
        } catch (Exception e) {
            return "";
        }
    }

    public static void setNavBarColor(Activity activity, int color) {
        setNavBarColor(activity.getWindow(), color);
    }

    public static void setNavBarColor(Window window, int color) {
        window.addFlags(Integer.MIN_VALUE);
        window.setNavigationBarColor(color);
    }

    public static int getNavBarColor(Activity activity) {
        return getNavBarColor(activity.getWindow());
    }

    public static int getNavBarColor(Window window) {
        return window.getNavigationBarColor();
    }

    public static boolean isSupportNavBar() {
        WindowManager wm = (WindowManager) Utils.getApp().getSystemService("window");
        if (wm == null) {
            return false;
        }
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();
        display.getSize(size);
        display.getRealSize(realSize);
        return (realSize.y == size.y && realSize.x == size.x) ? false : true;
    }

    public static void setNavBarLightMode(Activity activity, boolean isLightMode) {
        setNavBarLightMode(activity.getWindow(), isLightMode);
    }

    public static void setNavBarLightMode(Window window, boolean isLightMode) {
        int vis;
        View decorView = window.getDecorView();
        int vis2 = decorView.getSystemUiVisibility();
        if (isLightMode) {
            vis = vis2 | 16;
        } else {
            vis = vis2 & (-17);
        }
        decorView.setSystemUiVisibility(vis);
    }

    public static boolean isNavBarLightMode(Activity activity) {
        return isNavBarLightMode(activity.getWindow());
    }

    public static boolean isNavBarLightMode(Window window) {
        View decorView = window.getDecorView();
        int vis = decorView.getSystemUiVisibility();
        return (vis & 16) != 0;
    }

    public static void transparentNavBar(Activity activity) {
        transparentNavBar(activity.getWindow());
    }

    public static void transparentNavBar(Window window) {
        if (Build.VERSION.SDK_INT >= 29) {
            window.setNavigationBarContrastEnforced(false);
        }
        window.setNavigationBarColor(0);
        View decorView = window.getDecorView();
        int vis = decorView.getSystemUiVisibility();
        decorView.setSystemUiVisibility(vis | 1792);
    }
}

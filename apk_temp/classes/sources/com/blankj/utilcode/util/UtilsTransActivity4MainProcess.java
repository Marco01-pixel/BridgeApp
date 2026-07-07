package com.blankj.utilcode.util;

import android.app.Activity;
import android.content.Intent;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.UtilsTransActivity;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public class UtilsTransActivity4MainProcess extends UtilsTransActivity {
    public static void start(UtilsTransActivity.TransActivityDelegate delegate) {
        start(null, null, delegate, UtilsTransActivity4MainProcess.class);
    }

    public static void start(Utils.Consumer<Intent> consumer, UtilsTransActivity.TransActivityDelegate delegate) {
        start(null, consumer, delegate, UtilsTransActivity4MainProcess.class);
    }

    public static void start(Activity activity, UtilsTransActivity.TransActivityDelegate delegate) {
        start(activity, null, delegate, UtilsTransActivity4MainProcess.class);
    }

    public static void start(Activity activity, Utils.Consumer<Intent> consumer, UtilsTransActivity.TransActivityDelegate delegate) {
        start(activity, consumer, delegate, UtilsTransActivity4MainProcess.class);
    }
}

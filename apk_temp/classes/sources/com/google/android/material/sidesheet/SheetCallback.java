package com.google.android.material.sidesheet;

import android.view.View;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
interface SheetCallback {
    void onSlide(View view, float f);

    void onStateChanged(View view, int i);
}

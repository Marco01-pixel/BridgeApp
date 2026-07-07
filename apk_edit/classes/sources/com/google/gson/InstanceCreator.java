package com.google.gson;

import java.lang.reflect.Type;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_edit/classes.dex */
public interface InstanceCreator<T> {
    T createInstance(Type type);
}

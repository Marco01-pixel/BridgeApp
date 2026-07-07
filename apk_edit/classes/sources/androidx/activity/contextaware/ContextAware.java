package androidx.activity.contextaware;

import android.content.Context;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_edit/classes.dex */
public interface ContextAware {
    void addOnContextAvailableListener(OnContextAvailableListener onContextAvailableListener);

    Context peekAvailableContext();

    void removeOnContextAvailableListener(OnContextAvailableListener onContextAvailableListener);
}

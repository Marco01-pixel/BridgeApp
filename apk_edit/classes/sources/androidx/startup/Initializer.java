package androidx.startup;

import android.content.Context;
import java.util.List;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_edit/classes.dex */
public interface Initializer<T> {
    T create(Context context);

    List<Class<? extends Initializer<?>>> dependencies();
}

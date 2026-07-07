package androidx.core.content;

import android.content.res.Configuration;
import androidx.core.util.Consumer;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_edit/classes.dex */
public interface OnConfigurationChangedProvider {
    void addOnConfigurationChangedListener(Consumer<Configuration> consumer);

    void removeOnConfigurationChangedListener(Consumer<Configuration> consumer);
}

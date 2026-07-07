package com.blankj.utilcode.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.blankj.utilcode.util.Utils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public class NotificationUtils {
    public static final int IMPORTANCE_DEFAULT = 3;
    public static final int IMPORTANCE_HIGH = 4;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_MIN = 1;
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_UNSPECIFIED = -1000;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Importance {
    }

    public static boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(Utils.getApp()).areNotificationsEnabled();
    }

    public static void notify(int id, Utils.Consumer<NotificationCompat.Builder> consumer) {
        notify(null, id, ChannelConfig.DEFAULT_CHANNEL_CONFIG, consumer);
    }

    public static void notify(String tag, int id, Utils.Consumer<NotificationCompat.Builder> consumer) {
        notify(tag, id, ChannelConfig.DEFAULT_CHANNEL_CONFIG, consumer);
    }

    public static void notify(int id, ChannelConfig channelConfig, Utils.Consumer<NotificationCompat.Builder> consumer) {
        notify(null, id, channelConfig, consumer);
    }

    public static void notify(String tag, int id, ChannelConfig channelConfig, Utils.Consumer<NotificationCompat.Builder> consumer) {
        NotificationManagerCompat.from(Utils.getApp()).notify(tag, id, getNotification(channelConfig, consumer));
    }

    public static Notification getNotification(ChannelConfig channelConfig, Utils.Consumer<NotificationCompat.Builder> consumer) {
        NotificationManager nm = (NotificationManager) Utils.getApp().getSystemService("notification");
        nm.createNotificationChannel(channelConfig.getNotificationChannel());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.getApp());
        builder.setChannelId(channelConfig.mNotificationChannel.getId());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return builder.build();
    }

    public static void cancel(String tag, int id) {
        NotificationManagerCompat.from(Utils.getApp()).cancel(tag, id);
    }

    public static void cancel(int id) {
        NotificationManagerCompat.from(Utils.getApp()).cancel(id);
    }

    public static void cancelAll() {
        NotificationManagerCompat.from(Utils.getApp()).cancelAll();
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

    public static class ChannelConfig {
        public static final ChannelConfig DEFAULT_CHANNEL_CONFIG = new ChannelConfig(Utils.getApp().getPackageName(), Utils.getApp().getPackageName(), 3);
        private NotificationChannel mNotificationChannel;

        public ChannelConfig(String id, CharSequence name, int importance) {
            this.mNotificationChannel = new NotificationChannel(id, name, importance);
        }

        public NotificationChannel getNotificationChannel() {
            return this.mNotificationChannel;
        }

        public ChannelConfig setBypassDnd(boolean bypassDnd) {
            this.mNotificationChannel.setBypassDnd(bypassDnd);
            return this;
        }

        public ChannelConfig setDescription(String description) {
            this.mNotificationChannel.setDescription(description);
            return this;
        }

        public ChannelConfig setGroup(String groupId) {
            this.mNotificationChannel.setGroup(groupId);
            return this;
        }

        public ChannelConfig setImportance(int importance) {
            this.mNotificationChannel.setImportance(importance);
            return this;
        }

        public ChannelConfig setLightColor(int argb) {
            this.mNotificationChannel.setLightColor(argb);
            return this;
        }

        public ChannelConfig setLockscreenVisibility(int lockscreenVisibility) {
            this.mNotificationChannel.setLockscreenVisibility(lockscreenVisibility);
            return this;
        }

        public ChannelConfig setName(CharSequence name) {
            this.mNotificationChannel.setName(name);
            return this;
        }

        public ChannelConfig setShowBadge(boolean showBadge) {
            this.mNotificationChannel.setShowBadge(showBadge);
            return this;
        }

        public ChannelConfig setSound(Uri sound, AudioAttributes audioAttributes) {
            this.mNotificationChannel.setSound(sound, audioAttributes);
            return this;
        }

        public ChannelConfig setVibrationPattern(long[] vibrationPattern) {
            this.mNotificationChannel.setVibrationPattern(vibrationPattern);
            return this;
        }
    }
}

package com.blankj.utilcode.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import com.blankj.utilcode.util.ShellUtils;
import com.blankj.utilcode.util.Utils;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class NetworkUtils {
    private static final long SCAN_PERIOD_MILLIS = 3000;
    private static final Set<Utils.Consumer<WifiScanResults>> SCAN_RESULT_CONSUMERS = new CopyOnWriteArraySet();
    private static WifiScanResults sPreWifiScanResults;
    private static Timer sScanWifiTimer;

    public enum NetworkType {
        NETWORK_ETHERNET,
        NETWORK_WIFI,
        NETWORK_5G,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    public interface OnNetworkStatusChangedListener {
        void onConnected(NetworkType networkType);

        void onDisconnected();
    }

    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void openWirelessSettings() {
        Utils.getApp().startActivity(new Intent("android.settings.WIRELESS_SETTINGS").setFlags(268435456));
    }

    public static boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static Utils.Task<Boolean> isAvailableAsync(Utils.Consumer<Boolean> consumer) {
        return UtilsBridge.doAsync(new Utils.Task<Boolean>(consumer) { // from class: com.blankj.utilcode.util.NetworkUtils.1
            @Override // com.blankj.utilcode.util.ThreadUtils.Task
            public Boolean doInBackground() {
                return Boolean.valueOf(NetworkUtils.isAvailable());
            }
        });
    }

    public static boolean isAvailable() {
        return isAvailableByDns() || isAvailableByPing(null);
    }

    public static void isAvailableByPingAsync(Utils.Consumer<Boolean> consumer) {
        isAvailableByPingAsync("", consumer);
    }

    public static Utils.Task<Boolean> isAvailableByPingAsync(final String ip, Utils.Consumer<Boolean> consumer) {
        return UtilsBridge.doAsync(new Utils.Task<Boolean>(consumer) { // from class: com.blankj.utilcode.util.NetworkUtils.2
            @Override // com.blankj.utilcode.util.ThreadUtils.Task
            public Boolean doInBackground() {
                return Boolean.valueOf(NetworkUtils.isAvailableByPing(ip));
            }
        });
    }

    public static boolean isAvailableByPing() {
        return isAvailableByPing("");
    }

    public static boolean isAvailableByPing(String ip) {
        String realIp = TextUtils.isEmpty(ip) ? "223.5.5.5" : ip;
        ShellUtils.CommandResult result = ShellUtils.execCmd(String.format("ping -c 1 %s", realIp), false);
        return result.result == 0;
    }

    public static void isAvailableByDnsAsync(Utils.Consumer<Boolean> consumer) {
        isAvailableByDnsAsync("", consumer);
    }

    public static Utils.Task isAvailableByDnsAsync(final String domain, Utils.Consumer<Boolean> consumer) {
        return UtilsBridge.doAsync(new Utils.Task<Boolean>(consumer) { // from class: com.blankj.utilcode.util.NetworkUtils.3
            @Override // com.blankj.utilcode.util.ThreadUtils.Task
            public Boolean doInBackground() {
                return Boolean.valueOf(NetworkUtils.isAvailableByDns(domain));
            }
        });
    }

    public static boolean isAvailableByDns() {
        return isAvailableByDns("");
    }

    public static boolean isAvailableByDns(String domain) {
        String realDomain = TextUtils.isEmpty(domain) ? "www.baidu.com" : domain;
        try {
            InetAddress inetAddress = InetAddress.getByName(realDomain);
            return inetAddress != null;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean getMobileDataEnabled() {
        try {
            TelephonyManager tm = (TelephonyManager) Utils.getApp().getSystemService("phone");
            if (tm == null) {
                return false;
            }
            return tm.isDataEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isBehindProxy() {
        return (System.getProperty("http.proxyHost") == null || System.getProperty("http.proxyPort") == null) ? false : true;
    }

    public static boolean isUsingVPN() {
        ConnectivityManager cm = (ConnectivityManager) Utils.getApp().getSystemService("connectivity");
        if (Build.VERSION.SDK_INT >= 28) {
            return cm.getNetworkInfo(17).isConnectedOrConnecting();
        }
        return cm.getNetworkInfo(4).isConnectedOrConnecting();
    }

    public static boolean isMobileData() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.getType() == 0;
    }

    public static boolean is4G() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.getSubtype() == 13;
    }

    public static boolean is5G() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.getSubtype() == 20;
    }

    public static boolean getWifiEnabled() {
        WifiManager manager = (WifiManager) Utils.getApp().getSystemService("wifi");
        if (manager == null) {
            return false;
        }
        return manager.isWifiEnabled();
    }

    public static void setWifiEnabled(boolean enabled) {
        WifiManager manager = (WifiManager) Utils.getApp().getSystemService("wifi");
        if (manager == null || enabled == manager.isWifiEnabled()) {
            return;
        }
        manager.setWifiEnabled(enabled);
    }

    public static boolean isWifiConnected() {
        NetworkInfo ni;
        ConnectivityManager cm = (ConnectivityManager) Utils.getApp().getSystemService("connectivity");
        return (cm == null || (ni = cm.getActiveNetworkInfo()) == null || ni.getType() != 1) ? false : true;
    }

    public static boolean isWifiAvailable() {
        return getWifiEnabled() && isAvailable();
    }

    public static Utils.Task<Boolean> isWifiAvailableAsync(Utils.Consumer<Boolean> consumer) {
        return UtilsBridge.doAsync(new Utils.Task<Boolean>(consumer) { // from class: com.blankj.utilcode.util.NetworkUtils.4
            @Override // com.blankj.utilcode.util.ThreadUtils.Task
            public Boolean doInBackground() {
                return Boolean.valueOf(NetworkUtils.isWifiAvailable());
            }
        });
    }

    public static String getNetworkOperatorName() {
        TelephonyManager tm = (TelephonyManager) Utils.getApp().getSystemService("phone");
        return tm == null ? "" : tm.getNetworkOperatorName();
    }

    public static NetworkType getNetworkType() {
        if (isEthernet()) {
            return NetworkType.NETWORK_ETHERNET;
        }
        NetworkInfo info = getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == 1) {
                return NetworkType.NETWORK_WIFI;
            }
            if (info.getType() == 0) {
                switch (info.getSubtype()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                    case 16:
                        return NetworkType.NETWORK_2G;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                    case 17:
                        return NetworkType.NETWORK_3G;
                    case 13:
                    case 18:
                        return NetworkType.NETWORK_4G;
                    case 19:
                    default:
                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA") || subtypeName.equalsIgnoreCase("WCDMA") || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            return NetworkType.NETWORK_3G;
                        }
                        return NetworkType.NETWORK_UNKNOWN;
                    case 20:
                        return NetworkType.NETWORK_5G;
                }
            }
            return NetworkType.NETWORK_UNKNOWN;
        }
        return NetworkType.NETWORK_NO;
    }

    private static boolean isEthernet() {
        NetworkInfo info;
        NetworkInfo.State state;
        ConnectivityManager cm = (ConnectivityManager) Utils.getApp().getSystemService("connectivity");
        if (cm == null || (info = cm.getNetworkInfo(9)) == null || (state = info.getState()) == null) {
            return false;
        }
        return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING;
    }

    private static NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) Utils.getApp().getSystemService("connectivity");
        if (cm == null) {
            return null;
        }
        return cm.getActiveNetworkInfo();
    }

    public static Utils.Task<String> getIPAddressAsync(final boolean useIPv4, Utils.Consumer<String> consumer) {
        return UtilsBridge.doAsync(new Utils.Task<String>(consumer) { // from class: com.blankj.utilcode.util.NetworkUtils.5
            @Override // com.blankj.utilcode.util.ThreadUtils.Task
            public String doInBackground() {
                return NetworkUtils.getIPAddress(useIPv4);
            }
        });
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (ni.isUp() && !ni.isLoopback()) {
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        adds.addFirst(addresses.nextElement());
                    }
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(58) < 0;
                    if (useIPv4) {
                        if (isIPv4) {
                            return hostAddress;
                        }
                    } else if (!isIPv4) {
                        int index = hostAddress.indexOf(37);
                        if (index < 0) {
                            return hostAddress.toUpperCase();
                        }
                        return hostAddress.substring(0, index).toUpperCase();
                    }
                }
            }
            return "";
        } catch (SocketException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getBroadcastIpAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            new LinkedList();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (ni.isUp() && !ni.isLoopback()) {
                    List<InterfaceAddress> ias = ni.getInterfaceAddresses();
                    int size = ias.size();
                    for (int i = 0; i < size; i++) {
                        InterfaceAddress ia = ias.get(i);
                        InetAddress broadcast = ia.getBroadcast();
                        if (broadcast != null) {
                            return broadcast.getHostAddress();
                        }
                    }
                }
            }
            return "";
        } catch (SocketException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Utils.Task<String> getDomainAddressAsync(final String domain, Utils.Consumer<String> consumer) {
        return UtilsBridge.doAsync(new Utils.Task<String>(consumer) { // from class: com.blankj.utilcode.util.NetworkUtils.6
            @Override // com.blankj.utilcode.util.ThreadUtils.Task
            public String doInBackground() {
                return NetworkUtils.getDomainAddress(domain);
            }
        });
    }

    public static String getDomainAddress(String domain) {
        try {
            InetAddress inetAddress = InetAddress.getByName(domain);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getIpAddressByWifi() {
        WifiManager wm = (WifiManager) Utils.getApp().getSystemService("wifi");
        return wm == null ? "" : Formatter.formatIpAddress(wm.getDhcpInfo().ipAddress);
    }

    public static String getGatewayByWifi() {
        WifiManager wm = (WifiManager) Utils.getApp().getSystemService("wifi");
        return wm == null ? "" : Formatter.formatIpAddress(wm.getDhcpInfo().gateway);
    }

    public static String getNetMaskByWifi() {
        WifiManager wm = (WifiManager) Utils.getApp().getSystemService("wifi");
        return wm == null ? "" : Formatter.formatIpAddress(wm.getDhcpInfo().netmask);
    }

    public static String getServerAddressByWifi() {
        WifiManager wm = (WifiManager) Utils.getApp().getSystemService("wifi");
        return wm == null ? "" : Formatter.formatIpAddress(wm.getDhcpInfo().serverAddress);
    }

    public static String getSSID() {
        WifiInfo wi;
        WifiManager wm = (WifiManager) Utils.getApp().getApplicationContext().getSystemService("wifi");
        if (wm == null || (wi = wm.getConnectionInfo()) == null) {
            return "";
        }
        String ssid = wi.getSSID();
        if (TextUtils.isEmpty(ssid)) {
            return "";
        }
        if (ssid.length() > 2 && ssid.charAt(0) == '\"' && ssid.charAt(ssid.length() - 1) == '\"') {
            return ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    public static void registerNetworkStatusChangedListener(OnNetworkStatusChangedListener listener) {
        NetworkChangedReceiver.getInstance().registerListener(listener);
    }

    public static boolean isRegisteredNetworkStatusChangedListener(OnNetworkStatusChangedListener listener) {
        return NetworkChangedReceiver.getInstance().isRegistered(listener);
    }

    public static void unregisterNetworkStatusChangedListener(OnNetworkStatusChangedListener listener) {
        NetworkChangedReceiver.getInstance().unregisterListener(listener);
    }

    public static WifiScanResults getWifiScanResult() {
        WifiScanResults result = new WifiScanResults();
        if (!getWifiEnabled()) {
            return result;
        }
        WifiManager wm = (WifiManager) Utils.getApp().getSystemService("wifi");
        List<ScanResult> results = wm.getScanResults();
        if (results != null) {
            result.setAllResults(results);
        }
        return result;
    }

    public static void addOnWifiChangedConsumer(final Utils.Consumer<WifiScanResults> consumer) {
        if (consumer == null) {
            return;
        }
        UtilsBridge.runOnUiThread(new Runnable() { // from class: com.blankj.utilcode.util.NetworkUtils.7
            @Override // java.lang.Runnable
            public void run() {
                if (NetworkUtils.SCAN_RESULT_CONSUMERS.isEmpty()) {
                    NetworkUtils.SCAN_RESULT_CONSUMERS.add(consumer);
                    NetworkUtils.startScanWifi();
                } else {
                    consumer.accept(NetworkUtils.sPreWifiScanResults);
                    NetworkUtils.SCAN_RESULT_CONSUMERS.add(consumer);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void startScanWifi() {
        sPreWifiScanResults = new WifiScanResults();
        Timer timer = new Timer();
        sScanWifiTimer = timer;
        timer.schedule(new TimerTask() { // from class: com.blankj.utilcode.util.NetworkUtils.8
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                NetworkUtils.startScanWifiIfEnabled();
                WifiScanResults scanResults = NetworkUtils.getWifiScanResult();
                if (!NetworkUtils.isSameScanResults(NetworkUtils.sPreWifiScanResults.allResults, scanResults.allResults)) {
                    WifiScanResults unused = NetworkUtils.sPreWifiScanResults = scanResults;
                    UtilsBridge.runOnUiThread(new Runnable() { // from class: com.blankj.utilcode.util.NetworkUtils.8.1
                        @Override // java.lang.Runnable
                        public void run() {
                            for (Utils.Consumer<WifiScanResults> consumer : NetworkUtils.SCAN_RESULT_CONSUMERS) {
                                consumer.accept(NetworkUtils.sPreWifiScanResults);
                            }
                        }
                    });
                }
            }
        }, 0L, SCAN_PERIOD_MILLIS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void startScanWifiIfEnabled() {
        if (getWifiEnabled()) {
            WifiManager wm = (WifiManager) Utils.getApp().getSystemService("wifi");
            wm.startScan();
        }
    }

    public static void removeOnWifiChangedConsumer(final Utils.Consumer<WifiScanResults> consumer) {
        if (consumer == null) {
            return;
        }
        UtilsBridge.runOnUiThread(new Runnable() { // from class: com.blankj.utilcode.util.NetworkUtils.9
            @Override // java.lang.Runnable
            public void run() {
                NetworkUtils.SCAN_RESULT_CONSUMERS.remove(consumer);
                if (NetworkUtils.SCAN_RESULT_CONSUMERS.isEmpty()) {
                    NetworkUtils.stopScanWifi();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void stopScanWifi() {
        Timer timer = sScanWifiTimer;
        if (timer != null) {
            timer.cancel();
            sScanWifiTimer = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isSameScanResults(List<ScanResult> l1, List<ScanResult> l2) {
        if (l1 == null && l2 == null) {
            return true;
        }
        if (l1 == null || l2 == null || l1.size() != l2.size()) {
            return false;
        }
        for (int i = 0; i < l1.size(); i++) {
            ScanResult r1 = l1.get(i);
            ScanResult r2 = l2.get(i);
            if (!isSameScanResultContent(r1, r2)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSameScanResultContent(ScanResult r1, ScanResult r2) {
        return r1 != null && r2 != null && UtilsBridge.equals(r1.BSSID, r2.BSSID) && UtilsBridge.equals(r1.SSID, r2.SSID) && UtilsBridge.equals(r1.capabilities, r2.capabilities) && r1.level == r2.level;
    }

    public static final class NetworkChangedReceiver extends BroadcastReceiver {
        private Set<OnNetworkStatusChangedListener> mListeners = new HashSet();
        private NetworkType mType;

        /* JADX INFO: Access modifiers changed from: private */
        public static NetworkChangedReceiver getInstance() {
            return LazyHolder.INSTANCE;
        }

        void registerListener(final OnNetworkStatusChangedListener listener) {
            if (listener == null) {
                return;
            }
            UtilsBridge.runOnUiThread(new Runnable() { // from class: com.blankj.utilcode.util.NetworkUtils.NetworkChangedReceiver.1
                @Override // java.lang.Runnable
                public void run() {
                    int preSize = NetworkChangedReceiver.this.mListeners.size();
                    NetworkChangedReceiver.this.mListeners.add(listener);
                    if (preSize == 0 && NetworkChangedReceiver.this.mListeners.size() == 1) {
                        NetworkChangedReceiver.this.mType = NetworkUtils.getNetworkType();
                        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
                        Utils.getApp().registerReceiver(NetworkChangedReceiver.getInstance(), intentFilter);
                    }
                }
            });
        }

        boolean isRegistered(OnNetworkStatusChangedListener listener) {
            if (listener == null) {
                return false;
            }
            return this.mListeners.contains(listener);
        }

        void unregisterListener(final OnNetworkStatusChangedListener listener) {
            if (listener == null) {
                return;
            }
            UtilsBridge.runOnUiThread(new Runnable() { // from class: com.blankj.utilcode.util.NetworkUtils.NetworkChangedReceiver.2
                @Override // java.lang.Runnable
                public void run() {
                    int preSize = NetworkChangedReceiver.this.mListeners.size();
                    NetworkChangedReceiver.this.mListeners.remove(listener);
                    if (preSize == 1 && NetworkChangedReceiver.this.mListeners.size() == 0) {
                        Utils.getApp().unregisterReceiver(NetworkChangedReceiver.getInstance());
                    }
                }
            });
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                UtilsBridge.runOnUiThreadDelayed(new Runnable() { // from class: com.blankj.utilcode.util.NetworkUtils.NetworkChangedReceiver.3
                    @Override // java.lang.Runnable
                    public void run() {
                        NetworkType networkType = NetworkUtils.getNetworkType();
                        if (NetworkChangedReceiver.this.mType == networkType) {
                            return;
                        }
                        NetworkChangedReceiver.this.mType = networkType;
                        if (networkType == NetworkType.NETWORK_NO) {
                            for (OnNetworkStatusChangedListener listener : NetworkChangedReceiver.this.mListeners) {
                                listener.onDisconnected();
                            }
                            return;
                        }
                        for (OnNetworkStatusChangedListener listener2 : NetworkChangedReceiver.this.mListeners) {
                            listener2.onConnected(networkType);
                        }
                    }
                }, 1000L);
            }
        }

        private static class LazyHolder {
            private static final NetworkChangedReceiver INSTANCE = new NetworkChangedReceiver();

            private LazyHolder() {
            }
        }
    }

    public static final class WifiScanResults {
        private List<ScanResult> allResults = new ArrayList();
        private List<ScanResult> filterResults = new ArrayList();

        public List<ScanResult> getAllResults() {
            return this.allResults;
        }

        public List<ScanResult> getFilterResults() {
            return this.filterResults;
        }

        public void setAllResults(List<ScanResult> allResults) {
            this.allResults = allResults;
            this.filterResults = filterScanResult(allResults);
        }

        private static List<ScanResult> filterScanResult(List<ScanResult> results) {
            ScanResult resultInMap;
            if (results == null || results.isEmpty()) {
                return new ArrayList();
            }
            LinkedHashMap<String, ScanResult> map = new LinkedHashMap<>(results.size());
            for (ScanResult result : results) {
                if (!TextUtils.isEmpty(result.SSID) && ((resultInMap = map.get(result.SSID)) == null || resultInMap.level < result.level)) {
                    map.put(result.SSID, result);
                }
            }
            return new ArrayList(map.values());
        }
    }
}

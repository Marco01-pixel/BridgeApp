
package com.radar.bridge;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BridgeService extends Service {

    private static final int PORT = 9876;
    private static final int FOREGROUND_ID = 1001;
    private static final String CHANNEL_ID = "radar_bridge_channel";
    private WebServer server;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private BluetoothLeAdvertiser advertiser;
    private final Map<String, Object> lastScanResults = new HashMap<>();
    private Gson gson = new Gson();
    private AdvertiseCallback advertiseCallback;
    private String originalAdapterName;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(FOREGROUND_ID, buildNotification());

        try { Thread.sleep(50); } catch (InterruptedException ignored) {}

        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = manager.getAdapter();
        if (bluetoothAdapter != null) {
            originalAdapterName = bluetoothAdapter.getName();
            bluetoothAdapter.setName("RadarBridge");

            scanner = bluetoothAdapter.getBluetoothLeScanner();
            advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        }
        try {
            server = new WebServer(PORT);
            server.start();
            Log.d("BridgeService", "Servidor HTTP iniciado en puerto " + PORT);
        } catch (IOException e) {
            Log.e("BridgeService", "Error iniciando servidor", e);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Radar Bridge Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        return new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Radar Bridge activo")
                .setContentText("Escaneando y transmitiendo...")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null && originalAdapterName != null) {
            bluetoothAdapter.setName(originalAdapterName);
        }
        if (server != null) server.stop();
    }

    private class WebServer extends NanoHTTPD {
        public WebServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();
            Map<String, String> params = session.getParms();
            try {
                if (uri.equals("/ping")) {
                    return newFixedLengthResponse(Response.Status.OK, "text/plain", "pong");
                } else if (uri.equals("/scan")) {
                    if (scanner == null) return newFixedLengthResponse(Response.Status.SERVICE_UNAVAILABLE, "text/plain", "BLE scanner no disponible");
                    List<Map<String, Object>> devices = new ArrayList<>();
                    scanner.startScan(new ScanCallback() {
                        @Override
                        public void onScanResult(int callbackType, ScanResult result) {
                            BluetoothDevice device = result.getDevice();
                            Map<String, Object> devMap = new HashMap<>();
                            devMap.put("mac", device.getAddress());
                            devMap.put("name", device.getName());
                            devMap.put("rssi", result.getRssi());
                            devices.add(devMap);
                        }
                    });
                    try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                    scanner.stopScan(new ScanCallback() {});
                    lastScanResults.put("devices", devices);
                    lastScanResults.put("timestamp", System.currentTimeMillis());
                    String json = gson.toJson(lastScanResults);
                    return newFixedLengthResponse(Response.Status.OK, "application/json", json);
                } else if (uri.equals("/advertise")) {
                    String payload = params.get("payload");
                    if (payload == null || payload.isEmpty()) return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Falta payload");
                    if (advertiser == null) return newFixedLengthResponse(Response.Status.SERVICE_UNAVAILABLE, "text/plain", "Advertiser no disponible");
                    byte[] data = Base64.decode(payload, Base64.DEFAULT);
                    AdvertiseSettings settings = new AdvertiseSettings.Builder()
                            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                            .setConnectable(false)
                            .build();
                    AdvertiseData advData = new AdvertiseData.Builder()
                            .addServiceData(new ParcelUuid(UUID.fromString("0000ffff-0000-1000-8000-00805f9b34fb")), data)
                            .setIncludeDeviceName(true)
                            .build();
                    advertiseCallback = new AdvertiseCallback() {
                        @Override
                        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                            super.onStartSuccess(settingsInEffect);
                        }
                        @Override
                        public void onStartFailure(int errorCode) {
                            super.onStartFailure(errorCode);
                        }
                    };
                    advertiser.startAdvertising(settings, advData, advertiseCallback);
                    return newFixedLengthResponse(Response.Status.OK, "text/plain", "Advertising iniciado");
                } else {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Endpoint no encontrado");
                }
            } catch (Exception e) {
                Log.e("BridgeService", "Error en endpoint", e);
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.getMessage());
            }
        }
    }
}

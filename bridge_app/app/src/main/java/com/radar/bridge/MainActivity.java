package com.radar.bridge;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndRequestLocationPermission();
    }

    private void checkAndRequestLocationPermission() {
        if (hasLocationPermission()) {
            startBridgeService();
            finish();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            showLocationRationaleDialog();
        } else {
            requestLocationPermission();
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
    }

    private void showLocationRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso de ubicación necesario")
                .setMessage("RadarBridge necesita acceder a la ubicación para escanear dispositivos Bluetooth. Es obligatorio para la detección de demanda.")
                .setPositiveButton("Conceder", (dialog, which) -> requestLocationPermission())
                .setNegativeButton("Salir", (dialog, which) -> finish())
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (hasLocationPermission()) {
                startBridgeService();
                finish();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showPermissionDeniedForeverDialog();
                } else {
                    finish();
                }
            }
        }
    }

    private void showPermissionDeniedForeverDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso denegado permanentemente")
                .setMessage("Sin permiso de ubicación la app no funciona. Ve a Ajustes > Aplicaciones > RadarBridge > Permisos y activa Ubicación.")
                .setPositiveButton("Ir a Ajustes", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Salir", (dialog, which) -> finish())
                .show();
    }

    private void startBridgeService() {
        Intent intent = new Intent(this, BridgeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        // Mostrar aviso sobre orígenes desconocidos la primera vez
        if (getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("first_run", true)) {
            getSharedPreferences("prefs", MODE_PRIVATE).edit().putBoolean("first_run", false).apply();
            showUnknownSourcesDialog();
        }
    }

    private void showUnknownSourcesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Instalación de apps externas")
                .setMessage("Para instalar actualizaciones de RadarBridge, debes permitir que Termux (o tu gestor de archivos) instale apps de orígenes desconocidos.\n\nVe a Ajustes > Aplicaciones > Acceso especial a apps > Instalar aplicaciones desconocidas y activa la opción correspondiente.")
                .setPositiveButton("Ir a ajustes", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    startActivity(intent);
                })
                .setNegativeButton("Más tarde", null)
                .show();
    }
}

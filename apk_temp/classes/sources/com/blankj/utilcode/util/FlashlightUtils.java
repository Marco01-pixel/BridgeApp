package com.blankj.utilcode.util;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import java.io.IOException;
import kotlinx.coroutines.DebugKt;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class FlashlightUtils {
    private static Camera mCamera;
    private static SurfaceTexture mSurfaceTexture;

    private FlashlightUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isFlashlightEnable() {
        return Utils.getApp().getPackageManager().hasSystemFeature("android.hardware.camera.flash");
    }

    public static boolean isFlashlightOn() {
        if (!init()) {
            return false;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        return "torch".equals(parameters.getFlashMode());
    }

    public static void setFlashlightStatus(boolean isOn) {
        if (init()) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (isOn) {
                if (!"torch".equals(parameters.getFlashMode())) {
                    try {
                        mCamera.setPreviewTexture(mSurfaceTexture);
                        mCamera.startPreview();
                        parameters.setFlashMode("torch");
                        mCamera.setParameters(parameters);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                return;
            }
            if (!DebugKt.DEBUG_PROPERTY_VALUE_OFF.equals(parameters.getFlashMode())) {
                parameters.setFlashMode(DebugKt.DEBUG_PROPERTY_VALUE_OFF);
                mCamera.setParameters(parameters);
            }
        }
    }

    public static void destroy() {
        Camera camera = mCamera;
        if (camera == null) {
            return;
        }
        camera.release();
        mSurfaceTexture = null;
        mCamera = null;
    }

    private static boolean init() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(0);
                mSurfaceTexture = new SurfaceTexture(0);
            } catch (Throwable t) {
                Log.e("FlashlightUtils", "init failed: ", t);
                return false;
            }
        }
        if (mCamera == null) {
            Log.e("FlashlightUtils", "init failed.");
            return false;
        }
        return true;
    }
}

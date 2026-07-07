package com.blankj.utilcode.util;

import android.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.SystemClock;
import android.util.Log;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import androidx.core.view.ViewCompat;
import com.blankj.utilcode.util.ShadowUtils;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public class ClickUtils {
    private static final long DEBOUNCING_DEFAULT_VALUE = 1000;
    private static final float PRESSED_BG_ALPHA_DEFAULT_VALUE = 0.9f;
    private static final int PRESSED_BG_ALPHA_STYLE = 4;
    private static final float PRESSED_BG_DARK_DEFAULT_VALUE = 0.9f;
    private static final int PRESSED_BG_DARK_STYLE = 5;
    private static final float PRESSED_VIEW_ALPHA_DEFAULT_VALUE = 0.8f;
    private static final int PRESSED_VIEW_ALPHA_SRC_TAG = -3;
    private static final int PRESSED_VIEW_ALPHA_TAG = -2;
    private static final float PRESSED_VIEW_SCALE_DEFAULT_VALUE = -0.06f;
    private static final int PRESSED_VIEW_SCALE_TAG = -1;
    private static final long TIP_DURATION = 2000;
    private static int sClickCount;
    private static long sLastClickMillis;

    public interface Back2HomeFriendlyListener {
        public static final Back2HomeFriendlyListener DEFAULT = new Back2HomeFriendlyListener() { // from class: com.blankj.utilcode.util.ClickUtils.Back2HomeFriendlyListener.1
            @Override // com.blankj.utilcode.util.ClickUtils.Back2HomeFriendlyListener
            public void show(CharSequence text, long duration) {
                UtilsBridge.toastShowShort(text);
            }

            @Override // com.blankj.utilcode.util.ClickUtils.Back2HomeFriendlyListener
            public void dismiss() {
                UtilsBridge.toastCancel();
            }
        };

        void dismiss();

        void show(CharSequence charSequence, long j);
    }

    private ClickUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void applyPressedViewScale(View... views) {
        applyPressedViewScale(views, (float[]) null);
    }

    public static void applyPressedViewScale(View[] views, float[] scaleFactors) {
        if (views == null || views.length == 0) {
            return;
        }
        for (int i = 0; i < views.length; i++) {
            if (scaleFactors == null || i >= scaleFactors.length) {
                applyPressedViewScale(views[i], PRESSED_VIEW_SCALE_DEFAULT_VALUE);
            } else {
                applyPressedViewScale(views[i], scaleFactors[i]);
            }
        }
    }

    public static void applyPressedViewScale(View view, float scaleFactor) {
        if (view == null) {
            return;
        }
        view.setTag(-1, Float.valueOf(scaleFactor));
        view.setClickable(true);
        view.setOnTouchListener(OnUtilsTouchListener.getInstance());
    }

    public static void applyPressedViewAlpha(View... views) {
        applyPressedViewAlpha(views, (float[]) null);
    }

    public static void applyPressedViewAlpha(View[] views, float[] alphas) {
        if (views == null || views.length == 0) {
            return;
        }
        for (int i = 0; i < views.length; i++) {
            if (alphas == null || i >= alphas.length) {
                applyPressedViewAlpha(views[i], PRESSED_VIEW_ALPHA_DEFAULT_VALUE);
            } else {
                applyPressedViewAlpha(views[i], alphas[i]);
            }
        }
    }

    public static void applyPressedViewAlpha(View view, float alpha) {
        if (view == null) {
            return;
        }
        view.setTag(-2, Float.valueOf(alpha));
        view.setTag(-3, Float.valueOf(view.getAlpha()));
        view.setClickable(true);
        view.setOnTouchListener(OnUtilsTouchListener.getInstance());
    }

    public static void applyPressedBgAlpha(View view) {
        applyPressedBgAlpha(view, 0.9f);
    }

    public static void applyPressedBgAlpha(View view, float alpha) {
        applyPressedBgStyle(view, 4, alpha);
    }

    public static void applyPressedBgDark(View view) {
        applyPressedBgDark(view, 0.9f);
    }

    public static void applyPressedBgDark(View view, float darkAlpha) {
        applyPressedBgStyle(view, 5, darkAlpha);
    }

    private static void applyPressedBgStyle(View view, int style, float value) {
        if (view == null) {
            return;
        }
        Drawable background = view.getBackground();
        Object tag = view.getTag(-style);
        if (tag instanceof Drawable) {
            ViewCompat.setBackground(view, (Drawable) tag);
            return;
        }
        Drawable background2 = createStyleDrawable(background, style, value);
        ViewCompat.setBackground(view, background2);
        view.setTag(-style, background2);
    }

    private static Drawable createStyleDrawable(Drawable src, int style, float value) {
        if (src == null) {
            src = new ColorDrawable(0);
        }
        if (src.getConstantState() == null) {
            return src;
        }
        Drawable pressed = src.getConstantState().newDrawable().mutate();
        if (style == 4) {
            pressed = createAlphaDrawable(pressed, value);
        } else if (style == 5) {
            pressed = createDarkDrawable(pressed, value);
        }
        Drawable disable = src.getConstantState().newDrawable().mutate();
        Drawable disable2 = createAlphaDrawable(disable, 0.5f);
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{R.attr.state_pressed}, pressed);
        drawable.addState(new int[]{-16842910}, disable2);
        drawable.addState(StateSet.WILD_CARD, src);
        return drawable;
    }

    private static Drawable createAlphaDrawable(Drawable drawable, float alpha) {
        ClickDrawableWrapper drawableWrapper = new ClickDrawableWrapper(drawable);
        drawableWrapper.setAlpha((int) (255.0f * alpha));
        return drawableWrapper;
    }

    private static Drawable createDarkDrawable(Drawable drawable, float alpha) {
        ClickDrawableWrapper drawableWrapper = new ClickDrawableWrapper(drawable);
        drawableWrapper.setColorFilter(getDarkColorFilter(alpha));
        return drawableWrapper;
    }

    private static ColorMatrixColorFilter getDarkColorFilter(float darkAlpha) {
        return new ColorMatrixColorFilter(new ColorMatrix(new float[]{darkAlpha, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, darkAlpha, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, darkAlpha, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 0.0f}));
    }

    public static void applySingleDebouncing(View view, View.OnClickListener listener) {
        applySingleDebouncing(new View[]{view}, listener);
    }

    public static void applySingleDebouncing(View view, long duration, View.OnClickListener listener) {
        applySingleDebouncing(new View[]{view}, duration, listener);
    }

    public static void applySingleDebouncing(View[] views, View.OnClickListener listener) {
        applySingleDebouncing(views, DEBOUNCING_DEFAULT_VALUE, listener);
    }

    public static void applySingleDebouncing(View[] views, long duration, View.OnClickListener listener) {
        applyDebouncing(views, false, duration, listener);
    }

    public static void applyGlobalDebouncing(View view, View.OnClickListener listener) {
        applyGlobalDebouncing(new View[]{view}, listener);
    }

    public static void applyGlobalDebouncing(View view, long duration, View.OnClickListener listener) {
        applyGlobalDebouncing(new View[]{view}, duration, listener);
    }

    public static void applyGlobalDebouncing(View[] views, View.OnClickListener listener) {
        applyGlobalDebouncing(views, DEBOUNCING_DEFAULT_VALUE, listener);
    }

    public static void applyGlobalDebouncing(View[] views, long duration, View.OnClickListener listener) {
        applyDebouncing(views, true, duration, listener);
    }

    private static void applyDebouncing(View[] views, boolean isGlobal, long duration, final View.OnClickListener listener) {
        if (views == null || views.length == 0 || listener == null) {
            return;
        }
        for (View view : views) {
            if (view != null) {
                view.setOnClickListener(new OnDebouncingClickListener(isGlobal, duration) { // from class: com.blankj.utilcode.util.ClickUtils.1
                    @Override // com.blankj.utilcode.util.ClickUtils.OnDebouncingClickListener
                    public void onDebouncingClick(View v) {
                        listener.onClick(v);
                    }
                });
            }
        }
    }

    public static void expandClickArea(View view, int expandSize) {
        expandClickArea(view, expandSize, expandSize, expandSize, expandSize);
    }

    public static void expandClickArea(final View view, final int expandSizeTop, final int expandSizeLeft, final int expandSizeRight, final int expandSizeBottom) {
        final View parentView = (View) view.getParent();
        if (parentView == null) {
            Log.e("ClickUtils", "expandClickArea must have parent view.");
        } else {
            parentView.post(new Runnable() { // from class: com.blankj.utilcode.util.ClickUtils.2
                @Override // java.lang.Runnable
                public void run() {
                    Rect rect = new Rect();
                    view.getHitRect(rect);
                    rect.top -= expandSizeTop;
                    rect.bottom += expandSizeBottom;
                    rect.left -= expandSizeLeft;
                    rect.right += expandSizeRight;
                    parentView.setTouchDelegate(new TouchDelegate(rect, view));
                }
            });
        }
    }

    public static void back2HomeFriendly(CharSequence tip) {
        back2HomeFriendly(tip, TIP_DURATION, Back2HomeFriendlyListener.DEFAULT);
    }

    public static void back2HomeFriendly(CharSequence tip, long duration, Back2HomeFriendlyListener listener) {
        long nowMillis = SystemClock.elapsedRealtime();
        if (Math.abs(nowMillis - sLastClickMillis) < duration) {
            int i = sClickCount + 1;
            sClickCount = i;
            if (i == 2) {
                UtilsBridge.startHomeActivity();
                listener.dismiss();
                sLastClickMillis = 0L;
                return;
            }
            return;
        }
        sClickCount = 1;
        listener.show(tip, duration);
        sLastClickMillis = nowMillis;
    }

    public static abstract class OnDebouncingClickListener implements View.OnClickListener {
        private long mDuration;
        private boolean mIsGlobal;
        private static boolean mEnabled = true;
        private static final Runnable ENABLE_AGAIN = new Runnable() { // from class: com.blankj.utilcode.util.ClickUtils.OnDebouncingClickListener.1
            @Override // java.lang.Runnable
            public void run() {
                boolean unused = OnDebouncingClickListener.mEnabled = true;
            }
        };

        public abstract void onDebouncingClick(View view);

        private static boolean isValid(View view, long duration) {
            return UtilsBridge.isValid(view, duration);
        }

        public OnDebouncingClickListener() {
            this(true, ClickUtils.DEBOUNCING_DEFAULT_VALUE);
        }

        public OnDebouncingClickListener(boolean isGlobal) {
            this(isGlobal, ClickUtils.DEBOUNCING_DEFAULT_VALUE);
        }

        public OnDebouncingClickListener(long duration) {
            this(true, duration);
        }

        public OnDebouncingClickListener(boolean isGlobal, long duration) {
            this.mIsGlobal = isGlobal;
            this.mDuration = duration;
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View v) {
            if (this.mIsGlobal) {
                if (mEnabled) {
                    mEnabled = false;
                    v.postDelayed(ENABLE_AGAIN, this.mDuration);
                    onDebouncingClick(v);
                    return;
                }
                return;
            }
            if (isValid(v, this.mDuration)) {
                onDebouncingClick(v);
            }
        }
    }

    public static abstract class OnMultiClickListener implements View.OnClickListener {
        private static final long INTERVAL_DEFAULT_VALUE = 666;
        private int mClickCount;
        private final long mClickInterval;
        private long mLastClickTime;
        private final int mTriggerClickCount;

        public abstract void onBeforeTriggerClick(View view, int i);

        public abstract void onTriggerClick(View view);

        public OnMultiClickListener(int triggerClickCount) {
            this(triggerClickCount, INTERVAL_DEFAULT_VALUE);
        }

        public OnMultiClickListener(int triggerClickCount, long clickInterval) {
            this.mTriggerClickCount = triggerClickCount;
            this.mClickInterval = clickInterval;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (this.mTriggerClickCount <= 1) {
                onTriggerClick(v);
                return;
            }
            long curTime = System.currentTimeMillis();
            if (curTime - this.mLastClickTime < this.mClickInterval) {
                int i = this.mClickCount + 1;
                this.mClickCount = i;
                int i2 = this.mTriggerClickCount;
                if (i == i2) {
                    onTriggerClick(v);
                } else if (i < i2) {
                    onBeforeTriggerClick(v, i);
                } else {
                    this.mClickCount = 1;
                    onBeforeTriggerClick(v, 1);
                }
            } else {
                this.mClickCount = 1;
                onBeforeTriggerClick(v, 1);
            }
            this.mLastClickTime = curTime;
        }
    }

    private static class OnUtilsTouchListener implements View.OnTouchListener {
        public static OnUtilsTouchListener getInstance() {
            return LazyHolder.INSTANCE;
        }

        private OnUtilsTouchListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == 0) {
                processScale(v, true);
                processAlpha(v, true);
            } else if (action == 1 || action == 3) {
                processScale(v, false);
                processAlpha(v, false);
            }
            return false;
        }

        private void processScale(View view, boolean isDown) {
            Object tag = view.getTag(-1);
            if (tag instanceof Float) {
                float value = isDown ? 1.0f + ((Float) tag).floatValue() : 1.0f;
                view.animate().scaleX(value).scaleY(value).setDuration(200L).start();
            }
        }

        private void processAlpha(View view, boolean isDown) {
            Object tag = view.getTag(isDown ? -2 : -3);
            if (tag instanceof Float) {
                view.setAlpha(((Float) tag).floatValue());
            }
        }

        private static class LazyHolder {
            private static final OnUtilsTouchListener INSTANCE = new OnUtilsTouchListener();

            private LazyHolder() {
            }
        }
    }

    static class ClickDrawableWrapper extends ShadowUtils.DrawableWrapper {
        private BitmapDrawable mBitmapDrawable;
        private Paint mColorPaint;

        public ClickDrawableWrapper(Drawable drawable) {
            super(drawable);
            this.mBitmapDrawable = null;
            this.mColorPaint = null;
            if (drawable instanceof ColorDrawable) {
                Paint paint = new Paint(5);
                this.mColorPaint = paint;
                paint.setColor(((ColorDrawable) drawable).getColor());
            }
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter cf) {
            super.setColorFilter(cf);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            super.setAlpha(alpha);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            if (this.mBitmapDrawable == null) {
                Bitmap bitmap = Bitmap.createBitmap(getBounds().width(), getBounds().height(), Bitmap.Config.ARGB_8888);
                Canvas myCanvas = new Canvas(bitmap);
                if (this.mColorPaint != null) {
                    myCanvas.drawRect(getBounds(), this.mColorPaint);
                } else {
                    super.draw(myCanvas);
                }
                BitmapDrawable bitmapDrawable = new BitmapDrawable(Resources.getSystem(), bitmap);
                this.mBitmapDrawable = bitmapDrawable;
                bitmapDrawable.setBounds(getBounds());
            }
            this.mBitmapDrawable.draw(canvas);
        }
    }
}

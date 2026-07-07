package com.blankj.utilcode.util;

import android.R;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;
import android.view.View;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public class ShadowUtils {
    private static final int SHADOW_TAG = -16;

    public static void apply(View... views) {
        if (views == null) {
            return;
        }
        for (View view : views) {
            apply(view, new Config());
        }
    }

    public static void apply(View view, Config builder) {
        if (view == null || builder == null) {
            return;
        }
        Drawable background = view.getBackground();
        Object tag = view.getTag(SHADOW_TAG);
        if (tag instanceof Drawable) {
            ViewCompat.setBackground(view, (Drawable) tag);
            return;
        }
        Drawable background2 = builder.apply(background);
        ViewCompat.setBackground(view, background2);
        view.setTag(SHADOW_TAG, background2);
    }

    public static class Config {
        private static final int SHADOW_COLOR_DEFAULT = 1140850688;
        private static final int SHADOW_SIZE = UtilsBridge.dp2px(8.0f);
        private float mShadowRadius = -1.0f;
        private float mShadowSizeNormal = -1.0f;
        private float mShadowSizePressed = -1.0f;
        private float mShadowMaxSizeNormal = -1.0f;
        private float mShadowMaxSizePressed = -1.0f;
        private int mShadowColorNormal = SHADOW_COLOR_DEFAULT;
        private int mShadowColorPressed = SHADOW_COLOR_DEFAULT;
        private boolean isCircle = false;

        public Config setShadowRadius(float radius) {
            this.mShadowRadius = radius;
            if (this.isCircle) {
                throw new IllegalArgumentException("Set circle needn't set radius.");
            }
            return this;
        }

        public Config setCircle() {
            this.isCircle = true;
            if (this.mShadowRadius != -1.0f) {
                throw new IllegalArgumentException("Set circle needn't set radius.");
            }
            return this;
        }

        public Config setShadowSize(int size) {
            return setShadowSize(size, size);
        }

        public Config setShadowSize(int sizeNormal, int sizePressed) {
            this.mShadowSizeNormal = sizeNormal;
            this.mShadowSizePressed = sizePressed;
            return this;
        }

        public Config setShadowMaxSize(int maxSize) {
            return setShadowMaxSize(maxSize, maxSize);
        }

        public Config setShadowMaxSize(int maxSizeNormal, int maxSizePressed) {
            this.mShadowMaxSizeNormal = maxSizeNormal;
            this.mShadowMaxSizePressed = maxSizePressed;
            return this;
        }

        public Config setShadowColor(int color) {
            return setShadowColor(color, color);
        }

        public Config setShadowColor(int colorNormal, int colorPressed) {
            this.mShadowColorNormal = colorNormal;
            this.mShadowColorPressed = colorPressed;
            return this;
        }

        Drawable apply(Drawable src) {
            if (src == null) {
                src = new ColorDrawable(0);
            }
            StateListDrawable drawable = new StateListDrawable();
            Drawable drawable2 = src;
            drawable.addState(new int[]{R.attr.state_pressed}, new ShadowDrawable(drawable2, getShadowRadius(), getShadowSizeNormal(), getShadowMaxSizeNormal(), this.mShadowColorPressed, this.isCircle));
            drawable.addState(StateSet.WILD_CARD, new ShadowDrawable(drawable2, getShadowRadius(), getShadowSizePressed(), getShadowMaxSizePressed(), this.mShadowColorNormal, this.isCircle));
            return drawable;
        }

        private float getShadowRadius() {
            if (this.mShadowRadius < 0.0f) {
                this.mShadowRadius = 0.0f;
            }
            return this.mShadowRadius;
        }

        private float getShadowSizeNormal() {
            if (this.mShadowSizeNormal == -1.0f) {
                this.mShadowSizeNormal = SHADOW_SIZE;
            }
            return this.mShadowSizeNormal;
        }

        private float getShadowSizePressed() {
            if (this.mShadowSizePressed == -1.0f) {
                this.mShadowSizePressed = getShadowSizeNormal();
            }
            return this.mShadowSizePressed;
        }

        private float getShadowMaxSizeNormal() {
            if (this.mShadowMaxSizeNormal == -1.0f) {
                this.mShadowMaxSizeNormal = getShadowSizeNormal();
            }
            return this.mShadowMaxSizeNormal;
        }

        private float getShadowMaxSizePressed() {
            if (this.mShadowMaxSizePressed == -1.0f) {
                this.mShadowMaxSizePressed = getShadowSizePressed();
            }
            return this.mShadowMaxSizePressed;
        }
    }

    public static class ShadowDrawable extends DrawableWrapper {
        private static final double COS_45 = Math.cos(Math.toRadians(45.0d));
        private boolean isCircle;
        private boolean mAddPaddingForCorners;
        private RectF mContentBounds;
        private float mCornerRadius;
        private Paint mCornerShadowPaint;
        private Path mCornerShadowPath;
        private boolean mDirty;
        private Paint mEdgeShadowPaint;
        private float mMaxShadowSize;
        private float mRawMaxShadowSize;
        private float mRawShadowSize;
        private float mRotation;
        private float mShadowBottomScale;
        private final int mShadowEndColor;
        private float mShadowHorizScale;
        private float mShadowMultiplier;
        private float mShadowSize;
        private final int mShadowStartColor;
        private float mShadowTopScale;

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ int getChangingConfigurations() {
            return super.getChangingConfigurations();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ Drawable getCurrent() {
            return super.getCurrent();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ int getIntrinsicHeight() {
            return super.getIntrinsicHeight();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ int getIntrinsicWidth() {
            return super.getIntrinsicWidth();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ int getMinimumHeight() {
            return super.getMinimumHeight();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ int getMinimumWidth() {
            return super.getMinimumWidth();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ int[] getState() {
            return super.getState();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ Region getTransparentRegion() {
            return super.getTransparentRegion();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper
        public /* bridge */ /* synthetic */ Drawable getWrappedDrawable() {
            return super.getWrappedDrawable();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable.Callback
        public /* bridge */ /* synthetic */ void invalidateDrawable(Drawable drawable) {
            super.invalidateDrawable(drawable);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ boolean isAutoMirrored() {
            return super.isAutoMirrored();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ boolean isStateful() {
            return super.isStateful();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ void jumpToCurrentState() {
            super.jumpToCurrentState();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable.Callback
        public /* bridge */ /* synthetic */ void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
            super.scheduleDrawable(drawable, runnable, j);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ void setAutoMirrored(boolean z) {
            super.setAutoMirrored(z);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ void setChangingConfigurations(int i) {
            super.setChangingConfigurations(i);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ void setColorFilter(ColorFilter colorFilter) {
            super.setColorFilter(colorFilter);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ void setDither(boolean z) {
            super.setDither(z);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ void setFilterBitmap(boolean z) {
            super.setFilterBitmap(z);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ void setHotspot(float f, float f2) {
            super.setHotspot(f, f2);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ void setHotspotBounds(int i, int i2, int i3, int i4) {
            super.setHotspotBounds(i, i2, i3, i4);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ boolean setState(int[] iArr) {
            return super.setState(iArr);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ void setTint(int i) {
            super.setTint(i);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ void setTintList(ColorStateList colorStateList) {
            super.setTintList(colorStateList);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ void setTintMode(PorterDuff.Mode mode) {
            super.setTintMode(mode);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public /* bridge */ /* synthetic */ boolean setVisible(boolean z, boolean z2) {
            return super.setVisible(z, z2);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper
        public /* bridge */ /* synthetic */ void setWrappedDrawable(Drawable drawable) {
            super.setWrappedDrawable(drawable);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable.Callback
        public /* bridge */ /* synthetic */ void unscheduleDrawable(Drawable drawable, Runnable runnable) {
            super.unscheduleDrawable(drawable, runnable);
        }

        public ShadowDrawable(Drawable content, float radius, float shadowSize, float maxShadowSize, int shadowColor, boolean isCircle) {
            super(content);
            this.mShadowMultiplier = 1.0f;
            this.mShadowTopScale = 1.0f;
            this.mShadowHorizScale = 1.0f;
            this.mShadowBottomScale = 1.0f;
            this.mDirty = true;
            this.mAddPaddingForCorners = false;
            this.mShadowStartColor = shadowColor;
            this.mShadowEndColor = 16777215 & shadowColor;
            this.isCircle = isCircle;
            if (isCircle) {
                this.mShadowMultiplier = 1.0f;
                this.mShadowTopScale = 1.0f;
                this.mShadowHorizScale = 1.0f;
                this.mShadowBottomScale = 1.0f;
            }
            Paint paint = new Paint(5);
            this.mCornerShadowPaint = paint;
            paint.setStyle(Paint.Style.FILL);
            this.mCornerRadius = Math.round(radius);
            this.mContentBounds = new RectF();
            Paint paint2 = new Paint(this.mCornerShadowPaint);
            this.mEdgeShadowPaint = paint2;
            paint2.setAntiAlias(false);
            setShadowSize(shadowSize, maxShadowSize);
        }

        private static int toEven(float value) {
            int i = Math.round(value);
            return i % 2 == 1 ? i - 1 : i;
        }

        public void setAddPaddingForCorners(boolean addPaddingForCorners) {
            this.mAddPaddingForCorners = addPaddingForCorners;
            invalidateSelf();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            super.setAlpha(alpha);
            this.mCornerShadowPaint.setAlpha(alpha);
            this.mEdgeShadowPaint.setAlpha(alpha);
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        protected void onBoundsChange(Rect bounds) {
            this.mDirty = true;
        }

        void setShadowSize(float shadowSize, float maxShadowSize) {
            if (shadowSize < 0.0f || maxShadowSize < 0.0f) {
                throw new IllegalArgumentException("invalid shadow size");
            }
            float shadowSize2 = toEven(shadowSize);
            float maxShadowSize2 = toEven(maxShadowSize);
            if (shadowSize2 > maxShadowSize2) {
                shadowSize2 = maxShadowSize2;
            }
            if (this.mRawShadowSize == shadowSize2 && this.mRawMaxShadowSize == maxShadowSize2) {
                return;
            }
            this.mRawShadowSize = shadowSize2;
            this.mRawMaxShadowSize = maxShadowSize2;
            this.mShadowSize = Math.round(this.mShadowMultiplier * shadowSize2);
            this.mMaxShadowSize = maxShadowSize2;
            this.mDirty = true;
            invalidateSelf();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public boolean getPadding(Rect padding) {
            int vOffset = (int) Math.ceil(calculateVerticalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
            int hOffset = (int) Math.ceil(calculateHorizontalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
            padding.set(hOffset, vOffset, hOffset, vOffset);
            return true;
        }

        private float calculateVerticalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
            if (addPaddingForCorners) {
                return (float) (((double) (this.mShadowMultiplier * maxShadowSize)) + ((1.0d - COS_45) * ((double) cornerRadius)));
            }
            return this.mShadowMultiplier * maxShadowSize;
        }

        private static float calculateHorizontalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
            if (addPaddingForCorners) {
                return (float) (((double) maxShadowSize) + ((1.0d - COS_45) * ((double) cornerRadius)));
            }
            return maxShadowSize;
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }

        public void setCornerRadius(float radius) {
            float radius2 = Math.round(radius);
            if (this.mCornerRadius == radius2) {
                return;
            }
            this.mCornerRadius = radius2;
            this.mDirty = true;
            invalidateSelf();
        }

        @Override // com.blankj.utilcode.util.ShadowUtils.DrawableWrapper, android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            if (this.mDirty) {
                buildComponents(getBounds());
                this.mDirty = false;
            }
            drawShadow(canvas);
            super.draw(canvas);
        }

        final void setRotation(float rotation) {
            if (this.mRotation != rotation) {
                this.mRotation = rotation;
                invalidateSelf();
            }
        }

        private void drawShadow(Canvas canvas) {
            float shadowScaleBottom;
            float shadowScaleTop;
            float shadowScaleHorizontal;
            int saved;
            float shadowOffsetHorizontal;
            float shadowScaleHorizontal2;
            if (this.isCircle) {
                int saved2 = canvas.save();
                canvas.translate(this.mContentBounds.centerX(), this.mContentBounds.centerY());
                canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
                canvas.restoreToCount(saved2);
                return;
            }
            int rotateSaved = canvas.save();
            canvas.rotate(this.mRotation, this.mContentBounds.centerX(), this.mContentBounds.centerY());
            float edgeShadowTop = (-this.mCornerRadius) - this.mShadowSize;
            float shadowOffset = this.mCornerRadius;
            boolean drawHorizontalEdges = this.mContentBounds.width() - (shadowOffset * 2.0f) > 0.0f;
            boolean drawVerticalEdges = this.mContentBounds.height() - (shadowOffset * 2.0f) > 0.0f;
            float f = this.mRawShadowSize;
            float shadowOffsetTop = f - (this.mShadowTopScale * f);
            float shadowOffsetHorizontal2 = f - (this.mShadowHorizScale * f);
            float shadowOffsetBottom = f - (this.mShadowBottomScale * f);
            float shadowScaleHorizontal3 = shadowOffset == 0.0f ? 1.0f : shadowOffset / (shadowOffset + shadowOffsetHorizontal2);
            float shadowScaleTop2 = shadowOffset == 0.0f ? 1.0f : shadowOffset / (shadowOffset + shadowOffsetTop);
            float shadowScaleBottom2 = shadowOffset == 0.0f ? 1.0f : shadowOffset / (shadowOffset + shadowOffsetBottom);
            int saved3 = canvas.save();
            canvas.translate(this.mContentBounds.left + shadowOffset, this.mContentBounds.top + shadowOffset);
            canvas.scale(shadowScaleHorizontal3, shadowScaleTop2);
            canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
            if (!drawHorizontalEdges) {
                shadowScaleBottom = shadowScaleBottom2;
                shadowScaleTop = shadowScaleTop2;
                shadowScaleHorizontal = shadowScaleHorizontal3;
                saved = saved3;
                shadowOffsetHorizontal = 1.0f;
            } else {
                canvas.scale(1.0f / shadowScaleHorizontal3, 1.0f);
                float shadowOffsetHorizontal3 = this.mContentBounds.width() - (shadowOffset * 2.0f);
                saved = saved3;
                shadowScaleBottom = shadowScaleBottom2;
                shadowScaleTop = shadowScaleTop2;
                shadowScaleHorizontal = shadowScaleHorizontal3;
                shadowOffsetHorizontal = 1.0f;
                canvas.drawRect(0.0f, edgeShadowTop, shadowOffsetHorizontal3, -this.mCornerRadius, this.mEdgeShadowPaint);
            }
            canvas.restoreToCount(saved);
            int saved4 = canvas.save();
            canvas.translate(this.mContentBounds.right - shadowOffset, this.mContentBounds.bottom - shadowOffset);
            float shadowScaleHorizontal4 = shadowScaleHorizontal;
            canvas.scale(shadowScaleHorizontal4, shadowScaleBottom);
            canvas.rotate(180.0f);
            canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
            if (!drawHorizontalEdges) {
                shadowScaleHorizontal2 = shadowScaleHorizontal4;
            } else {
                canvas.scale(shadowOffsetHorizontal / shadowScaleHorizontal4, shadowOffsetHorizontal);
                shadowScaleHorizontal2 = shadowScaleHorizontal4;
                canvas.drawRect(0.0f, edgeShadowTop, this.mContentBounds.width() - (shadowOffset * 2.0f), -this.mCornerRadius, this.mEdgeShadowPaint);
            }
            canvas.restoreToCount(saved4);
            int saved5 = canvas.save();
            canvas.translate(this.mContentBounds.left + shadowOffset, this.mContentBounds.bottom - shadowOffset);
            canvas.scale(shadowScaleHorizontal2, shadowScaleBottom);
            canvas.rotate(270.0f);
            canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
            if (drawVerticalEdges) {
                canvas.scale(1.0f / shadowScaleBottom, 1.0f);
                canvas.drawRect(0.0f, edgeShadowTop, this.mContentBounds.height() - (shadowOffset * 2.0f), -this.mCornerRadius, this.mEdgeShadowPaint);
            }
            canvas.restoreToCount(saved5);
            int saved6 = canvas.save();
            canvas.translate(this.mContentBounds.right - shadowOffset, this.mContentBounds.top + shadowOffset);
            float shadowScaleTop3 = shadowScaleTop;
            canvas.scale(shadowScaleHorizontal2, shadowScaleTop3);
            canvas.rotate(90.0f);
            canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
            if (drawVerticalEdges) {
                canvas.scale(1.0f / shadowScaleTop3, 1.0f);
                canvas.drawRect(0.0f, edgeShadowTop, this.mContentBounds.height() - (2.0f * shadowOffset), -this.mCornerRadius, this.mEdgeShadowPaint);
            }
            canvas.restoreToCount(saved6);
            canvas.restoreToCount(rotateSaved);
        }

        private void buildShadowCorners() {
            if (!this.isCircle) {
                float f = this.mCornerRadius;
                RectF innerBounds = new RectF(-f, -f, f, f);
                RectF outerBounds = new RectF(innerBounds);
                float f2 = this.mShadowSize;
                outerBounds.inset(-f2, -f2);
                Path path = this.mCornerShadowPath;
                if (path == null) {
                    this.mCornerShadowPath = new Path();
                } else {
                    path.reset();
                }
                this.mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
                this.mCornerShadowPath.moveTo(-this.mCornerRadius, 0.0f);
                this.mCornerShadowPath.rLineTo(-this.mShadowSize, 0.0f);
                this.mCornerShadowPath.arcTo(outerBounds, 180.0f, 90.0f, false);
                this.mCornerShadowPath.arcTo(innerBounds, 270.0f, -90.0f, false);
                this.mCornerShadowPath.close();
                float shadowRadius = -outerBounds.top;
                if (shadowRadius > 0.0f) {
                    float startRatio = this.mCornerRadius / shadowRadius;
                    this.mCornerShadowPaint.setShader(new RadialGradient(0.0f, 0.0f, shadowRadius, new int[]{0, this.mShadowStartColor, this.mShadowEndColor}, new float[]{0.0f, startRatio, 1.0f}, Shader.TileMode.CLAMP));
                }
                this.mEdgeShadowPaint.setShader(new LinearGradient(0.0f, innerBounds.top, 0.0f, outerBounds.top, this.mShadowStartColor, this.mShadowEndColor, Shader.TileMode.CLAMP));
                this.mEdgeShadowPaint.setAntiAlias(false);
                return;
            }
            float size = (this.mContentBounds.width() / 2.0f) - 1.0f;
            RectF innerBounds2 = new RectF(-size, -size, size, size);
            RectF outerBounds2 = new RectF(innerBounds2);
            float f3 = this.mShadowSize;
            outerBounds2.inset(-f3, -f3);
            Path path2 = this.mCornerShadowPath;
            if (path2 == null) {
                this.mCornerShadowPath = new Path();
            } else {
                path2.reset();
            }
            this.mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
            this.mCornerShadowPath.moveTo(-size, 0.0f);
            this.mCornerShadowPath.rLineTo(-this.mShadowSize, 0.0f);
            this.mCornerShadowPath.arcTo(outerBounds2, 180.0f, 180.0f, false);
            this.mCornerShadowPath.arcTo(outerBounds2, 0.0f, 180.0f, false);
            this.mCornerShadowPath.arcTo(innerBounds2, 180.0f, 180.0f, false);
            this.mCornerShadowPath.arcTo(innerBounds2, 0.0f, 180.0f, false);
            this.mCornerShadowPath.close();
            float shadowRadius2 = -outerBounds2.top;
            if (shadowRadius2 > 0.0f) {
                float startRatio2 = size / shadowRadius2;
                this.mCornerShadowPaint.setShader(new RadialGradient(0.0f, 0.0f, shadowRadius2, new int[]{0, this.mShadowStartColor, this.mShadowEndColor}, new float[]{0.0f, startRatio2, 1.0f}, Shader.TileMode.CLAMP));
            }
        }

        private void buildComponents(Rect bounds) {
            if (this.isCircle) {
                this.mCornerRadius = bounds.width() / 2;
            }
            float verticalOffset = this.mRawMaxShadowSize * this.mShadowMultiplier;
            this.mContentBounds.set(bounds.left + this.mRawMaxShadowSize, bounds.top + verticalOffset, bounds.right - this.mRawMaxShadowSize, bounds.bottom - verticalOffset);
            getWrappedDrawable().setBounds((int) this.mContentBounds.left, (int) this.mContentBounds.top, (int) this.mContentBounds.right, (int) this.mContentBounds.bottom);
            buildShadowCorners();
        }

        public float getCornerRadius() {
            return this.mCornerRadius;
        }

        public void setShadowSize(float size) {
            setShadowSize(size, this.mRawMaxShadowSize);
        }

        public void setMaxShadowSize(float size) {
            setShadowSize(this.mRawShadowSize, size);
        }

        public float getShadowSize() {
            return this.mRawShadowSize;
        }

        public float getMaxShadowSize() {
            return this.mRawMaxShadowSize;
        }

        public float getMinWidth() {
            float f = this.mRawMaxShadowSize;
            float content = Math.max(f, this.mCornerRadius + (f / 2.0f)) * 2.0f;
            return (this.mRawMaxShadowSize * 2.0f) + content;
        }

        public float getMinHeight() {
            float f = this.mRawMaxShadowSize;
            float content = Math.max(f, this.mCornerRadius + ((this.mShadowMultiplier * f) / 2.0f)) * 2.0f;
            return (this.mRawMaxShadowSize * this.mShadowMultiplier * 2.0f) + content;
        }
    }

    static class DrawableWrapper extends Drawable implements Drawable.Callback {
        private Drawable mDrawable;

        public DrawableWrapper(Drawable drawable) {
            setWrappedDrawable(drawable);
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            this.mDrawable.draw(canvas);
        }

        @Override // android.graphics.drawable.Drawable
        protected void onBoundsChange(Rect bounds) {
            this.mDrawable.setBounds(bounds);
        }

        @Override // android.graphics.drawable.Drawable
        public void setChangingConfigurations(int configs) {
            this.mDrawable.setChangingConfigurations(configs);
        }

        @Override // android.graphics.drawable.Drawable
        public int getChangingConfigurations() {
            return this.mDrawable.getChangingConfigurations();
        }

        @Override // android.graphics.drawable.Drawable
        public void setDither(boolean dither) {
            this.mDrawable.setDither(dither);
        }

        @Override // android.graphics.drawable.Drawable
        public void setFilterBitmap(boolean filter) {
            this.mDrawable.setFilterBitmap(filter);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            this.mDrawable.setAlpha(alpha);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter cf) {
            this.mDrawable.setColorFilter(cf);
        }

        @Override // android.graphics.drawable.Drawable
        public boolean isStateful() {
            return this.mDrawable.isStateful();
        }

        @Override // android.graphics.drawable.Drawable
        public boolean setState(int[] stateSet) {
            return this.mDrawable.setState(stateSet);
        }

        @Override // android.graphics.drawable.Drawable
        public int[] getState() {
            return this.mDrawable.getState();
        }

        @Override // android.graphics.drawable.Drawable
        public void jumpToCurrentState() {
            DrawableCompat.jumpToCurrentState(this.mDrawable);
        }

        @Override // android.graphics.drawable.Drawable
        public Drawable getCurrent() {
            return this.mDrawable.getCurrent();
        }

        @Override // android.graphics.drawable.Drawable
        public boolean setVisible(boolean visible, boolean restart) {
            return super.setVisible(visible, restart) || this.mDrawable.setVisible(visible, restart);
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return this.mDrawable.getOpacity();
        }

        @Override // android.graphics.drawable.Drawable
        public Region getTransparentRegion() {
            return this.mDrawable.getTransparentRegion();
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return this.mDrawable.getIntrinsicWidth();
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return this.mDrawable.getIntrinsicHeight();
        }

        @Override // android.graphics.drawable.Drawable
        public int getMinimumWidth() {
            return this.mDrawable.getMinimumWidth();
        }

        @Override // android.graphics.drawable.Drawable
        public int getMinimumHeight() {
            return this.mDrawable.getMinimumHeight();
        }

        @Override // android.graphics.drawable.Drawable
        public boolean getPadding(Rect padding) {
            return this.mDrawable.getPadding(padding);
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public void invalidateDrawable(Drawable who) {
            invalidateSelf();
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            scheduleSelf(what, when);
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public void unscheduleDrawable(Drawable who, Runnable what) {
            unscheduleSelf(what);
        }

        @Override // android.graphics.drawable.Drawable
        protected boolean onLevelChange(int level) {
            return this.mDrawable.setLevel(level);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAutoMirrored(boolean mirrored) {
            DrawableCompat.setAutoMirrored(this.mDrawable, mirrored);
        }

        @Override // android.graphics.drawable.Drawable
        public boolean isAutoMirrored() {
            return DrawableCompat.isAutoMirrored(this.mDrawable);
        }

        @Override // android.graphics.drawable.Drawable
        public void setTint(int tint) {
            DrawableCompat.setTint(this.mDrawable, tint);
        }

        @Override // android.graphics.drawable.Drawable
        public void setTintList(ColorStateList tint) {
            DrawableCompat.setTintList(this.mDrawable, tint);
        }

        @Override // android.graphics.drawable.Drawable
        public void setTintMode(PorterDuff.Mode tintMode) {
            DrawableCompat.setTintMode(this.mDrawable, tintMode);
        }

        @Override // android.graphics.drawable.Drawable
        public void setHotspot(float x, float y) {
            DrawableCompat.setHotspot(this.mDrawable, x, y);
        }

        @Override // android.graphics.drawable.Drawable
        public void setHotspotBounds(int left, int top, int right, int bottom) {
            DrawableCompat.setHotspotBounds(this.mDrawable, left, top, right, bottom);
        }

        public Drawable getWrappedDrawable() {
            return this.mDrawable;
        }

        public void setWrappedDrawable(Drawable drawable) {
            Drawable drawable2 = this.mDrawable;
            if (drawable2 != null) {
                drawable2.setCallback(null);
            }
            this.mDrawable = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
            }
        }
    }
}

package com.blankj.utilcode.util;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public class TouchUtils {
    public static final int DOWN = 8;
    public static final int LEFT = 1;
    public static final int RIGHT = 4;
    public static final int UNKNOWN = 0;
    public static final int UP = 2;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
    }

    private TouchUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void setOnTouchListener(View v, OnTouchUtilsListener listener) {
        if (v == null || listener == null) {
            return;
        }
        v.setOnTouchListener(listener);
    }

    public static abstract class OnTouchUtilsListener implements View.OnTouchListener {
        private static final int MIN_TAP_TIME = 1000;
        private static final int STATE_DOWN = 0;
        private static final int STATE_MOVE = 1;
        private static final int STATE_STOP = 2;
        private int direction;
        private int downX;
        private int downY;
        private int lastX;
        private int lastY;
        private int maximumFlingVelocity;
        private int minimumFlingVelocity;
        private int state;
        private int touchSlop;
        private VelocityTracker velocityTracker;

        public abstract boolean onDown(View view, int i, int i2, MotionEvent motionEvent);

        public abstract boolean onMove(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, MotionEvent motionEvent);

        public abstract boolean onStop(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, MotionEvent motionEvent);

        public OnTouchUtilsListener() {
            resetTouch(-1, -1);
        }

        private void resetTouch(int x, int y) {
            this.downX = x;
            this.downY = y;
            this.lastX = x;
            this.lastY = y;
            this.state = 0;
            this.direction = 0;
            VelocityTracker velocityTracker = this.velocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            if (this.touchSlop == 0) {
                this.touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
            }
            if (this.maximumFlingVelocity == 0) {
                this.maximumFlingVelocity = ViewConfiguration.get(v.getContext()).getScaledMaximumFlingVelocity();
            }
            if (this.minimumFlingVelocity == 0) {
                this.minimumFlingVelocity = ViewConfiguration.get(v.getContext()).getScaledMinimumFlingVelocity();
            }
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.velocityTracker.addMovement(event);
            switch (event.getAction()) {
                case 0:
                    return onUtilsDown(v, event);
                case 1:
                case 3:
                    return onUtilsStop(v, event);
                case 2:
                    return onUtilsMove(v, event);
                default:
                    return false;
            }
        }

        public boolean onUtilsDown(View view, MotionEvent event) {
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            resetTouch(x, y);
            view.setPressed(true);
            return onDown(view, x, y, event);
        }

        public boolean onUtilsMove(View view, MotionEvent event) {
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            if (this.downX == -1) {
                resetTouch(x, y);
                view.setPressed(true);
            }
            if (this.state != 1) {
                if (Math.abs(x - this.lastX) < this.touchSlop && Math.abs(y - this.lastY) < this.touchSlop) {
                    return true;
                }
                this.state = 1;
                if (Math.abs(x - this.lastX) >= Math.abs(y - this.lastY)) {
                    if (x - this.lastX < 0) {
                        this.direction = 1;
                    } else {
                        this.direction = 4;
                    }
                } else if (y - this.lastY < 0) {
                    this.direction = 2;
                } else {
                    this.direction = 8;
                }
            }
            boolean consumeMove = onMove(view, this.direction, x, y, x - this.lastX, y - this.lastY, x - this.downX, y - this.downY, event);
            this.lastX = x;
            this.lastY = y;
            return consumeMove;
        }

        public boolean onUtilsStop(View view, MotionEvent event) {
            int vx;
            int vy;
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            VelocityTracker velocityTracker = this.velocityTracker;
            if (velocityTracker == null) {
                vx = 0;
                vy = 0;
            } else {
                velocityTracker.computeCurrentVelocity(1000, this.maximumFlingVelocity);
                int vx2 = (int) this.velocityTracker.getXVelocity();
                int vy2 = (int) this.velocityTracker.getYVelocity();
                this.velocityTracker.recycle();
                if (Math.abs(vx2) < this.minimumFlingVelocity) {
                    vx2 = 0;
                }
                if (Math.abs(vy2) < this.minimumFlingVelocity) {
                    vy2 = 0;
                }
                this.velocityTracker = null;
                vx = vx2;
                vy = vy2;
            }
            view.setPressed(false);
            boolean consumeStop = onStop(view, this.direction, x, y, x - this.downX, y - this.downY, vx, vy, event);
            if (event.getAction() == 1 && this.state == 0) {
                if (event.getEventTime() - event.getDownTime() <= 1000) {
                    view.performClick();
                } else {
                    view.performLongClick();
                }
            }
            resetTouch(-1, -1);
            return consumeStop;
        }
    }
}

package com.blankj.utilcode.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class NumberUtils {
    private static final ThreadLocal<DecimalFormat> DF_THREAD_LOCAL = new ThreadLocal<DecimalFormat>() { // from class: com.blankj.utilcode.util.NumberUtils.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public DecimalFormat initialValue() {
            return (DecimalFormat) NumberFormat.getInstance();
        }
    };

    public static DecimalFormat getSafeDecimalFormat() {
        return DF_THREAD_LOCAL.get();
    }

    private NumberUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String format(float value, int fractionDigits) {
        return format(value, false, 1, fractionDigits, true);
    }

    public static String format(float value, int fractionDigits, boolean isHalfUp) {
        return format(value, false, 1, fractionDigits, isHalfUp);
    }

    public static String format(float value, int minIntegerDigits, int fractionDigits, boolean isHalfUp) {
        return format(value, false, minIntegerDigits, fractionDigits, isHalfUp);
    }

    public static String format(float value, boolean isGrouping, int fractionDigits) {
        return format(value, isGrouping, 1, fractionDigits, true);
    }

    public static String format(float value, boolean isGrouping, int fractionDigits, boolean isHalfUp) {
        return format(value, isGrouping, 1, fractionDigits, isHalfUp);
    }

    public static String format(float value, boolean isGrouping, int minIntegerDigits, int fractionDigits, boolean isHalfUp) {
        return format(float2Double(value), isGrouping, minIntegerDigits, fractionDigits, isHalfUp);
    }

    public static String format(double value, int fractionDigits) {
        return format(value, false, 1, fractionDigits, true);
    }

    public static String format(double value, int fractionDigits, boolean isHalfUp) {
        return format(value, false, 1, fractionDigits, isHalfUp);
    }

    public static String format(double value, int minIntegerDigits, int fractionDigits, boolean isHalfUp) {
        return format(value, false, minIntegerDigits, fractionDigits, isHalfUp);
    }

    public static String format(double value, boolean isGrouping, int fractionDigits) {
        return format(value, isGrouping, 1, fractionDigits, true);
    }

    public static String format(double value, boolean isGrouping, int fractionDigits, boolean isHalfUp) {
        return format(value, isGrouping, 1, fractionDigits, isHalfUp);
    }

    public static String format(double value, boolean isGrouping, int minIntegerDigits, int fractionDigits, boolean isHalfUp) {
        DecimalFormat nf = getSafeDecimalFormat();
        nf.setGroupingUsed(isGrouping);
        nf.setRoundingMode(isHalfUp ? RoundingMode.HALF_UP : RoundingMode.DOWN);
        nf.setMinimumIntegerDigits(minIntegerDigits);
        nf.setMinimumFractionDigits(fractionDigits);
        nf.setMaximumFractionDigits(fractionDigits);
        return nf.format(value);
    }

    public static double float2Double(float value) {
        return new BigDecimal(String.valueOf(value)).doubleValue();
    }
}

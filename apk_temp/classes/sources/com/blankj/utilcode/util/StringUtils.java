package com.blankj.utilcode.util;

import android.content.res.Resources;
import java.util.IllegalFormatException;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class StringUtils {
    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static boolean isTrimEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(CharSequence s1, CharSequence s2) {
        int length;
        if (s1 == s2) {
            return true;
        }
        if (s1 == null || s2 == null || (length = s1.length()) != s2.length()) {
            return false;
        }
        if ((s1 instanceof String) && (s2 instanceof String)) {
            return s1.equals(s2);
        }
        for (int i = 0; i < length; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsIgnoreCase(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
    }

    public static String null2Length0(String s) {
        return s == null ? "" : s;
    }

    public static int length(CharSequence s) {
        if (s == null) {
            return 0;
        }
        return s.length();
    }

    public static String upperFirstLetter(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        return !Character.isLowerCase(s.charAt(0)) ? s : ((char) (s.charAt(0) - ' ')) + s.substring(1);
    }

    public static String lowerFirstLetter(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        return !Character.isUpperCase(s.charAt(0)) ? s : String.valueOf((char) (s.charAt(0) + ' ')) + s.substring(1);
    }

    public static String reverse(String s) {
        if (s == null) {
            return "";
        }
        int len = s.length();
        if (len <= 1) {
            return s;
        }
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        for (int i = 0; i < mid; i++) {
            char c = chars[i];
            chars[i] = chars[(len - i) - 1];
            chars[(len - i) - 1] = c;
        }
        return new String(chars);
    }

    public static String toDBC(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char[] chars = s.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    public static String toSBC(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char[] chars = s.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = 12288;
            } else if ('!' <= chars[i] && chars[i] <= '~') {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    public static String getString(int id) {
        return getString(id, null);
    }

    public static String getString(int id, Object... formatArgs) {
        try {
            return format(Utils.getApp().getString(id), formatArgs);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return String.valueOf(id);
        }
    }

    public static String[] getStringArray(int id) {
        try {
            return Utils.getApp().getResources().getStringArray(id);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return new String[]{String.valueOf(id)};
        }
    }

    public static String format(String str, Object... args) {
        if (str == null || args == null || args.length <= 0) {
            return str;
        }
        try {
            String text = String.format(str, args);
            return text;
        } catch (IllegalFormatException e) {
            e.printStackTrace();
            return str;
        }
    }
}

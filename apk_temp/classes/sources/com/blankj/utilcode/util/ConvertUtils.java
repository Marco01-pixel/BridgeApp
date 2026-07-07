package com.blankj.utilcode.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import kotlin.UByte;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class ConvertUtils {
    private static final int BUFFER_SIZE = 8192;
    private static final char[] HEX_DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final char[] HEX_DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private ConvertUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String int2HexString(int num) {
        return Integer.toHexString(num);
    }

    public static int hexString2Int(String hexString) {
        return Integer.parseInt(hexString, 16);
    }

    public static String bytes2Bits(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            for (int j = 7; j >= 0; j--) {
                sb.append(((aByte >> j) & 1) == 0 ? '0' : '1');
            }
        }
        return sb.toString();
    }

    public static byte[] bits2Bytes(String bits) {
        int lenMod = bits.length() % 8;
        int byteLen = bits.length() / 8;
        if (lenMod != 0) {
            for (int i = lenMod; i < 8; i++) {
                bits = "0" + bits;
            }
            byteLen++;
        }
        byte[] bytes = new byte[byteLen];
        for (int i2 = 0; i2 < byteLen; i2++) {
            for (int j = 0; j < 8; j++) {
                bytes[i2] = (byte) (bytes[i2] << 1);
                bytes[i2] = (byte) (bytes[i2] | (bits.charAt((i2 * 8) + j) - '0'));
            }
        }
        return bytes;
    }

    public static char[] bytes2Chars(byte[] bytes) {
        int len;
        if (bytes == null || (len = bytes.length) <= 0) {
            return null;
        }
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = (char) (bytes[i] & UByte.MAX_VALUE);
        }
        return chars;
    }

    public static byte[] chars2Bytes(char[] chars) {
        if (chars == null || chars.length <= 0) {
            return null;
        }
        int len = chars.length;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }

    public static String bytes2HexString(byte[] bytes) {
        return bytes2HexString(bytes, true);
    }

    public static String bytes2HexString(byte[] bytes, boolean isUpperCase) {
        if (bytes == null) {
            return "";
        }
        char[] hexDigits = isUpperCase ? HEX_DIGITS_UPPER : HEX_DIGITS_LOWER;
        int len = bytes.length;
        if (len <= 0) {
            return "";
        }
        char[] ret = new char[len << 1];
        int j = 0;
        for (int i = 0; i < len; i++) {
            int j2 = j + 1;
            ret[j] = hexDigits[(bytes[i] >> 4) & 15];
            j = j2 + 1;
            ret[j2] = hexDigits[bytes[i] & 15];
        }
        return new String(ret);
    }

    public static byte[] hexString2Bytes(String hexString) {
        if (UtilsBridge.isSpace(hexString)) {
            return new byte[0];
        }
        int len = hexString.length();
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len++;
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) ((hex2Dec(hexBytes[i]) << 4) | hex2Dec(hexBytes[i + 1]));
        }
        return ret;
    }

    private static int hex2Dec(char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        }
        if (hexChar >= 'A' && hexChar <= 'F') {
            return (hexChar - 'A') + 10;
        }
        throw new IllegalArgumentException();
    }

    public static String bytes2String(byte[] bytes) {
        return bytes2String(bytes, "");
    }

    public static String bytes2String(byte[] bytes, String charsetName) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, getSafeCharset(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new String(bytes);
        }
    }

    public static byte[] string2Bytes(String string) {
        return string2Bytes(string, "");
    }

    public static byte[] string2Bytes(String string, String charsetName) {
        if (string == null) {
            return null;
        }
        try {
            return string.getBytes(getSafeCharset(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return string.getBytes();
        }
    }

    public static JSONObject bytes2JSONObject(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            return new JSONObject(new String(bytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] jsonObject2Bytes(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.toString().getBytes();
    }

    public static JSONArray bytes2JSONArray(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            return new JSONArray(new String(bytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] jsonArray2Bytes(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        return jsonArray.toString().getBytes();
    }

    public static <T> T bytes2Parcelable(byte[] bytes, Parcelable.Creator<T> creator) {
        if (bytes == null) {
            return null;
        }
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }

    public static byte[] parcelable2Bytes(Parcelable parcelable) {
        if (parcelable == null) {
            return null;
        }
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Object bytes2Object(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ObjectInputStream ois = null;
        try {
            try {
                ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                Object object = ois.readObject();
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return object;
            } catch (Exception e2) {
                e2.printStackTrace();
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static byte[] serializable2Bytes(Serializable serializable) {
        if (serializable == null) {
            return null;
        }
        ObjectOutputStream oos = null;
        try {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(serializable);
                byte[] byteArray = baos.toByteArray();
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return byteArray;
            } catch (Throwable th) {
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (Exception e3) {
            e3.printStackTrace();
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            return null;
        }
    }

    public static Bitmap bytes2Bitmap(byte[] bytes) {
        return UtilsBridge.bytes2Bitmap(bytes);
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        return UtilsBridge.bitmap2Bytes(bitmap);
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        return UtilsBridge.bitmap2Bytes(bitmap, format, quality);
    }

    public static Drawable bytes2Drawable(byte[] bytes) {
        return UtilsBridge.bytes2Drawable(bytes);
    }

    public static byte[] drawable2Bytes(Drawable drawable) {
        return UtilsBridge.drawable2Bytes(drawable);
    }

    public static byte[] drawable2Bytes(Drawable drawable, Bitmap.CompressFormat format, int quality) {
        return UtilsBridge.drawable2Bytes(drawable, format, quality);
    }

    public static long memorySize2Byte(long memorySize, int unit) {
        if (memorySize < 0) {
            return -1L;
        }
        return ((long) unit) * memorySize;
    }

    public static double byte2MemorySize(long byteSize, int unit) {
        if (byteSize < 0) {
            return -1.0d;
        }
        return byteSize / ((double) unit);
    }

    public static String byte2FitMemorySize(long byteSize) {
        return byte2FitMemorySize(byteSize, 3);
    }

    public static String byte2FitMemorySize(long byteSize, int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException("precision shouldn't be less than zero!");
        }
        if (byteSize >= 0) {
            return byteSize < 1024 ? String.format("%." + precision + "fB", Double.valueOf(byteSize)) : byteSize < 1048576 ? String.format("%." + precision + "fKB", Double.valueOf(byteSize / 1024.0d)) : byteSize < 1073741824 ? String.format("%." + precision + "fMB", Double.valueOf(byteSize / 1048576.0d)) : String.format("%." + precision + "fGB", Double.valueOf(byteSize / 1.073741824E9d));
        }
        throw new IllegalArgumentException("byteSize shouldn't be less than zero!");
    }

    public static long timeSpan2Millis(long timeSpan, int unit) {
        return ((long) unit) * timeSpan;
    }

    public static long millis2TimeSpan(long millis, int unit) {
        return millis / ((long) unit);
    }

    public static String millis2FitTimeSpan(long millis, int precision) {
        return UtilsBridge.millis2FitTimeSpan(millis, precision);
    }

    public static ByteArrayOutputStream input2OutputStream(InputStream is) {
        try {
            if (is == null) {
                return null;
            }
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] b = new byte[8192];
                while (true) {
                    int len = is.read(b, 0, 8192);
                    if (len != -1) {
                        os.write(b, 0, len);
                    } else {
                        try {
                            break;
                        } catch (IOException e) {
                        }
                    }
                }
                return os;
            } catch (IOException e2) {
                e2.printStackTrace();
                try {
                    is.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                return null;
            }
        } finally {
            try {
                is.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
        }
    }

    public static ByteArrayInputStream output2InputStream(OutputStream out) {
        if (out == null) {
            return null;
        }
        return new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
    }

    public static byte[] inputStream2Bytes(InputStream is) {
        if (is == null) {
            return null;
        }
        return input2OutputStream(is).toByteArray();
    }

    public static InputStream bytes2InputStream(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    public static byte[] outputStream2Bytes(OutputStream out) {
        if (out == null) {
            return null;
        }
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    public static OutputStream bytes2OutputStream(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        ByteArrayOutputStream os = null;
        try {
            try {
                os = new ByteArrayOutputStream();
                os.write(bytes);
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return os;
            } catch (IOException e2) {
                e2.printStackTrace();
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static String inputStream2String(InputStream is, String charsetName) {
        if (is == null) {
            return "";
        }
        try {
            ByteArrayOutputStream baos = input2OutputStream(is);
            if (baos == null) {
                return "";
            }
            return baos.toString(getSafeCharset(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static InputStream string2InputStream(String string, String charsetName) {
        if (string == null) {
            return null;
        }
        try {
            return new ByteArrayInputStream(string.getBytes(getSafeCharset(charsetName)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String outputStream2String(OutputStream out, String charsetName) {
        if (out == null) {
            return "";
        }
        try {
            return new String(outputStream2Bytes(out), getSafeCharset(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static OutputStream string2OutputStream(String string, String charsetName) {
        if (string == null) {
            return null;
        }
        try {
            return bytes2OutputStream(string.getBytes(getSafeCharset(charsetName)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> inputStream2Lines(InputStream is) {
        return inputStream2Lines(is, "");
    }

    public static List<String> inputStream2Lines(InputStream is, String charsetName) {
        BufferedReader reader = null;
        try {
            try {
                List<String> list = new ArrayList<>();
                reader = new BufferedReader(new InputStreamReader(is, getSafeCharset(charsetName)));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        list.add(line);
                    } else {
                        try {
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                reader.close();
                return list;
            } catch (IOException e2) {
                e2.printStackTrace();
                if (reader == null) {
                    return null;
                }
                try {
                    reader.close();
                    return null;
                } catch (IOException e3) {
                    e3.printStackTrace();
                    return null;
                }
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        return UtilsBridge.drawable2Bitmap(drawable);
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return UtilsBridge.bitmap2Drawable(bitmap);
    }

    public static Bitmap view2Bitmap(View view) {
        return UtilsBridge.view2Bitmap(view);
    }

    public static int dp2px(float dpValue) {
        return UtilsBridge.dp2px(dpValue);
    }

    public static int px2dp(float pxValue) {
        return UtilsBridge.px2dp(pxValue);
    }

    public static int sp2px(float spValue) {
        return UtilsBridge.sp2px(spValue);
    }

    public static int px2sp(float pxValue) {
        return UtilsBridge.px2sp(pxValue);
    }

    private static String getSafeCharset(String charsetName) {
        if (!UtilsBridge.isSpace(charsetName) && Charset.isSupported(charsetName)) {
            return charsetName;
        }
        return "UTF-8";
    }
}

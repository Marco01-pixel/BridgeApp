package com.blankj.utilcode.util;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class FileIOUtils {
    private static int sBufferSize = 524288;

    public interface OnProgressUpdateListener {
        void onProgressUpdate(double d);
    }

    private FileIOUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean writeFileFromIS(String filePath, InputStream is) {
        return writeFileFromIS(UtilsBridge.getFileByPath(filePath), is, false, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromIS(String filePath, InputStream is, boolean append) {
        return writeFileFromIS(UtilsBridge.getFileByPath(filePath), is, append, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromIS(File file, InputStream is) {
        return writeFileFromIS(file, is, false, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromIS(File file, InputStream is, boolean append) {
        return writeFileFromIS(file, is, append, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromIS(String filePath, InputStream is, OnProgressUpdateListener listener) {
        return writeFileFromIS(UtilsBridge.getFileByPath(filePath), is, false, listener);
    }

    public static boolean writeFileFromIS(String filePath, InputStream is, boolean append, OnProgressUpdateListener listener) {
        return writeFileFromIS(UtilsBridge.getFileByPath(filePath), is, append, listener);
    }

    public static boolean writeFileFromIS(File file, InputStream is, OnProgressUpdateListener listener) {
        return writeFileFromIS(file, is, false, listener);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(8:60|8|(3:10|(2:11|(1:13)(1:73))|14)(6:15|(2:16|(1:18)(0))|21|63|26|76)|67|21|63|26|76) */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0051, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0052, code lost:
    
        r0.printStackTrace();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean writeFileFromIS(java.io.File r11, java.io.InputStream r12, boolean r13, com.blankj.utilcode.util.FileIOUtils.OnProgressUpdateListener r14) {
        /*
            r0 = 0
            if (r12 == 0) goto L91
            boolean r1 = com.blankj.utilcode.util.UtilsBridge.createOrExistsFile(r11)
            if (r1 != 0) goto Lb
            goto L91
        Lb:
            r1 = 0
            java.io.BufferedOutputStream r2 = new java.io.BufferedOutputStream     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            r3.<init>(r11, r13)     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            int r4 = com.blankj.utilcode.util.FileIOUtils.sBufferSize     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            r2.<init>(r3, r4)     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            r1 = r2
            r2 = -1
            if (r14 != 0) goto L2c
            int r3 = com.blankj.utilcode.util.FileIOUtils.sBufferSize     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            byte[] r3 = new byte[r3]     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
        L20:
            int r4 = r12.read(r3)     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            r5 = r4
            if (r4 == r2) goto L2b
            r1.write(r3, r0, r5)     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            goto L20
        L2b:
            goto L4c
        L2c:
            int r3 = r12.available()     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            double r3 = (double) r3     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            r5 = 0
            r6 = 0
            r14.onProgressUpdate(r6)     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            int r6 = com.blankj.utilcode.util.FileIOUtils.sBufferSize     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            byte[] r6 = new byte[r6]     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
        L3b:
            int r7 = r12.read(r6)     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            r8 = r7
            if (r7 == r2) goto L4c
            r1.write(r6, r0, r8)     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            int r5 = r5 + r8
            double r9 = (double) r5     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            double r9 = r9 / r3
            r14.onProgressUpdate(r9)     // Catch: java.lang.Throwable -> L60 java.io.IOException -> L62
            goto L3b
        L4c:
            r12.close()     // Catch: java.io.IOException -> L51
            goto L55
        L51:
            r0 = move-exception
            r0.printStackTrace()
        L55:
            r1.close()     // Catch: java.io.IOException -> L5a
            goto L5e
        L5a:
            r0 = move-exception
            r0.printStackTrace()
        L5e:
            r0 = 1
            return r0
        L60:
            r0 = move-exception
            goto L7c
        L62:
            r2 = move-exception
            r2.printStackTrace()     // Catch: java.lang.Throwable -> L60
            r12.close()     // Catch: java.io.IOException -> L6b
            goto L6f
        L6b:
            r3 = move-exception
            r3.printStackTrace()
        L6f:
            if (r1 == 0) goto L7a
            r1.close()     // Catch: java.io.IOException -> L75
            goto L7a
        L75:
            r3 = move-exception
            r3.printStackTrace()
            goto L7b
        L7a:
        L7b:
            return r0
        L7c:
            r12.close()     // Catch: java.io.IOException -> L80
            goto L84
        L80:
            r2 = move-exception
            r2.printStackTrace()
        L84:
            if (r1 == 0) goto L8f
            r1.close()     // Catch: java.io.IOException -> L8a
            goto L8f
        L8a:
            r2 = move-exception
            r2.printStackTrace()
            goto L90
        L8f:
        L90:
            throw r0
        L91:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "create file <"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r11)
            java.lang.String r2 = "> failed."
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "FileIOUtils"
            android.util.Log.e(r2, r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.FileIOUtils.writeFileFromIS(java.io.File, java.io.InputStream, boolean, com.blankj.utilcode.util.FileIOUtils$OnProgressUpdateListener):boolean");
    }

    public static boolean writeFileFromBytesByStream(String filePath, byte[] bytes) {
        return writeFileFromBytesByStream(UtilsBridge.getFileByPath(filePath), bytes, false, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromBytesByStream(String filePath, byte[] bytes, boolean append) {
        return writeFileFromBytesByStream(UtilsBridge.getFileByPath(filePath), bytes, append, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromBytesByStream(File file, byte[] bytes) {
        return writeFileFromBytesByStream(file, bytes, false, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromBytesByStream(File file, byte[] bytes, boolean append) {
        return writeFileFromBytesByStream(file, bytes, append, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromBytesByStream(String filePath, byte[] bytes, OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(UtilsBridge.getFileByPath(filePath), bytes, false, listener);
    }

    public static boolean writeFileFromBytesByStream(String filePath, byte[] bytes, boolean append, OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(UtilsBridge.getFileByPath(filePath), bytes, append, listener);
    }

    public static boolean writeFileFromBytesByStream(File file, byte[] bytes, OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(file, bytes, false, listener);
    }

    public static boolean writeFileFromBytesByStream(File file, byte[] bytes, boolean append, OnProgressUpdateListener listener) {
        if (bytes == null) {
            return false;
        }
        return writeFileFromIS(file, new ByteArrayInputStream(bytes), append, listener);
    }

    public static boolean writeFileFromBytesByChannel(String filePath, byte[] bytes, boolean isForce) {
        return writeFileFromBytesByChannel(UtilsBridge.getFileByPath(filePath), bytes, false, isForce);
    }

    public static boolean writeFileFromBytesByChannel(String filePath, byte[] bytes, boolean append, boolean isForce) {
        return writeFileFromBytesByChannel(UtilsBridge.getFileByPath(filePath), bytes, append, isForce);
    }

    public static boolean writeFileFromBytesByChannel(File file, byte[] bytes, boolean isForce) {
        return writeFileFromBytesByChannel(file, bytes, false, isForce);
    }

    public static boolean writeFileFromBytesByChannel(File file, byte[] bytes, boolean append, boolean isForce) {
        if (bytes == null) {
            Log.e("FileIOUtils", "bytes is null.");
            return false;
        }
        if (!UtilsBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <" + file + "> failed.");
            return false;
        }
        FileChannel fc = null;
        try {
            try {
                FileChannel fc2 = new FileOutputStream(file, append).getChannel();
                if (fc2 == null) {
                    Log.e("FileIOUtils", "fc is null.");
                    if (fc2 != null) {
                        try {
                            fc2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
                fc2.position(fc2.size());
                fc2.write(ByteBuffer.wrap(bytes));
                if (isForce) {
                    fc2.force(true);
                }
                if (fc2 != null) {
                    try {
                        fc2.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                return true;
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        fc.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (IOException e4) {
            e4.printStackTrace();
            if (0 != 0) {
                try {
                    fc.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            return false;
        }
    }

    public static boolean writeFileFromBytesByMap(String filePath, byte[] bytes, boolean isForce) {
        return writeFileFromBytesByMap(filePath, bytes, false, isForce);
    }

    public static boolean writeFileFromBytesByMap(String filePath, byte[] bytes, boolean append, boolean isForce) {
        return writeFileFromBytesByMap(UtilsBridge.getFileByPath(filePath), bytes, append, isForce);
    }

    public static boolean writeFileFromBytesByMap(File file, byte[] bytes, boolean isForce) {
        return writeFileFromBytesByMap(file, bytes, false, isForce);
    }

    public static boolean writeFileFromBytesByMap(File file, byte[] bytes, boolean append, boolean isForce) {
        if (bytes == null || !UtilsBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <" + file + "> failed.");
            return false;
        }
        FileChannel fc = null;
        try {
            try {
                FileChannel fc2 = new FileOutputStream(file, append).getChannel();
                if (fc2 == null) {
                    Log.e("FileIOUtils", "fc is null.");
                    if (fc2 != null) {
                        try {
                            fc2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
                MappedByteBuffer mbb = fc2.map(FileChannel.MapMode.READ_WRITE, fc2.size(), bytes.length);
                mbb.put(bytes);
                if (isForce) {
                    mbb.force();
                }
                if (fc2 == null) {
                    return true;
                }
                try {
                    fc2.close();
                    return true;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return true;
                }
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        fc.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (IOException e4) {
            e4.printStackTrace();
            if (0 != 0) {
                try {
                    fc.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            return false;
        }
    }

    public static boolean writeFileFromString(String filePath, String content) {
        return writeFileFromString(UtilsBridge.getFileByPath(filePath), content, false);
    }

    public static boolean writeFileFromString(String filePath, String content, boolean append) {
        return writeFileFromString(UtilsBridge.getFileByPath(filePath), content, append);
    }

    public static boolean writeFileFromString(File file, String content) {
        return writeFileFromString(file, content, false);
    }

    public static boolean writeFileFromString(File file, String content, boolean append) {
        if (file == null || content == null) {
            return false;
        }
        if (!UtilsBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <" + file + "> failed.");
            return false;
        }
        BufferedWriter bw = null;
        try {
            try {
                bw = new BufferedWriter(new FileWriter(file, append));
                bw.write(content);
                try {
                    bw.close();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return true;
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                return false;
            }
        } catch (Throwable th) {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static List<String> readFile2List(String filePath) {
        return readFile2List(UtilsBridge.getFileByPath(filePath), (String) null);
    }

    public static List<String> readFile2List(String filePath, String charsetName) {
        return readFile2List(UtilsBridge.getFileByPath(filePath), charsetName);
    }

    public static List<String> readFile2List(File file) {
        return readFile2List(file, 0, Integer.MAX_VALUE, (String) null);
    }

    public static List<String> readFile2List(File file, String charsetName) {
        return readFile2List(file, 0, Integer.MAX_VALUE, charsetName);
    }

    public static List<String> readFile2List(String filePath, int st, int end) {
        return readFile2List(UtilsBridge.getFileByPath(filePath), st, end, (String) null);
    }

    public static List<String> readFile2List(String filePath, int st, int end, String charsetName) {
        return readFile2List(UtilsBridge.getFileByPath(filePath), st, end, charsetName);
    }

    public static List<String> readFile2List(File file, int st, int end) {
        return readFile2List(file, st, end, (String) null);
    }

    public static List<String> readFile2List(File file, int st, int end, String charsetName) {
        if (!UtilsBridge.isFileExists(file) || st > end) {
            return null;
        }
        BufferedReader reader = null;
        int curLine = 1;
        try {
            try {
                List<String> list = new ArrayList<>();
                if (UtilsBridge.isSpace(charsetName)) {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                } else {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
                }
                while (true) {
                    String line = reader.readLine();
                    if (line == null || curLine > end) {
                        break;
                    }
                    if (st <= curLine && curLine <= end) {
                        list.add(line);
                    }
                    curLine++;
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return list;
            } catch (IOException e2) {
                e2.printStackTrace();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                return null;
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

    public static String readFile2String(String filePath) {
        return readFile2String(UtilsBridge.getFileByPath(filePath), (String) null);
    }

    public static String readFile2String(String filePath, String charsetName) {
        return readFile2String(UtilsBridge.getFileByPath(filePath), charsetName);
    }

    public static String readFile2String(File file) {
        return readFile2String(file, (String) null);
    }

    public static String readFile2String(File file, String charsetName) {
        byte[] bytes = readFile2BytesByStream(file);
        if (bytes == null) {
            return null;
        }
        if (UtilsBridge.isSpace(charsetName)) {
            return new String(bytes);
        }
        try {
            return new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static byte[] readFile2BytesByStream(String filePath) {
        return readFile2BytesByStream(UtilsBridge.getFileByPath(filePath), (OnProgressUpdateListener) null);
    }

    public static byte[] readFile2BytesByStream(File file) {
        return readFile2BytesByStream(file, (OnProgressUpdateListener) null);
    }

    public static byte[] readFile2BytesByStream(String filePath, OnProgressUpdateListener listener) {
        return readFile2BytesByStream(UtilsBridge.getFileByPath(filePath), listener);
    }

    public static byte[] readFile2BytesByStream(File file, OnProgressUpdateListener listener) {
        if (!UtilsBridge.isFileExists(file)) {
            return null;
        }
        ByteArrayOutputStream os = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file), sBufferSize);
            try {
                try {
                    os = new ByteArrayOutputStream();
                    byte[] b = new byte[sBufferSize];
                    if (listener != null) {
                        double totalSize = is.available();
                        int curSize = 0;
                        listener.onProgressUpdate(0.0d);
                        while (true) {
                            int len = is.read(b, 0, sBufferSize);
                            if (len == -1) {
                                break;
                            }
                            os.write(b, 0, len);
                            curSize += len;
                            listener.onProgressUpdate(((double) curSize) / totalSize);
                        }
                    } else {
                        while (true) {
                            int len2 = is.read(b, 0, sBufferSize);
                            if (len2 == -1) {
                                break;
                            }
                            os.write(b, 0, len2);
                        }
                    }
                    byte[] byteArray = os.toByteArray();
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        os.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    return byteArray;
                } catch (IOException e3) {
                    e3.printStackTrace();
                    try {
                        is.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                    }
                    return null;
                }
            } finally {
            }
        } catch (FileNotFoundException e6) {
            e6.printStackTrace();
            return null;
        }
    }

    public static byte[] readFile2BytesByChannel(String filePath) {
        return readFile2BytesByChannel(UtilsBridge.getFileByPath(filePath));
    }

    public static byte[] readFile2BytesByChannel(File file) {
        if (!UtilsBridge.isFileExists(file)) {
            return null;
        }
        FileChannel fc = null;
        try {
            try {
                FileChannel fc2 = new RandomAccessFile(file, "r").getChannel();
                if (fc2 == null) {
                    Log.e("FileIOUtils", "fc is null.");
                    byte[] bArr = new byte[0];
                    if (fc2 != null) {
                        try {
                            fc2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return bArr;
                }
                ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc2.size());
                while (fc2.read(byteBuffer) > 0) {
                }
                byte[] bArrArray = byteBuffer.array();
                if (fc2 != null) {
                    try {
                        fc2.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                return bArrArray;
            } catch (IOException e3) {
                e3.printStackTrace();
                if (0 != 0) {
                    try {
                        fc.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    fc.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static byte[] readFile2BytesByMap(String filePath) {
        return readFile2BytesByMap(UtilsBridge.getFileByPath(filePath));
    }

    public static byte[] readFile2BytesByMap(File file) {
        if (!UtilsBridge.isFileExists(file)) {
            return null;
        }
        FileChannel fc = null;
        try {
            try {
                FileChannel fc2 = new RandomAccessFile(file, "r").getChannel();
                if (fc2 == null) {
                    Log.e("FileIOUtils", "fc is null.");
                    byte[] bArr = new byte[0];
                    if (fc2 != null) {
                        try {
                            fc2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return bArr;
                }
                int size = (int) fc2.size();
                MappedByteBuffer mbb = fc2.map(FileChannel.MapMode.READ_ONLY, 0L, size).load();
                byte[] result = new byte[size];
                mbb.get(result, 0, size);
                if (fc2 != null) {
                    try {
                        fc2.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                return result;
            } catch (IOException e3) {
                e3.printStackTrace();
                if (0 != 0) {
                    try {
                        fc.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    fc.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static void setBufferSize(int bufferSize) {
        sBufferSize = bufferSize;
    }
}

package com.blankj.utilcode.util;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.StatFs;
import android.text.TextUtils;
import com.blankj.utilcode.constant.RegexConstants;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import kotlin.jvm.internal.ByteCompanionObject;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class FileUtils {
    private static final String LINE_SEP = System.getProperty("line.separator");

    public interface OnReplaceListener {
        boolean onReplace(File file, File file2);
    }

    private FileUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static File getFileByPath(String filePath) {
        if (UtilsBridge.isSpace(filePath)) {
            return null;
        }
        return new File(filePath);
    }

    public static boolean isFileExists(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExists(file.getAbsolutePath());
    }

    public static boolean isFileExists(String filePath) {
        File file = getFileByPath(filePath);
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExistsApi29(filePath);
    }

    private static boolean isFileExistsApi29(String filePath) {
        if (Build.VERSION.SDK_INT < 29) {
            return false;
        }
        try {
            Uri uri = Uri.parse(filePath);
            ContentResolver cr = Utils.getApp().getContentResolver();
            AssetFileDescriptor afd = cr.openAssetFileDescriptor(uri, "r");
            if (afd == null) {
                return false;
            }
            try {
                afd.close();
                return true;
            } catch (IOException e) {
                return true;
            }
        } catch (FileNotFoundException e2) {
            return false;
        }
    }

    public static boolean rename(String filePath, String newName) {
        return rename(getFileByPath(filePath), newName);
    }

    public static boolean rename(File file, String newName) {
        if (file == null || !file.exists() || UtilsBridge.isSpace(newName)) {
            return false;
        }
        if (newName.equals(file.getName())) {
            return true;
        }
        File newFile = new File(file.getParent() + File.separator + newName);
        return !newFile.exists() && file.renameTo(newFile);
    }

    public static boolean isDir(String dirPath) {
        return isDir(getFileByPath(dirPath));
    }

    public static boolean isDir(File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    public static boolean isFile(String filePath) {
        return isFile(getFileByPath(filePath));
    }

    public static boolean isFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

    public static boolean createOrExistsDir(String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    public static boolean createOrExistsDir(File file) {
        return file != null && (!file.exists() ? !file.mkdirs() : !file.isDirectory());
    }

    public static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    public static boolean createOrExistsFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createFileByDeleteOldFile(String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }

    public static boolean createFileByDeleteOldFile(File file) {
        if (file == null) {
            return false;
        }
        if ((file.exists() && !file.delete()) || !createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copy(String srcPath, String destPath) {
        return copy(getFileByPath(srcPath), getFileByPath(destPath), (OnReplaceListener) null);
    }

    public static boolean copy(String srcPath, String destPath, OnReplaceListener listener) {
        return copy(getFileByPath(srcPath), getFileByPath(destPath), listener);
    }

    public static boolean copy(File src, File dest) {
        return copy(src, dest, (OnReplaceListener) null);
    }

    public static boolean copy(File src, File dest, OnReplaceListener listener) {
        if (src == null) {
            return false;
        }
        if (src.isDirectory()) {
            return copyDir(src, dest, listener);
        }
        return copyFile(src, dest, listener);
    }

    private static boolean copyDir(File srcDir, File destDir, OnReplaceListener listener) {
        return copyOrMoveDir(srcDir, destDir, listener, false);
    }

    private static boolean copyFile(File srcFile, File destFile, OnReplaceListener listener) {
        return copyOrMoveFile(srcFile, destFile, listener, false);
    }

    public static boolean move(String srcPath, String destPath) {
        return move(getFileByPath(srcPath), getFileByPath(destPath), (OnReplaceListener) null);
    }

    public static boolean move(String srcPath, String destPath, OnReplaceListener listener) {
        return move(getFileByPath(srcPath), getFileByPath(destPath), listener);
    }

    public static boolean move(File src, File dest) {
        return move(src, dest, (OnReplaceListener) null);
    }

    public static boolean move(File src, File dest, OnReplaceListener listener) {
        if (src == null) {
            return false;
        }
        if (src.isDirectory()) {
            return moveDir(src, dest, listener);
        }
        return moveFile(src, dest, listener);
    }

    public static boolean moveDir(File srcDir, File destDir, OnReplaceListener listener) {
        return copyOrMoveDir(srcDir, destDir, listener, true);
    }

    public static boolean moveFile(File srcFile, File destFile, OnReplaceListener listener) {
        return copyOrMoveFile(srcFile, destFile, listener, true);
    }

    private static boolean copyOrMoveDir(File srcDir, File destDir, OnReplaceListener listener, boolean isMove) {
        if (srcDir == null || destDir == null) {
            return false;
        }
        String srcPath = srcDir.getPath() + File.separator;
        String destPath = destDir.getPath() + File.separator;
        if (destPath.contains(srcPath) || !srcDir.exists() || !srcDir.isDirectory() || !createOrExistsDir(destDir)) {
            return false;
        }
        File[] files = srcDir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                File oneDestFile = new File(destPath + file.getName());
                if (file.isFile()) {
                    if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) {
                        return false;
                    }
                } else if (file.isDirectory() && !copyOrMoveDir(file, oneDestFile, listener, isMove)) {
                    return false;
                }
            }
        }
        return !isMove || deleteDir(srcDir);
    }

    private static boolean copyOrMoveFile(File srcFile, File destFile, OnReplaceListener listener, boolean isMove) {
        if (srcFile == null || destFile == null || srcFile.equals(destFile) || !srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        if (destFile.exists()) {
            if (listener != null && !listener.onReplace(srcFile, destFile)) {
                return true;
            }
            if (!destFile.delete()) {
                return false;
            }
        }
        if (!createOrExistsDir(destFile.getParentFile())) {
            return false;
        }
        try {
            if (!UtilsBridge.writeFileFromIS(destFile.getAbsolutePath(), new FileInputStream(srcFile))) {
                return false;
            }
            if (isMove) {
                if (!deleteFile(srcFile)) {
                    return false;
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(String filePath) {
        return delete(getFileByPath(filePath));
    }

    public static boolean delete(File file) {
        if (file == null) {
            return false;
        }
        if (file.isDirectory()) {
            return deleteDir(file);
        }
        return deleteFile(file);
    }

    private static boolean deleteDir(File dir) {
        if (dir == null) {
            return false;
        }
        if (!dir.exists()) {
            return true;
        }
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        return false;
                    }
                } else if (file.isDirectory() && !deleteDir(file)) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private static boolean deleteFile(File file) {
        return file != null && (!file.exists() || (file.isFile() && file.delete()));
    }

    public static boolean deleteAllInDir(String dirPath) {
        return deleteAllInDir(getFileByPath(dirPath));
    }

    public static boolean deleteAllInDir(File dir) {
        return deleteFilesInDirWithFilter(dir, new FileFilter() { // from class: com.blankj.utilcode.util.FileUtils.1
            @Override // java.io.FileFilter
            public boolean accept(File pathname) {
                return true;
            }
        });
    }

    public static boolean deleteFilesInDir(String dirPath) {
        return deleteFilesInDir(getFileByPath(dirPath));
    }

    public static boolean deleteFilesInDir(File dir) {
        return deleteFilesInDirWithFilter(dir, new FileFilter() { // from class: com.blankj.utilcode.util.FileUtils.2
            @Override // java.io.FileFilter
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }

    public static boolean deleteFilesInDirWithFilter(String dirPath, FileFilter filter) {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter);
    }

    public static boolean deleteFilesInDirWithFilter(File dir, FileFilter filter) {
        if (dir == null || filter == null) {
            return false;
        }
        if (!dir.exists()) {
            return true;
        }
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    if (file.isFile()) {
                        if (!file.delete()) {
                            return false;
                        }
                    } else if (file.isDirectory() && !deleteDir(file)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static List<File> listFilesInDir(String dirPath) {
        return listFilesInDir(dirPath, (Comparator<File>) null);
    }

    public static List<File> listFilesInDir(File dir) {
        return listFilesInDir(dir, (Comparator<File>) null);
    }

    public static List<File> listFilesInDir(String dirPath, Comparator<File> comparator) {
        return listFilesInDir(getFileByPath(dirPath), false, comparator);
    }

    public static List<File> listFilesInDir(File dir, Comparator<File> comparator) {
        return listFilesInDir(dir, false, comparator);
    }

    public static List<File> listFilesInDir(String dirPath, boolean isRecursive) {
        return listFilesInDir(getFileByPath(dirPath), isRecursive);
    }

    public static List<File> listFilesInDir(File dir, boolean isRecursive) {
        return listFilesInDir(dir, isRecursive, (Comparator<File>) null);
    }

    public static List<File> listFilesInDir(String dirPath, boolean isRecursive, Comparator<File> comparator) {
        return listFilesInDir(getFileByPath(dirPath), isRecursive, comparator);
    }

    public static List<File> listFilesInDir(File dir, boolean isRecursive, Comparator<File> comparator) {
        return listFilesInDirWithFilter(dir, new FileFilter() { // from class: com.blankj.utilcode.util.FileUtils.3
            @Override // java.io.FileFilter
            public boolean accept(File pathname) {
                return true;
            }
        }, isRecursive, comparator);
    }

    public static List<File> listFilesInDirWithFilter(String dirPath, FileFilter filter) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter);
    }

    public static List<File> listFilesInDirWithFilter(File dir, FileFilter filter) {
        return listFilesInDirWithFilter(dir, filter, false, (Comparator<File>) null);
    }

    public static List<File> listFilesInDirWithFilter(String dirPath, FileFilter filter, Comparator<File> comparator) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, comparator);
    }

    public static List<File> listFilesInDirWithFilter(File dir, FileFilter filter, Comparator<File> comparator) {
        return listFilesInDirWithFilter(dir, filter, false, comparator);
    }

    public static List<File> listFilesInDirWithFilter(String dirPath, FileFilter filter, boolean isRecursive) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive);
    }

    public static List<File> listFilesInDirWithFilter(File dir, FileFilter filter, boolean isRecursive) {
        return listFilesInDirWithFilter(dir, filter, isRecursive, (Comparator<File>) null);
    }

    public static List<File> listFilesInDirWithFilter(String dirPath, FileFilter filter, boolean isRecursive, Comparator<File> comparator) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive, comparator);
    }

    public static List<File> listFilesInDirWithFilter(File dir, FileFilter filter, boolean isRecursive, Comparator<File> comparator) {
        List<File> files = listFilesInDirWithFilterInner(dir, filter, isRecursive);
        if (comparator != null) {
            Collections.sort(files, comparator);
        }
        return files;
    }

    private static List<File> listFilesInDirWithFilterInner(File dir, FileFilter filter, boolean isRecursive) {
        File[] files;
        List<File> list = new ArrayList<>();
        if (isDir(dir) && (files = dir.listFiles()) != null && files.length > 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    list.add(file);
                }
                if (isRecursive && file.isDirectory()) {
                    list.addAll(listFilesInDirWithFilterInner(file, filter, true));
                }
            }
        }
        return list;
    }

    public static long getFileLastModified(String filePath) {
        return getFileLastModified(getFileByPath(filePath));
    }

    public static long getFileLastModified(File file) {
        if (file == null) {
            return -1L;
        }
        return file.lastModified();
    }

    public static String getFileCharsetSimple(String filePath) {
        return getFileCharsetSimple(getFileByPath(filePath));
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0040 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0043 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0046 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String getFileCharsetSimple(java.io.File r4) {
        /*
            if (r4 != 0) goto L5
            java.lang.String r0 = ""
            return r0
        L5:
            boolean r0 = isUtf8(r4)
            if (r0 == 0) goto Le
            java.lang.String r0 = "UTF-8"
            return r0
        Le:
            r0 = 0
            r1 = 0
            java.io.BufferedInputStream r2 = new java.io.BufferedInputStream     // Catch: java.lang.Throwable -> L31 java.io.IOException -> L33
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L31 java.io.IOException -> L33
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L31 java.io.IOException -> L33
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L31 java.io.IOException -> L33
            r1 = r2
            int r2 = r1.read()     // Catch: java.lang.Throwable -> L31 java.io.IOException -> L33
            int r2 = r2 << 8
            int r3 = r1.read()     // Catch: java.lang.Throwable -> L31 java.io.IOException -> L33
            int r0 = r2 + r3
            r1.close()     // Catch: java.io.IOException -> L2c
        L2b:
            goto L3d
        L2c:
            r2 = move-exception
            r2.printStackTrace()
            goto L3d
        L31:
            r2 = move-exception
            goto L49
        L33:
            r2 = move-exception
            r2.printStackTrace()     // Catch: java.lang.Throwable -> L31
            if (r1 == 0) goto L2b
            r1.close()     // Catch: java.io.IOException -> L2c
            goto L2b
        L3d:
            switch(r0) {
                case 65279: goto L46;
                case 65534: goto L43;
                default: goto L40;
            }
        L40:
            java.lang.String r2 = "GBK"
            return r2
        L43:
            java.lang.String r2 = "Unicode"
            return r2
        L46:
            java.lang.String r2 = "UTF-16BE"
            return r2
        L49:
            if (r1 == 0) goto L54
            r1.close()     // Catch: java.io.IOException -> L4f
            goto L54
        L4f:
            r3 = move-exception
            r3.printStackTrace()
            goto L55
        L54:
        L55:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.FileUtils.getFileCharsetSimple(java.io.File):java.lang.String");
    }

    public static boolean isUtf8(String filePath) {
        return isUtf8(getFileByPath(filePath));
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x0053 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isUtf8(java.io.File r7) {
        /*
            r0 = 0
            if (r7 != 0) goto L4
            return r0
        L4:
            r1 = 0
            r2 = 24
            byte[] r2 = new byte[r2]     // Catch: java.lang.Throwable -> L3e java.io.IOException -> L40
            java.io.BufferedInputStream r3 = new java.io.BufferedInputStream     // Catch: java.lang.Throwable -> L3e java.io.IOException -> L40
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L3e java.io.IOException -> L40
            r4.<init>(r7)     // Catch: java.lang.Throwable -> L3e java.io.IOException -> L40
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L3e java.io.IOException -> L40
            r1 = r3
            int r3 = r1.read(r2)     // Catch: java.lang.Throwable -> L3e java.io.IOException -> L40
            r4 = -1
            if (r3 == r4) goto L33
            byte[] r4 = new byte[r3]     // Catch: java.lang.Throwable -> L3e java.io.IOException -> L40
            java.lang.System.arraycopy(r2, r0, r4, r0, r3)     // Catch: java.lang.Throwable -> L3e java.io.IOException -> L40
            int r5 = isUtf8(r4)     // Catch: java.lang.Throwable -> L3e java.io.IOException -> L40
            r6 = 100
            if (r5 != r6) goto L29
            r0 = 1
        L29:
            r1.close()     // Catch: java.io.IOException -> L2e
            goto L32
        L2e:
            r5 = move-exception
            r5.printStackTrace()
        L32:
            return r0
        L33:
            r1.close()     // Catch: java.io.IOException -> L39
            goto L3d
        L39:
            r4 = move-exception
            r4.printStackTrace()
        L3d:
            return r0
        L3e:
            r0 = move-exception
            goto L51
        L40:
            r2 = move-exception
            r2.printStackTrace()     // Catch: java.lang.Throwable -> L3e
            if (r1 == 0) goto L4f
            r1.close()     // Catch: java.io.IOException -> L4a
            goto L4f
        L4a:
            r2 = move-exception
            r2.printStackTrace()
            goto L50
        L4f:
        L50:
            return r0
        L51:
            if (r1 == 0) goto L5c
            r1.close()     // Catch: java.io.IOException -> L57
            goto L5c
        L57:
            r2 = move-exception
            r2.printStackTrace()
            goto L5d
        L5c:
        L5d:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.FileUtils.isUtf8(java.io.File):boolean");
    }

    private static int isUtf8(byte[] raw) {
        int utf8 = 0;
        int ascii = 0;
        if (raw.length > 3 && raw[0] == -17 && raw[1] == -69 && raw[2] == -65) {
            return 100;
        }
        int len = raw.length;
        int child = 0;
        int i = 0;
        while (i < len) {
            if ((raw[i] & (-1)) == -1 || (raw[i] & (-2)) == -2) {
                return 0;
            }
            if (child == 0) {
                if ((raw[i] & ByteCompanionObject.MAX_VALUE) == raw[i] && raw[i] != 0) {
                    ascii++;
                } else if ((raw[i] & (-64)) == -64) {
                    for (int bit = 0; bit < 8 && (((byte) (128 >> bit)) & raw[i]) == ((byte) (128 >> bit)); bit++) {
                        child = bit;
                    }
                    utf8++;
                }
                i++;
            } else {
                int child2 = raw.length - i > child ? child : raw.length - i;
                boolean currentNotUtf8 = false;
                for (int children = 0; children < child2; children++) {
                    if ((raw[i + children] & ByteCompanionObject.MIN_VALUE) != -128) {
                        if ((raw[i + children] & ByteCompanionObject.MAX_VALUE) == raw[i + children] && raw[i] != 0) {
                            ascii++;
                        }
                        currentNotUtf8 = true;
                    }
                }
                if (currentNotUtf8) {
                    utf8--;
                    i++;
                } else {
                    utf8 += child2;
                    i += child2;
                }
                child = 0;
            }
        }
        if (ascii == len) {
            return 100;
        }
        return (int) (((utf8 + ascii) / len) * 100.0f);
    }

    public static int getFileLines(String filePath) {
        return getFileLines(getFileByPath(filePath));
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:28:0x004f -> B:47:0x005f). Please report as a decompilation issue!!! */
    public static int getFileLines(File file) {
        int count = 1;
        InputStream is = null;
        try {
            try {
                try {
                    InputStream is2 = new BufferedInputStream(new FileInputStream(file));
                    byte[] buffer = new byte[1024];
                    if (!LINE_SEP.endsWith("\n")) {
                        while (true) {
                            int readChars = is2.read(buffer, 0, 1024);
                            if (readChars == -1) {
                                break;
                            }
                            for (int i = 0; i < readChars; i++) {
                                if (buffer[i] == 13) {
                                    count++;
                                }
                            }
                        }
                    } else {
                        while (true) {
                            int readChars2 = is2.read(buffer, 0, 1024);
                            if (readChars2 == -1) {
                                break;
                            }
                            for (int i2 = 0; i2 < readChars2; i2++) {
                                if (buffer[i2] == 10) {
                                    count++;
                                }
                            }
                        }
                    }
                    is2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (0 != 0) {
                        is.close();
                    }
                    return count;
                }
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return count;
    }

    public static String getSize(String filePath) {
        return getSize(getFileByPath(filePath));
    }

    public static String getSize(File file) {
        if (file == null) {
            return "";
        }
        if (file.isDirectory()) {
            return getDirSize(file);
        }
        return getFileSize(file);
    }

    private static String getDirSize(File dir) {
        long len = getDirLength(dir);
        return len == -1 ? "" : UtilsBridge.byte2FitMemorySize(len);
    }

    private static String getFileSize(File file) {
        long len = getFileLength(file);
        return len == -1 ? "" : UtilsBridge.byte2FitMemorySize(len);
    }

    public static long getLength(String filePath) {
        return getLength(getFileByPath(filePath));
    }

    public static long getLength(File file) {
        if (file == null) {
            return 0L;
        }
        if (file.isDirectory()) {
            return getDirLength(file);
        }
        return getFileLength(file);
    }

    private static long getDirLength(File dir) {
        long length;
        if (!isDir(dir)) {
            return 0L;
        }
        long len = 0;
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    length = getDirLength(file);
                } else {
                    length = file.length();
                }
                len += length;
            }
        }
        return len;
    }

    public static long getFileLength(String filePath) {
        boolean isURL = filePath.matches(RegexConstants.REGEX_URL);
        if (isURL) {
            try {
                HttpsURLConnection conn = (HttpsURLConnection) new URL(filePath).openConnection();
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    return conn.getContentLength();
                }
                return -1L;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFileLength(getFileByPath(filePath));
    }

    private static long getFileLength(File file) {
        if (isFile(file)) {
            return file.length();
        }
        return -1L;
    }

    public static String getFileMD5ToString(String filePath) {
        File file = UtilsBridge.isSpace(filePath) ? null : new File(filePath);
        return getFileMD5ToString(file);
    }

    public static String getFileMD5ToString(File file) {
        return UtilsBridge.bytes2HexString(getFileMD5(file));
    }

    public static byte[] getFileMD5(String filePath) {
        return getFileMD5(getFileByPath(filePath));
    }

    public static byte[] getFileMD5(File file) {
        if (file == null) {
            return null;
        }
        DigestInputStream dis = null;
        try {
            try {
                FileInputStream fis = new FileInputStream(file);
                MessageDigest md = MessageDigest.getInstance("MD5");
                dis = new DigestInputStream(fis, md);
                byte[] buffer = new byte[262144];
                while (dis.read(buffer) > 0) {
                }
                MessageDigest md2 = dis.getMessageDigest();
                byte[] bArrDigest = md2.digest();
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bArrDigest;
            } finally {
                if (0 != 0) {
                    try {
                        dis.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        } catch (IOException | NoSuchAlgorithmException e3) {
            e3.printStackTrace();
            return null;
        }
    }

    public static String getDirName(File file) {
        return file == null ? "" : getDirName(file.getAbsolutePath());
    }

    public static String getDirName(String filePath) {
        int lastSep;
        return (UtilsBridge.isSpace(filePath) || (lastSep = filePath.lastIndexOf(File.separator)) == -1) ? "" : filePath.substring(0, lastSep + 1);
    }

    public static String getFileName(File file) {
        return file == null ? "" : getFileName(file.getAbsolutePath());
    }

    public static String getFileName(String filePath) {
        if (UtilsBridge.isSpace(filePath)) {
            return "";
        }
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    public static String getFileNameNoExtension(File file) {
        return file == null ? "" : getFileNameNoExtension(file.getPath());
    }

    public static String getFileNameNoExtension(String filePath) {
        if (UtilsBridge.isSpace(filePath)) {
            return "";
        }
        int lastPoi = filePath.lastIndexOf(46);
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return lastPoi == -1 ? filePath : filePath.substring(0, lastPoi);
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    public static String getFileExtension(File file) {
        return file == null ? "" : getFileExtension(file.getPath());
    }

    public static String getFileExtension(String filePath) {
        if (UtilsBridge.isSpace(filePath)) {
            return "";
        }
        int lastPoi = filePath.lastIndexOf(46);
        int lastSep = filePath.lastIndexOf(File.separator);
        return (lastPoi == -1 || lastSep >= lastPoi) ? "" : filePath.substring(lastPoi + 1);
    }

    public static void notifySystemToScan(String filePath) {
        notifySystemToScan(getFileByPath(filePath));
    }

    public static void notifySystemToScan(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setData(Uri.parse("file://" + file.getAbsolutePath()));
        Utils.getApp().sendBroadcast(intent);
    }

    public static long getFsTotalSize(String anyPathInFs) {
        if (TextUtils.isEmpty(anyPathInFs)) {
            return 0L;
        }
        StatFs statFs = new StatFs(anyPathInFs);
        long blockSize = statFs.getBlockSizeLong();
        long totalSize = statFs.getBlockCountLong();
        return blockSize * totalSize;
    }

    public static long getFsAvailableSize(String anyPathInFs) {
        if (TextUtils.isEmpty(anyPathInFs)) {
            return 0L;
        }
        StatFs statFs = new StatFs(anyPathInFs);
        long blockSize = statFs.getBlockSizeLong();
        long availableSize = statFs.getAvailableBlocksLong();
        return blockSize * availableSize;
    }
}

package com.blankj.utilcode.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public class ArrayUtils {
    public static final int INDEX_NOT_FOUND = -1;

    public interface Closure<E> {
        void execute(int i, E e);
    }

    private ArrayUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static <T> T[] newArray(T... array) {
        return array;
    }

    public static long[] newLongArray(long... array) {
        return array;
    }

    public static int[] newIntArray(int... array) {
        return array;
    }

    public static short[] newShortArray(short... array) {
        return array;
    }

    public static char[] newCharArray(char... array) {
        return array;
    }

    public static byte[] newByteArray(byte... array) {
        return array;
    }

    public static double[] newDoubleArray(double... array) {
        return array;
    }

    public static float[] newFloatArray(float... array) {
        return array;
    }

    public static boolean[] newBooleanArray(boolean... array) {
        return array;
    }

    public static boolean isEmpty(Object array) {
        return getLength(array) == 0;
    }

    public static int getLength(Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }

    public static boolean isSameLength(Object array1, Object array2) {
        return getLength(array1) == getLength(array2);
    }

    public static Object get(Object array, int index) {
        return get(array, index, null);
    }

    public static Object get(Object array, int index, Object defaultValue) {
        if (array == null) {
            return defaultValue;
        }
        try {
            return Array.get(array, index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static void set(Object array, int index, Object value) {
        if (array == null) {
            return;
        }
        Array.set(array, index, value);
    }

    public static boolean equals(Object[] a, Object[] a2) {
        return Arrays.deepEquals(a, a2);
    }

    public static boolean equals(boolean[] a, boolean[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean equals(byte[] a, byte[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean equals(char[] a, char[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean equals(double[] a, double[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean equals(float[] a, float[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean equals(int[] a, int[] a2) {
        return Arrays.equals(a, a2);
    }

    public static boolean equals(short[] a, short[] a2) {
        return Arrays.equals(a, a2);
    }

    public static <T> void reverse(T[] array) {
        if (array == null) {
            return;
        }
        int j = array.length - 1;
        for (int i = 0; j > i; i++) {
            T tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }
    }

    public static void reverse(long[] array) {
        if (array == null) {
            return;
        }
        int j = array.length - 1;
        for (int i = 0; j > i; i++) {
            long tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }
    }

    public static void reverse(int[] array) {
        if (array == null) {
            return;
        }
        int j = array.length - 1;
        for (int i = 0; j > i; i++) {
            int tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }
    }

    public static void reverse(short[] array) {
        if (array == null) {
            return;
        }
        int j = array.length - 1;
        for (int i = 0; j > i; i++) {
            short tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }
    }

    public static void reverse(char[] array) {
        if (array == null) {
            return;
        }
        int j = array.length - 1;
        for (int i = 0; j > i; i++) {
            char tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }
    }

    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int j = array.length - 1;
        for (int i = 0; j > i; i++) {
            byte tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }
    }

    public static void reverse(double[] array) {
        if (array == null) {
            return;
        }
        int j = array.length - 1;
        for (int i = 0; j > i; i++) {
            double tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }
    }

    public static void reverse(float[] array) {
        if (array == null) {
            return;
        }
        int j = array.length - 1;
        for (int i = 0; j > i; i++) {
            float tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }
    }

    public static void reverse(boolean[] array) {
        if (array == null) {
            return;
        }
        int j = array.length - 1;
        for (int i = 0; j > i; i++) {
            boolean tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }
    }

    public static <T> T[] copy(T[] tArr) {
        if (tArr == null) {
            return null;
        }
        return (T[]) subArray(tArr, 0, tArr.length);
    }

    public static long[] copy(long[] array) {
        if (array == null) {
            return null;
        }
        return subArray(array, 0, array.length);
    }

    public static int[] copy(int[] array) {
        if (array == null) {
            return null;
        }
        return subArray(array, 0, array.length);
    }

    public static short[] copy(short[] array) {
        if (array == null) {
            return null;
        }
        return subArray(array, 0, array.length);
    }

    public static char[] copy(char[] array) {
        if (array == null) {
            return null;
        }
        return subArray(array, 0, array.length);
    }

    public static byte[] copy(byte[] array) {
        if (array == null) {
            return null;
        }
        return subArray(array, 0, array.length);
    }

    public static double[] copy(double[] array) {
        if (array == null) {
            return null;
        }
        return subArray(array, 0, array.length);
    }

    public static float[] copy(float[] array) {
        if (array == null) {
            return null;
        }
        return subArray(array, 0, array.length);
    }

    public static boolean[] copy(boolean[] array) {
        if (array == null) {
            return null;
        }
        return subArray(array, 0, array.length);
    }

    private static Object realCopy(Object array) {
        if (array == null) {
            return null;
        }
        return realSubArray(array, 0, getLength(array));
    }

    public static <T> T[] subArray(T[] tArr, int i, int i2) {
        return (T[]) ((Object[]) realSubArray(tArr, i, i2));
    }

    public static long[] subArray(long[] array, int startIndexInclusive, int endIndexExclusive) {
        return (long[]) realSubArray(array, startIndexInclusive, endIndexExclusive);
    }

    public static int[] subArray(int[] array, int startIndexInclusive, int endIndexExclusive) {
        return (int[]) realSubArray(array, startIndexInclusive, endIndexExclusive);
    }

    public static short[] subArray(short[] array, int startIndexInclusive, int endIndexExclusive) {
        return (short[]) realSubArray(array, startIndexInclusive, endIndexExclusive);
    }

    public static char[] subArray(char[] array, int startIndexInclusive, int endIndexExclusive) {
        return (char[]) realSubArray(array, startIndexInclusive, endIndexExclusive);
    }

    public static byte[] subArray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
        return (byte[]) realSubArray(array, startIndexInclusive, endIndexExclusive);
    }

    public static double[] subArray(double[] array, int startIndexInclusive, int endIndexExclusive) {
        return (double[]) realSubArray(array, startIndexInclusive, endIndexExclusive);
    }

    public static float[] subArray(float[] array, int startIndexInclusive, int endIndexExclusive) {
        return (float[]) realSubArray(array, startIndexInclusive, endIndexExclusive);
    }

    public static boolean[] subArray(boolean[] array, int startIndexInclusive, int endIndexExclusive) {
        return (boolean[]) realSubArray(array, startIndexInclusive, endIndexExclusive);
    }

    private static Object realSubArray(Object array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        int length = getLength(array);
        if (endIndexExclusive > length) {
            endIndexExclusive = length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;
        Class<?> componentType = array.getClass().getComponentType();
        if (newSize <= 0) {
            return Array.newInstance(componentType, 0);
        }
        Object subArray = Array.newInstance(componentType, newSize);
        System.arraycopy(array, startIndexInclusive, subArray, 0, newSize);
        return subArray;
    }

    public static <T> T[] add(T[] tArr, T t) {
        return (T[]) ((Object[]) realAddOne(tArr, t, tArr != null ? tArr.getClass() : t != null ? t.getClass() : Object.class));
    }

    public static boolean[] add(boolean[] array, boolean element) {
        return (boolean[]) realAddOne(array, Boolean.valueOf(element), Boolean.TYPE);
    }

    public static byte[] add(byte[] array, byte element) {
        return (byte[]) realAddOne(array, Byte.valueOf(element), Byte.TYPE);
    }

    public static char[] add(char[] array, char element) {
        return (char[]) realAddOne(array, Character.valueOf(element), Character.TYPE);
    }

    public static double[] add(double[] array, double element) {
        return (double[]) realAddOne(array, Double.valueOf(element), Double.TYPE);
    }

    public static float[] add(float[] array, float element) {
        return (float[]) realAddOne(array, Float.valueOf(element), Float.TYPE);
    }

    public static int[] add(int[] array, int element) {
        return (int[]) realAddOne(array, Integer.valueOf(element), Integer.TYPE);
    }

    public static long[] add(long[] array, long element) {
        return (long[]) realAddOne(array, Long.valueOf(element), Long.TYPE);
    }

    public static short[] add(short[] array, short element) {
        return (short[]) realAddOne(array, Short.valueOf(element), Short.TYPE);
    }

    private static Object realAddOne(Object array, Object element, Class newArrayComponentType) {
        Object newArray;
        int arrayLength = 0;
        if (array != null) {
            arrayLength = getLength(array);
            newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
        } else {
            newArray = Array.newInstance((Class<?>) newArrayComponentType, 1);
        }
        Array.set(newArray, arrayLength, element);
        return newArray;
    }

    public static <T> T[] add(T[] tArr, T[] tArr2) {
        return (T[]) ((Object[]) realAddArr(tArr, tArr2));
    }

    public static boolean[] add(boolean[] array1, boolean[] array2) {
        return (boolean[]) realAddArr(array1, array2);
    }

    public static char[] add(char[] array1, char[] array2) {
        return (char[]) realAddArr(array1, array2);
    }

    public static byte[] add(byte[] array1, byte[] array2) {
        return (byte[]) realAddArr(array1, array2);
    }

    public static short[] add(short[] array1, short[] array2) {
        return (short[]) realAddArr(array1, array2);
    }

    public static int[] add(int[] array1, int[] array2) {
        return (int[]) realAddArr(array1, array2);
    }

    public static long[] add(long[] array1, long[] array2) {
        return (long[]) realAddArr(array1, array2);
    }

    public static float[] add(float[] array1, float[] array2) {
        return (float[]) realAddArr(array1, array2);
    }

    public static double[] add(double[] array1, double[] array2) {
        return (double[]) realAddArr(array1, array2);
    }

    private static Object realAddArr(Object array1, Object array2) {
        if (array1 == null && array2 == null) {
            return null;
        }
        if (array1 == null) {
            return realCopy(array2);
        }
        if (array2 == null) {
            return realCopy(array1);
        }
        int len1 = getLength(array1);
        int len2 = getLength(array2);
        Object joinedArray = Array.newInstance(array1.getClass().getComponentType(), len1 + len2);
        System.arraycopy(array1, 0, joinedArray, 0, len1);
        System.arraycopy(array2, 0, joinedArray, len1, len2);
        return joinedArray;
    }

    public static <T> T[] add(T[] tArr, int i, T[] tArr2) {
        Class<?> componentType;
        if (tArr != null) {
            componentType = tArr.getClass().getComponentType();
        } else if (tArr2 != null) {
            componentType = tArr2.getClass().getComponentType();
        } else {
            return null;
        }
        return (T[]) ((Object[]) realAddArr(tArr, i, tArr2, componentType));
    }

    public static boolean[] add(boolean[] array1, int index, boolean[] array2) {
        Object result = realAddArr(array1, index, array2, Boolean.TYPE);
        if (result == null) {
            return null;
        }
        return (boolean[]) result;
    }

    public static char[] add(char[] array1, int index, char[] array2) {
        Object result = realAddArr(array1, index, array2, Character.TYPE);
        if (result == null) {
            return null;
        }
        return (char[]) result;
    }

    public static byte[] add(byte[] array1, int index, byte[] array2) {
        Object result = realAddArr(array1, index, array2, Byte.TYPE);
        if (result == null) {
            return null;
        }
        return (byte[]) result;
    }

    public static short[] add(short[] array1, int index, short[] array2) {
        Object result = realAddArr(array1, index, array2, Short.TYPE);
        if (result == null) {
            return null;
        }
        return (short[]) result;
    }

    public static int[] add(int[] array1, int index, int[] array2) {
        Object result = realAddArr(array1, index, array2, Integer.TYPE);
        if (result == null) {
            return null;
        }
        return (int[]) result;
    }

    public static long[] add(long[] array1, int index, long[] array2) {
        Object result = realAddArr(array1, index, array2, Long.TYPE);
        if (result == null) {
            return null;
        }
        return (long[]) result;
    }

    public static float[] add(float[] array1, int index, float[] array2) {
        Object result = realAddArr(array1, index, array2, Float.TYPE);
        if (result == null) {
            return null;
        }
        return (float[]) result;
    }

    public static double[] add(double[] array1, int index, double[] array2) {
        Object result = realAddArr(array1, index, array2, Double.TYPE);
        if (result == null) {
            return null;
        }
        return (double[]) result;
    }

    private static Object realAddArr(Object array1, int index, Object array2, Class clss) {
        if (array1 == null && array2 == null) {
            return null;
        }
        int len1 = getLength(array1);
        int len2 = getLength(array2);
        if (len1 == 0) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", array1 Length: 0");
            }
            return realCopy(array2);
        }
        if (len2 == 0) {
            return realCopy(array1);
        }
        if (index > len1 || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", array1 Length: " + len1);
        }
        Object joinedArray = Array.newInstance(array1.getClass().getComponentType(), len1 + len2);
        if (index == len1) {
            System.arraycopy(array1, 0, joinedArray, 0, len1);
            System.arraycopy(array2, 0, joinedArray, len1, len2);
        } else if (index == 0) {
            System.arraycopy(array2, 0, joinedArray, 0, len2);
            System.arraycopy(array1, 0, joinedArray, len2, len1);
        } else {
            System.arraycopy(array1, 0, joinedArray, 0, index);
            System.arraycopy(array2, 0, joinedArray, index, len2);
            System.arraycopy(array1, index, joinedArray, index + len2, len1 - index);
        }
        return joinedArray;
    }

    public static <T> T[] add(T[] tArr, int i, T t) {
        Class<?> componentType;
        if (tArr != null) {
            componentType = tArr.getClass().getComponentType();
        } else if (t != null) {
            componentType = t.getClass();
        } else {
            T[] tArr2 = (T[]) new Object[1];
            tArr2[0] = null;
            return tArr2;
        }
        return (T[]) ((Object[]) realAdd(tArr, i, t, componentType));
    }

    public static boolean[] add(boolean[] array, int index, boolean element) {
        return (boolean[]) realAdd(array, index, Boolean.valueOf(element), Boolean.TYPE);
    }

    public static char[] add(char[] array, int index, char element) {
        return (char[]) realAdd(array, index, Character.valueOf(element), Character.TYPE);
    }

    public static byte[] add(byte[] array, int index, byte element) {
        return (byte[]) realAdd(array, index, Byte.valueOf(element), Byte.TYPE);
    }

    public static short[] add(short[] array, int index, short element) {
        return (short[]) realAdd(array, index, Short.valueOf(element), Short.TYPE);
    }

    public static int[] add(int[] array, int index, int element) {
        return (int[]) realAdd(array, index, Integer.valueOf(element), Integer.TYPE);
    }

    public static long[] add(long[] array, int index, long element) {
        return (long[]) realAdd(array, index, Long.valueOf(element), Long.TYPE);
    }

    public static float[] add(float[] array, int index, float element) {
        return (float[]) realAdd(array, index, Float.valueOf(element), Float.TYPE);
    }

    public static double[] add(double[] array, int index, double element) {
        return (double[]) realAdd(array, index, Double.valueOf(element), Double.TYPE);
    }

    private static Object realAdd(Object array, int index, Object element, Class clss) {
        if (array == null) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
            }
            Object joinedArray = Array.newInstance((Class<?>) clss, 1);
            Array.set(joinedArray, 0, element);
            return joinedArray;
        }
        int length = Array.getLength(array);
        if (index > length || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        Object result = Array.newInstance((Class<?>) clss, length + 1);
        System.arraycopy(array, 0, result, 0, index);
        Array.set(result, index, element);
        if (index < length) {
            System.arraycopy(array, index, result, index + 1, length - index);
        }
        return result;
    }

    public static Object[] remove(Object[] array, int index) {
        if (array == null) {
            return null;
        }
        return (Object[]) remove((Object) array, index);
    }

    public static Object[] removeElement(Object[] array, Object element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return copy(array);
        }
        return remove(array, index);
    }

    public static boolean[] remove(boolean[] array, int index) {
        if (array == null) {
            return null;
        }
        return (boolean[]) remove((Object) array, index);
    }

    public static boolean[] removeElement(boolean[] array, boolean element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return copy(array);
        }
        return remove(array, index);
    }

    public static byte[] remove(byte[] array, int index) {
        if (array == null) {
            return null;
        }
        return (byte[]) remove((Object) array, index);
    }

    public static byte[] removeElement(byte[] array, byte element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return copy(array);
        }
        return remove(array, index);
    }

    public static char[] remove(char[] array, int index) {
        if (array == null) {
            return null;
        }
        return (char[]) remove((Object) array, index);
    }

    public static char[] removeElement(char[] array, char element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return copy(array);
        }
        return remove(array, index);
    }

    public static double[] remove(double[] array, int index) {
        if (array == null) {
            return null;
        }
        return (double[]) remove((Object) array, index);
    }

    public static double[] removeElement(double[] array, double element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return copy(array);
        }
        return remove(array, index);
    }

    public static float[] remove(float[] array, int index) {
        if (array == null) {
            return null;
        }
        return (float[]) remove((Object) array, index);
    }

    public static float[] removeElement(float[] array, float element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return copy(array);
        }
        return remove(array, index);
    }

    public static int[] remove(int[] array, int index) {
        if (array == null) {
            return null;
        }
        return (int[]) remove((Object) array, index);
    }

    public static int[] removeElement(int[] array, int element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return copy(array);
        }
        return remove(array, index);
    }

    public static long[] remove(long[] array, int index) {
        if (array == null) {
            return null;
        }
        return (long[]) remove((Object) array, index);
    }

    public static long[] removeElement(long[] array, long element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return copy(array);
        }
        return remove(array, index);
    }

    public static short[] remove(short[] array, int index) {
        if (array == null) {
            return null;
        }
        return (short[]) remove((Object) array, index);
    }

    public static short[] removeElement(short[] array, short element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return copy(array);
        }
        return remove(array, index);
    }

    private static Object remove(Object array, int index) {
        int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, (length - index) - 1);
        }
        return result;
    }

    public static int indexOf(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i2 = startIndex; i2 < array.length; i2++) {
                if (objectToFind.equals(array[i2])) {
                    return i2;
                }
            }
        }
        return -1;
    }

    public static int lastIndexOf(Object[] array, Object objectToFind) {
        return lastIndexOf(array, objectToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i >= 0; i--) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i2 = startIndex; i2 >= 0; i2--) {
                if (objectToFind.equals(array[i2])) {
                    return i2;
                }
            }
        }
        return -1;
    }

    public static boolean contains(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind) != -1;
    }

    public static int indexOf(long[] array, long valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(long[] array, long valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(long[] array, long valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(int[] array, int valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(int[] array, int valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(int[] array, int valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(short[] array, short valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(short[] array, short valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(short[] array, short valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(char[] array, char valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(char[] array, char valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(char[] array, char valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(byte[] array, byte valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(byte[] array, byte valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(byte[] array, byte valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(double[] array, double valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(double[] array, double valueToFind, double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance);
    }

    public static int indexOf(double[] array, double valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i < array.length; i++) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(double[] array, double valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(double[] array, double valueToFind, double tolerance) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE, tolerance);
    }

    public static int lastIndexOf(double[] array, double valueToFind, int startIndex) {
        if (isEmpty(array) || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
        if (isEmpty(array) || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i >= 0; i--) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(double[] array, double valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static boolean contains(double[] array, double valueToFind, double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance) != -1;
    }

    public static int indexOf(float[] array, float valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(float[] array, float valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(float[] array, float valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(float[] array, float valueToFind, int startIndex) {
        if (isEmpty(array) || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(float[] array, float valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static int indexOf(boolean[] array, boolean valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    public static int indexOf(boolean[] array, boolean valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(boolean[] array, boolean valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    public static int lastIndexOf(boolean[] array, boolean valueToFind, int startIndex) {
        if (isEmpty(array) || startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(boolean[] array, boolean valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }

    public static char[] toPrimitive(Character[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new char[0];
        }
        char[] result = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].charValue();
        }
        return result;
    }

    public static char[] toPrimitive(Character[] array, char valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new char[0];
        }
        char[] result = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            Character b = array[i];
            result[i] = b == null ? valueForNull : b.charValue();
        }
        return result;
    }

    public static Character[] toObject(char[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new Character[0];
        }
        Character[] result = new Character[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Character(array[i]);
        }
        return result;
    }

    public static long[] toPrimitive(Long[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new long[0];
        }
        long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].longValue();
        }
        return result;
    }

    public static long[] toPrimitive(Long[] array, long valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new long[0];
        }
        long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            Long b = array[i];
            result[i] = b == null ? valueForNull : b.longValue();
        }
        return result;
    }

    public static Long[] toObject(long[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new Long[0];
        }
        Long[] result = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Long(array[i]);
        }
        return result;
    }

    public static int[] toPrimitive(Integer[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new int[0];
        }
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
    }

    public static int[] toPrimitive(Integer[] array, int valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new int[0];
        }
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            Integer b = array[i];
            result[i] = b == null ? valueForNull : b.intValue();
        }
        return result;
    }

    public static Integer[] toObject(int[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new Integer[0];
        }
        Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Integer(array[i]);
        }
        return result;
    }

    public static short[] toPrimitive(Short[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new short[0];
        }
        short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].shortValue();
        }
        return result;
    }

    public static short[] toPrimitive(Short[] array, short valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new short[0];
        }
        short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            Short b = array[i];
            result[i] = b == null ? valueForNull : b.shortValue();
        }
        return result;
    }

    public static Short[] toObject(short[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new Short[0];
        }
        Short[] result = new Short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Short(array[i]);
        }
        return result;
    }

    public static byte[] toPrimitive(Byte[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new byte[0];
        }
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }

    public static byte[] toPrimitive(Byte[] array, byte valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new byte[0];
        }
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            Byte b = array[i];
            result[i] = b == null ? valueForNull : b.byteValue();
        }
        return result;
    }

    public static Byte[] toObject(byte[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new Byte[0];
        }
        Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Byte(array[i]);
        }
        return result;
    }

    public static double[] toPrimitive(Double[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new double[0];
        }
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }

    public static double[] toPrimitive(Double[] array, double valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new double[0];
        }
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            Double b = array[i];
            result[i] = b == null ? valueForNull : b.doubleValue();
        }
        return result;
    }

    public static Double[] toObject(double[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new Double[0];
        }
        Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Double(array[i]);
        }
        return result;
    }

    public static float[] toPrimitive(Float[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new float[0];
        }
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
    }

    public static float[] toPrimitive(Float[] array, float valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new float[0];
        }
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            Float b = array[i];
            result[i] = b == null ? valueForNull : b.floatValue();
        }
        return result;
    }

    public static Float[] toObject(float[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new Float[0];
        }
        Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = new Float(array[i]);
        }
        return result;
    }

    public static boolean[] toPrimitive(Boolean[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new boolean[0];
        }
        boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].booleanValue();
        }
        return result;
    }

    public static boolean[] toPrimitive(Boolean[] array, boolean valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new boolean[0];
        }
        boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            Boolean b = array[i];
            result[i] = b == null ? valueForNull : b.booleanValue();
        }
        return result;
    }

    public static Boolean[] toObject(boolean[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return new Boolean[0];
        }
        Boolean[] result = new Boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i] ? Boolean.TRUE : Boolean.FALSE;
        }
        return result;
    }

    public static <T> List<T> asList(T... array) {
        if (array == null || array.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(array);
    }

    public static <T> List<T> asUnmodifiableList(T... array) {
        return Collections.unmodifiableList(asList(array));
    }

    public static <T> List<T> asArrayList(T... array) {
        List<T> list = new ArrayList<>();
        if (array == null || array.length == 0) {
            return list;
        }
        list.addAll(Arrays.asList(array));
        return list;
    }

    public static <T> List<T> asLinkedList(T... array) {
        List<T> list = new LinkedList<>();
        if (array == null || array.length == 0) {
            return list;
        }
        list.addAll(Arrays.asList(array));
        return list;
    }

    public static <T> void sort(T[] array, Comparator<? super T> c) {
        if (array == null || array.length < 2) {
            return;
        }
        Arrays.sort(array, c);
    }

    public static void sort(byte[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        Arrays.sort(array);
    }

    public static void sort(char[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        Arrays.sort(array);
    }

    public static void sort(double[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        Arrays.sort(array);
    }

    public static void sort(float[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        Arrays.sort(array);
    }

    public static void sort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        Arrays.sort(array);
    }

    public static void sort(long[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        Arrays.sort(array);
    }

    public static void sort(short[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        Arrays.sort(array);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <E> void forAllDo(Object array, Closure<E> closure) {
        if (array == null || closure == 0) {
            return;
        }
        if (array instanceof Object[]) {
            Object[] objects = (Object[]) array;
            int length = objects.length;
            for (int i = 0; i < length; i++) {
                Object ele = objects[i];
                closure.execute(i, ele);
            }
            return;
        }
        if (array instanceof boolean[]) {
            boolean[] booleans = (boolean[]) array;
            int length2 = booleans.length;
            for (int i2 = 0; i2 < length2; i2++) {
                boolean ele2 = booleans[i2];
                closure.execute(i2, ele2 ? Boolean.TRUE : Boolean.FALSE);
            }
            return;
        }
        if (array instanceof byte[]) {
            byte[] bytes = (byte[]) array;
            int length3 = bytes.length;
            for (int i3 = 0; i3 < length3; i3++) {
                byte ele3 = bytes[i3];
                closure.execute(i3, Byte.valueOf(ele3));
            }
            return;
        }
        if (array instanceof char[]) {
            char[] chars = (char[]) array;
            int length4 = chars.length;
            for (int i4 = 0; i4 < length4; i4++) {
                char ele4 = chars[i4];
                closure.execute(i4, Character.valueOf(ele4));
            }
            return;
        }
        if (array instanceof short[]) {
            short[] shorts = (short[]) array;
            int length5 = shorts.length;
            for (int i5 = 0; i5 < length5; i5++) {
                short ele5 = shorts[i5];
                closure.execute(i5, Short.valueOf(ele5));
            }
            return;
        }
        if (array instanceof int[]) {
            int[] ints = (int[]) array;
            int length6 = ints.length;
            for (int i6 = 0; i6 < length6; i6++) {
                int ele6 = ints[i6];
                closure.execute(i6, Integer.valueOf(ele6));
            }
            return;
        }
        if (array instanceof long[]) {
            long[] longs = (long[]) array;
            int length7 = longs.length;
            for (int i7 = 0; i7 < length7; i7++) {
                long ele7 = longs[i7];
                closure.execute(i7, Long.valueOf(ele7));
            }
            return;
        }
        if (array instanceof float[]) {
            float[] floats = (float[]) array;
            int length8 = floats.length;
            for (int i8 = 0; i8 < length8; i8++) {
                float ele8 = floats[i8];
                closure.execute(i8, Float.valueOf(ele8));
            }
            return;
        }
        if (array instanceof double[]) {
            double[] doubles = (double[]) array;
            int length9 = doubles.length;
            for (int i9 = 0; i9 < length9; i9++) {
                double ele9 = doubles[i9];
                closure.execute(i9, Double.valueOf(ele9));
            }
            return;
        }
        throw new IllegalArgumentException("Not an array: " + array.getClass());
    }

    public static String toString(Object array) {
        if (array == null) {
            return "null";
        }
        if (array instanceof Object[]) {
            return Arrays.deepToString((Object[]) array);
        }
        if (array instanceof boolean[]) {
            return Arrays.toString((boolean[]) array);
        }
        if (array instanceof byte[]) {
            return Arrays.toString((byte[]) array);
        }
        if (array instanceof char[]) {
            return Arrays.toString((char[]) array);
        }
        if (array instanceof double[]) {
            return Arrays.toString((double[]) array);
        }
        if (array instanceof float[]) {
            return Arrays.toString((float[]) array);
        }
        if (array instanceof int[]) {
            return Arrays.toString((int[]) array);
        }
        if (array instanceof long[]) {
            return Arrays.toString((long[]) array);
        }
        if (array instanceof short[]) {
            return Arrays.toString((short[]) array);
        }
        throw new IllegalArgumentException("Array has incompatible type: " + array.getClass());
    }
}

package com.blankj.utilcode.util;

import android.util.Log;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class BusUtils {
    private static final Object NULL = "nULl";
    private static final String TAG = "BusUtils";
    private final Map<String, Set<Object>> mClassName_BusesMap;
    private final Map<String, Map<String, Object>> mClassName_Tag_Arg4StickyMap;
    private final Map<String, List<String>> mClassName_TagsMap;
    private final Map<String, List<BusInfo>> mTag_BusInfoListMap;

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.CLASS)
    public @interface Bus {
        int priority() default 0;

        boolean sticky() default false;

        String tag();

        ThreadMode threadMode() default ThreadMode.POSTING;
    }

    public enum ThreadMode {
        MAIN,
        IO,
        CPU,
        CACHED,
        SINGLE,
        POSTING
    }

    private BusUtils() {
        this.mTag_BusInfoListMap = new ConcurrentHashMap();
        this.mClassName_BusesMap = new ConcurrentHashMap();
        this.mClassName_TagsMap = new ConcurrentHashMap();
        this.mClassName_Tag_Arg4StickyMap = new ConcurrentHashMap();
        init();
    }

    private void init() {
    }

    private void registerBus(String tag, String className, String funName, String paramType, String paramName, boolean sticky, String threadMode) {
        registerBus(tag, className, funName, paramType, paramName, sticky, threadMode, 0);
    }

    private void registerBus(String tag, String className, String funName, String paramType, String paramName, boolean sticky, String threadMode, int priority) {
        List<BusInfo> busInfoList;
        List<BusInfo> busInfoList2 = this.mTag_BusInfoListMap.get(tag);
        if (busInfoList2 != null) {
            busInfoList = busInfoList2;
        } else {
            List<BusInfo> busInfoList3 = new CopyOnWriteArrayList<>();
            this.mTag_BusInfoListMap.put(tag, busInfoList3);
            busInfoList = busInfoList3;
        }
        busInfoList.add(new BusInfo(tag, className, funName, paramType, paramName, sticky, threadMode, priority));
    }

    public static void register(Object bus) {
        getInstance().registerInner(bus);
    }

    public static void unregister(Object bus) {
        getInstance().unregisterInner(bus);
    }

    public static void post(String tag) {
        post(tag, NULL);
    }

    public static void post(String tag, Object arg) {
        getInstance().postInner(tag, arg);
    }

    public static void postSticky(String tag) {
        postSticky(tag, NULL);
    }

    public static void postSticky(String tag, Object arg) {
        getInstance().postStickyInner(tag, arg);
    }

    public static void removeSticky(String tag) {
        getInstance().removeStickyInner(tag);
    }

    public static String toString_() {
        return getInstance().toString();
    }

    public String toString() {
        return "BusUtils: " + this.mTag_BusInfoListMap;
    }

    private static BusUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    private void registerInner(Object bus) {
        if (bus == null) {
            return;
        }
        Class<?> aClass = bus.getClass();
        String className = aClass.getName();
        boolean isNeedRecordTags = false;
        synchronized (this.mClassName_BusesMap) {
            Set<Object> buses = this.mClassName_BusesMap.get(className);
            if (buses == null) {
                buses = new CopyOnWriteArraySet();
                this.mClassName_BusesMap.put(className, buses);
                isNeedRecordTags = true;
            }
            if (buses.contains(bus)) {
                Log.w(TAG, "The bus of <" + bus + "> already registered.");
                return;
            }
            buses.add(bus);
            if (isNeedRecordTags) {
                recordTags(aClass, className);
            }
            consumeStickyIfExist(bus);
        }
    }

    private void recordTags(Class<?> aClass, String className) {
        List<String> tags = this.mClassName_TagsMap.get(className);
        if (tags == null) {
            synchronized (this.mClassName_TagsMap) {
                List<String> tags2 = this.mClassName_TagsMap.get(className);
                if (tags2 == null) {
                    List<String> tags3 = new CopyOnWriteArrayList<>();
                    for (Map.Entry<String, List<BusInfo>> entry : this.mTag_BusInfoListMap.entrySet()) {
                        for (BusInfo busInfo : entry.getValue()) {
                            try {
                                if (Class.forName(busInfo.className).isAssignableFrom(aClass)) {
                                    tags3.add(entry.getKey());
                                    busInfo.subClassNames.add(className);
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    this.mClassName_TagsMap.put(className, tags3);
                }
            }
        }
    }

    private void consumeStickyIfExist(Object bus) {
        Map<String, Object> tagArgMap = this.mClassName_Tag_Arg4StickyMap.get(bus.getClass().getName());
        if (tagArgMap == null) {
            return;
        }
        synchronized (this.mClassName_Tag_Arg4StickyMap) {
            for (Map.Entry<String, Object> tagArgEntry : tagArgMap.entrySet()) {
                consumeSticky(bus, tagArgEntry.getKey(), tagArgEntry.getValue());
            }
        }
    }

    private void consumeSticky(Object bus, String tag, Object arg) {
        List<BusInfo> busInfoList = this.mTag_BusInfoListMap.get(tag);
        if (busInfoList == null) {
            Log.e(TAG, "The bus of tag <" + tag + "> is not exists.");
            return;
        }
        for (BusInfo busInfo : busInfoList) {
            if (busInfo.subClassNames.contains(bus.getClass().getName()) && busInfo.sticky) {
                synchronized (this.mClassName_Tag_Arg4StickyMap) {
                    Map<String, Object> tagArgMap = this.mClassName_Tag_Arg4StickyMap.get(busInfo.className);
                    if (tagArgMap != null && tagArgMap.containsKey(tag)) {
                        invokeBus(bus, arg, busInfo, true);
                    }
                }
            }
        }
    }

    private void unregisterInner(Object bus) {
        if (bus == null) {
            return;
        }
        String className = bus.getClass().getName();
        synchronized (this.mClassName_BusesMap) {
            Set<Object> buses = this.mClassName_BusesMap.get(className);
            if (buses != null && buses.contains(bus)) {
                buses.remove(bus);
                return;
            }
            Log.e(TAG, "The bus of <" + bus + "> was not registered before.");
        }
    }

    private void postInner(String tag, Object arg) {
        postInner(tag, arg, false);
    }

    private void postInner(String tag, Object arg, boolean sticky) {
        List<BusInfo> busInfoList = this.mTag_BusInfoListMap.get(tag);
        if (busInfoList == null) {
            Log.e(TAG, "The bus of tag <" + tag + "> is not exists.");
            if (this.mTag_BusInfoListMap.isEmpty()) {
                Log.e(TAG, "Please check whether the bus plugin is applied.");
                return;
            }
            return;
        }
        for (BusInfo busInfo : busInfoList) {
            invokeBus(arg, busInfo, sticky);
        }
    }

    private void invokeBus(Object arg, BusInfo busInfo, boolean sticky) {
        invokeBus(null, arg, busInfo, sticky);
    }

    private void invokeBus(Object bus, Object arg, BusInfo busInfo, boolean sticky) {
        if (busInfo.method == null) {
            Method method = getMethodByBusInfo(busInfo);
            if (method == null) {
                return;
            } else {
                busInfo.method = method;
            }
        }
        invokeMethod(bus, arg, busInfo, sticky);
    }

    private Method getMethodByBusInfo(BusInfo busInfo) {
        try {
            return "".equals(busInfo.paramType) ? Class.forName(busInfo.className).getDeclaredMethod(busInfo.funName, new Class[0]) : Class.forName(busInfo.className).getDeclaredMethod(busInfo.funName, getClassName(busInfo.paramType));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0059  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.Class getClassName(java.lang.String r2) throws java.lang.ClassNotFoundException {
        /*
            r1 = this;
            int r0 = r2.hashCode()
            switch(r0) {
                case -1325958191: goto L4f;
                case 104431: goto L45;
                case 3039496: goto L3b;
                case 3052374: goto L31;
                case 3327612: goto L27;
                case 64711720: goto L1d;
                case 97526364: goto L13;
                case 109413500: goto L8;
                default: goto L7;
            }
        L7:
            goto L59
        L8:
            java.lang.String r0 = "short"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L7
            r0 = 3
            goto L5a
        L13:
            java.lang.String r0 = "float"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L7
            r0 = 6
            goto L5a
        L1d:
            java.lang.String r0 = "boolean"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L7
            r0 = 0
            goto L5a
        L27:
            java.lang.String r0 = "long"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L7
            r0 = 2
            goto L5a
        L31:
            java.lang.String r0 = "char"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L7
            r0 = 7
            goto L5a
        L3b:
            java.lang.String r0 = "byte"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L7
            r0 = 4
            goto L5a
        L45:
            java.lang.String r0 = "int"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L7
            r0 = 1
            goto L5a
        L4f:
            java.lang.String r0 = "double"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L7
            r0 = 5
            goto L5a
        L59:
            r0 = -1
        L5a:
            switch(r0) {
                case 0: goto L77;
                case 1: goto L74;
                case 2: goto L71;
                case 3: goto L6e;
                case 4: goto L6b;
                case 5: goto L68;
                case 6: goto L65;
                case 7: goto L62;
                default: goto L5d;
            }
        L5d:
            java.lang.Class r0 = java.lang.Class.forName(r2)
            return r0
        L62:
            java.lang.Class r0 = java.lang.Character.TYPE
            return r0
        L65:
            java.lang.Class r0 = java.lang.Float.TYPE
            return r0
        L68:
            java.lang.Class r0 = java.lang.Double.TYPE
            return r0
        L6b:
            java.lang.Class r0 = java.lang.Byte.TYPE
            return r0
        L6e:
            java.lang.Class r0 = java.lang.Short.TYPE
            return r0
        L71:
            java.lang.Class r0 = java.lang.Long.TYPE
            return r0
        L74:
            java.lang.Class r0 = java.lang.Integer.TYPE
            return r0
        L77:
            java.lang.Class r0 = java.lang.Boolean.TYPE
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.BusUtils.getClassName(java.lang.String):java.lang.Class");
    }

    private void invokeMethod(Object arg, BusInfo busInfo, boolean sticky) {
        invokeMethod(null, arg, busInfo, sticky);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0047  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void invokeMethod(final java.lang.Object r8, final java.lang.Object r9, final com.blankj.utilcode.util.BusUtils.BusInfo r10, final boolean r11) {
        /*
            r7 = this;
            com.blankj.utilcode.util.BusUtils$1 r6 = new com.blankj.utilcode.util.BusUtils$1
            r0 = r6
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r10
            r5 = r11
            r0.<init>()
            java.lang.String r1 = r10.threadMode
            int r2 = r1.hashCode()
            switch(r2) {
                case -1848936376: goto L3d;
                case 2342: goto L33;
                case 66952: goto L29;
                case 2358713: goto L1f;
                case 1980249378: goto L15;
                default: goto L14;
            }
        L14:
            goto L47
        L15:
            java.lang.String r2 = "CACHED"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L14
            r1 = 3
            goto L48
        L1f:
            java.lang.String r2 = "MAIN"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L14
            r1 = 0
            goto L48
        L29:
            java.lang.String r2 = "CPU"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L14
            r1 = 2
            goto L48
        L33:
            java.lang.String r2 = "IO"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L14
            r1 = 1
            goto L48
        L3d:
            java.lang.String r2 = "SINGLE"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L14
            r1 = 4
            goto L48
        L47:
            r1 = -1
        L48:
            switch(r1) {
                case 0: goto L6f;
                case 1: goto L67;
                case 2: goto L5f;
                case 3: goto L57;
                case 4: goto L4f;
                default: goto L4b;
            }
        L4b:
            r0.run()
            return
        L4f:
            java.util.concurrent.ExecutorService r1 = com.blankj.utilcode.util.ThreadUtils.getSinglePool()
            r1.execute(r0)
            return
        L57:
            java.util.concurrent.ExecutorService r1 = com.blankj.utilcode.util.ThreadUtils.getCachedPool()
            r1.execute(r0)
            return
        L5f:
            java.util.concurrent.ExecutorService r1 = com.blankj.utilcode.util.ThreadUtils.getCpuPool()
            r1.execute(r0)
            return
        L67:
            java.util.concurrent.ExecutorService r1 = com.blankj.utilcode.util.ThreadUtils.getIoPool()
            r1.execute(r0)
            return
        L6f:
            com.blankj.utilcode.util.ThreadUtils.runOnUiThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.BusUtils.invokeMethod(java.lang.Object, java.lang.Object, com.blankj.utilcode.util.BusUtils$BusInfo, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void realInvokeMethod(Object bus, Object arg, BusInfo busInfo, boolean sticky) {
        Set<Object> buses = new HashSet<>();
        if (bus == null) {
            for (String subClassName : busInfo.subClassNames) {
                Set<Object> subBuses = this.mClassName_BusesMap.get(subClassName);
                if (subBuses != null && !subBuses.isEmpty()) {
                    buses.addAll(subBuses);
                }
            }
            if (buses.size() == 0) {
                if (!sticky) {
                    Log.e(TAG, "The " + busInfo + " was not registered before.");
                    return;
                }
                return;
            }
        } else {
            buses.add(bus);
        }
        invokeBuses(arg, busInfo, buses);
    }

    private void invokeBuses(Object arg, BusInfo busInfo, Set<Object> buses) {
        try {
            if (arg == NULL) {
                for (Object bus : buses) {
                    busInfo.method.invoke(bus, new Object[0]);
                }
                return;
            }
            for (Object bus2 : buses) {
                busInfo.method.invoke(bus2, arg);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        }
    }

    private void postStickyInner(String tag, Object arg) {
        List<BusInfo> busInfoList = this.mTag_BusInfoListMap.get(tag);
        if (busInfoList == null) {
            Log.e(TAG, "The bus of tag <" + tag + "> is not exists.");
            return;
        }
        for (BusInfo busInfo : busInfoList) {
            if (!busInfo.sticky) {
                invokeBus(arg, busInfo, false);
            } else {
                synchronized (this.mClassName_Tag_Arg4StickyMap) {
                    Map<String, Object> tagArgMap = this.mClassName_Tag_Arg4StickyMap.get(busInfo.className);
                    if (tagArgMap == null) {
                        tagArgMap = new ConcurrentHashMap();
                        this.mClassName_Tag_Arg4StickyMap.put(busInfo.className, tagArgMap);
                    }
                    tagArgMap.put(tag, arg);
                }
                invokeBus(arg, busInfo, true);
            }
        }
    }

    private void removeStickyInner(String tag) {
        List<BusInfo> busInfoList = this.mTag_BusInfoListMap.get(tag);
        if (busInfoList == null) {
            Log.e(TAG, "The bus of tag <" + tag + "> is not exists.");
            return;
        }
        for (BusInfo busInfo : busInfoList) {
            if (busInfo.sticky) {
                synchronized (this.mClassName_Tag_Arg4StickyMap) {
                    Map<String, Object> tagArgMap = this.mClassName_Tag_Arg4StickyMap.get(busInfo.className);
                    if (tagArgMap != null && tagArgMap.containsKey(tag)) {
                        tagArgMap.remove(tag);
                    }
                    return;
                }
            }
        }
    }

    static void registerBus4Test(String tag, String className, String funName, String paramType, String paramName, boolean sticky, String threadMode, int priority) {
        getInstance().registerBus(tag, className, funName, paramType, paramName, sticky, threadMode, priority);
    }

    private static final class BusInfo {
        String className;
        String funName;
        Method method;
        String paramName;
        String paramType;
        int priority;
        boolean sticky;
        List<String> subClassNames = new CopyOnWriteArrayList();
        String tag;
        String threadMode;

        BusInfo(String tag, String className, String funName, String paramType, String paramName, boolean sticky, String threadMode, int priority) {
            this.tag = tag;
            this.className = className;
            this.funName = funName;
            this.paramType = paramType;
            this.paramName = paramName;
            this.sticky = sticky;
            this.threadMode = threadMode;
            this.priority = priority;
        }

        public String toString() {
            return "BusInfo { tag : " + this.tag + ", desc: " + getDesc() + ", sticky: " + this.sticky + ", threadMode: " + this.threadMode + ", method: " + this.method + ", priority: " + this.priority + " }";
        }

        private String getDesc() {
            return this.className + "#" + this.funName + ("".equals(this.paramType) ? "()" : "(" + this.paramType + " " + this.paramName + ")");
        }
    }

    private static class LazyHolder {
        private static final BusUtils INSTANCE = new BusUtils();

        private LazyHolder() {
        }
    }
}

package com.blankj.utilcode.util;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
public final class FragmentUtils {
    private static final String ARGS_ID = "args_id";
    private static final String ARGS_IS_ADD_STACK = "args_is_add_stack";
    private static final String ARGS_IS_HIDE = "args_is_hide";
    private static final String ARGS_TAG = "args_tag";
    private static final int TYPE_ADD_FRAGMENT = 1;
    private static final int TYPE_HIDE_FRAGMENT = 4;
    private static final int TYPE_REMOVE_FRAGMENT = 32;
    private static final int TYPE_REMOVE_TO_FRAGMENT = 64;
    private static final int TYPE_REPLACE_FRAGMENT = 16;
    private static final int TYPE_SHOW_FRAGMENT = 2;
    private static final int TYPE_SHOW_HIDE_FRAGMENT = 8;

    public interface OnBackClickListener {
        boolean onBackClick();
    }

    private FragmentUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void add(FragmentManager fm, Fragment add, int containerId) {
        add(fm, add, containerId, (String) null, false, false);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, boolean isHide) {
        add(fm, add, containerId, (String) null, isHide, false);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, boolean isHide, boolean isAddStack) {
        add(fm, add, containerId, (String) null, isHide, isAddStack);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, int enterAnim, int exitAnim) {
        add(fm, add, containerId, null, false, enterAnim, exitAnim, 0, 0);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, boolean isAddStack, int enterAnim, int exitAnim) {
        add(fm, add, containerId, null, isAddStack, enterAnim, exitAnim, 0, 0);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        add(fm, add, containerId, null, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        add(fm, add, containerId, null, isAddStack, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, View... sharedElements) {
        add(fm, add, containerId, (String) null, false, sharedElements);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, boolean isAddStack, View... sharedElements) {
        add(fm, add, containerId, (String) null, isAddStack, sharedElements);
    }

    public static void add(FragmentManager fm, List<Fragment> adds, int containerId, int showIndex) {
        add(fm, (Fragment[]) adds.toArray(new Fragment[0]), containerId, (String[]) null, showIndex);
    }

    public static void add(FragmentManager fm, Fragment[] adds, int containerId, int showIndex) {
        add(fm, adds, containerId, (String[]) null, showIndex);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag) {
        add(fm, add, containerId, tag, false, false);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, boolean isHide) {
        add(fm, add, containerId, tag, isHide, false);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, boolean isHide, boolean isAddStack) {
        putArgs(add, new Args(containerId, tag, isHide, isAddStack));
        operateNoAnim(1, fm, null, add);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, int enterAnim, int exitAnim) {
        add(fm, add, containerId, tag, false, enterAnim, exitAnim, 0, 0);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, boolean isAddStack, int enterAnim, int exitAnim) {
        add(fm, add, containerId, tag, isAddStack, enterAnim, exitAnim, 0, 0);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        add(fm, add, containerId, tag, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        FragmentTransaction ft = fm.beginTransaction();
        putArgs(add, new Args(containerId, tag, false, isAddStack));
        addAnim(ft, enterAnim, exitAnim, popEnterAnim, popExitAnim);
        operate(1, fm, ft, null, add);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, View... sharedElements) {
        add(fm, add, containerId, tag, false, sharedElements);
    }

    public static void add(FragmentManager fm, Fragment add, int containerId, String tag, boolean isAddStack, View... sharedElements) {
        FragmentTransaction ft = fm.beginTransaction();
        putArgs(add, new Args(containerId, tag, false, isAddStack));
        addSharedElement(ft, sharedElements);
        operate(1, fm, ft, null, add);
    }

    public static void add(FragmentManager fm, List<Fragment> adds, int containerId, String[] tags, int showIndex) {
        add(fm, (Fragment[]) adds.toArray(new Fragment[0]), containerId, tags, showIndex);
    }

    public static void add(FragmentManager fm, Fragment[] adds, int containerId, String[] tags, int showIndex) {
        if (tags == null) {
            int i = 0;
            int len = adds.length;
            while (i < len) {
                putArgs(adds[i], new Args(containerId, null, showIndex != i, false));
                i++;
            }
        } else {
            int i2 = 0;
            int len2 = adds.length;
            while (i2 < len2) {
                putArgs(adds[i2], new Args(containerId, tags[i2], showIndex != i2, false));
                i2++;
            }
        }
        operateNoAnim(1, fm, null, adds);
    }

    public static void show(Fragment show) {
        putArgs(show, false);
        operateNoAnim(2, show.getFragmentManager(), null, show);
    }

    public static void show(FragmentManager fm) {
        List<Fragment> fragments = getFragments(fm);
        for (Fragment show : fragments) {
            putArgs(show, false);
        }
        operateNoAnim(2, fm, null, (Fragment[]) fragments.toArray(new Fragment[0]));
    }

    public static void hide(Fragment hide) {
        putArgs(hide, true);
        operateNoAnim(4, hide.getFragmentManager(), null, hide);
    }

    public static void hide(FragmentManager fm) {
        List<Fragment> fragments = getFragments(fm);
        for (Fragment hide : fragments) {
            putArgs(hide, true);
        }
        operateNoAnim(4, fm, null, (Fragment[]) fragments.toArray(new Fragment[0]));
    }

    public static void showHide(Fragment show, Fragment hide) {
        showHide(show, (List<Fragment>) Collections.singletonList(hide));
    }

    public static void showHide(int showIndex, Fragment... fragments) {
        showHide(fragments[showIndex], fragments);
    }

    public static void showHide(Fragment show, Fragment... hide) {
        showHide(show, (List<Fragment>) Arrays.asList(hide));
    }

    public static void showHide(int showIndex, List<Fragment> fragments) {
        showHide(fragments.get(showIndex), fragments);
    }

    public static void showHide(Fragment show, List<Fragment> hide) {
        Iterator<Fragment> it = hide.iterator();
        while (true) {
            boolean z = false;
            if (it.hasNext()) {
                Fragment fragment = it.next();
                if (fragment != show) {
                    z = true;
                }
                putArgs(fragment, z);
            } else {
                operateNoAnim(8, show.getFragmentManager(), show, (Fragment[]) hide.toArray(new Fragment[0]));
                return;
            }
        }
    }

    public static void showHide(Fragment show, Fragment hide, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        showHide(show, (List<Fragment>) Collections.singletonList(hide), enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void showHide(int showIndex, List<Fragment> fragments, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        showHide(fragments.get(showIndex), fragments, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void showHide(Fragment show, List<Fragment> hide, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        Iterator<Fragment> it = hide.iterator();
        while (true) {
            boolean z = false;
            if (!it.hasNext()) {
                break;
            }
            Fragment fragment = it.next();
            if (fragment != show) {
                z = true;
            }
            putArgs(fragment, z);
        }
        FragmentManager fm = show.getFragmentManager();
        if (fm != null) {
            FragmentTransaction ft = fm.beginTransaction();
            addAnim(ft, enterAnim, exitAnim, popEnterAnim, popExitAnim);
            operate(8, fm, ft, show, (Fragment[]) hide.toArray(new Fragment[0]));
        }
    }

    public static void replace(Fragment srcFragment, Fragment destFragment) {
        replace(srcFragment, destFragment, (String) null, false);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, boolean isAddStack) {
        replace(srcFragment, destFragment, (String) null, isAddStack);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, int enterAnim, int exitAnim) {
        replace(srcFragment, destFragment, (String) null, false, enterAnim, exitAnim, 0, 0);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, boolean isAddStack, int enterAnim, int exitAnim) {
        replace(srcFragment, destFragment, (String) null, isAddStack, enterAnim, exitAnim, 0, 0);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        replace(srcFragment, destFragment, (String) null, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        replace(srcFragment, destFragment, (String) null, isAddStack, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, View... sharedElements) {
        replace(srcFragment, destFragment, (String) null, false, sharedElements);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, boolean isAddStack, View... sharedElements) {
        replace(srcFragment, destFragment, (String) null, isAddStack, sharedElements);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId) {
        replace(fm, fragment, containerId, (String) null, false);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, boolean isAddStack) {
        replace(fm, fragment, containerId, (String) null, isAddStack);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, int enterAnim, int exitAnim) {
        replace(fm, fragment, containerId, null, false, enterAnim, exitAnim, 0, 0);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, boolean isAddStack, int enterAnim, int exitAnim) {
        replace(fm, fragment, containerId, null, isAddStack, enterAnim, exitAnim, 0, 0);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        replace(fm, fragment, containerId, null, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        replace(fm, fragment, containerId, null, isAddStack, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, View... sharedElements) {
        replace(fm, fragment, containerId, (String) null, false, sharedElements);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, boolean isAddStack, View... sharedElements) {
        replace(fm, fragment, containerId, (String) null, isAddStack, sharedElements);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag) {
        replace(srcFragment, destFragment, destTag, false);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, boolean isAddStack) {
        FragmentManager fm = srcFragment.getFragmentManager();
        if (fm == null) {
            return;
        }
        Args args = getArgs(srcFragment);
        replace(fm, destFragment, args.id, destTag, isAddStack);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, int enterAnim, int exitAnim) {
        replace(srcFragment, destFragment, destTag, false, enterAnim, exitAnim, 0, 0);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, boolean isAddStack, int enterAnim, int exitAnim) {
        replace(srcFragment, destFragment, destTag, isAddStack, enterAnim, exitAnim, 0, 0);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        replace(srcFragment, destFragment, destTag, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        FragmentManager fm = srcFragment.getFragmentManager();
        if (fm == null) {
            return;
        }
        Args args = getArgs(srcFragment);
        replace(fm, destFragment, args.id, destTag, isAddStack, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, View... sharedElements) {
        replace(srcFragment, destFragment, destTag, false, sharedElements);
    }

    public static void replace(Fragment srcFragment, Fragment destFragment, String destTag, boolean isAddStack, View... sharedElements) {
        FragmentManager fm = srcFragment.getFragmentManager();
        if (fm == null) {
            return;
        }
        Args args = getArgs(srcFragment);
        replace(fm, destFragment, args.id, destTag, isAddStack, sharedElements);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag) {
        replace(fm, fragment, containerId, destTag, false);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, boolean isAddStack) {
        FragmentTransaction ft = fm.beginTransaction();
        putArgs(fragment, new Args(containerId, destTag, false, isAddStack));
        operate(16, fm, ft, null, fragment);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, int enterAnim, int exitAnim) {
        replace(fm, fragment, containerId, destTag, false, enterAnim, exitAnim, 0, 0);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, boolean isAddStack, int enterAnim, int exitAnim) {
        replace(fm, fragment, containerId, destTag, isAddStack, enterAnim, exitAnim, 0, 0);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        replace(fm, fragment, containerId, destTag, false, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, boolean isAddStack, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        FragmentTransaction ft = fm.beginTransaction();
        putArgs(fragment, new Args(containerId, destTag, false, isAddStack));
        addAnim(ft, enterAnim, exitAnim, popEnterAnim, popExitAnim);
        operate(16, fm, ft, null, fragment);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, View... sharedElements) {
        replace(fm, fragment, containerId, destTag, false, sharedElements);
    }

    public static void replace(FragmentManager fm, Fragment fragment, int containerId, String destTag, boolean isAddStack, View... sharedElements) {
        FragmentTransaction ft = fm.beginTransaction();
        putArgs(fragment, new Args(containerId, destTag, false, isAddStack));
        addSharedElement(ft, sharedElements);
        operate(16, fm, ft, null, fragment);
    }

    public static void pop(FragmentManager fm) {
        pop(fm, true);
    }

    public static void pop(FragmentManager fm, boolean isImmediate) {
        if (isImmediate) {
            fm.popBackStackImmediate();
        } else {
            fm.popBackStack();
        }
    }

    public static void popTo(FragmentManager fm, Class<? extends Fragment> popClz, boolean isIncludeSelf) {
        popTo(fm, popClz, isIncludeSelf, true);
    }

    public static void popTo(FragmentManager fragmentManager, Class<? extends Fragment> cls, boolean z, boolean z2) {
        if (z2) {
            fragmentManager.popBackStackImmediate(cls.getName(), z ? 1 : 0);
        } else {
            fragmentManager.popBackStack(cls.getName(), z ? 1 : 0);
        }
    }

    public static void popAll(FragmentManager fm) {
        popAll(fm, true);
    }

    public static void popAll(FragmentManager fm, boolean isImmediate) {
        if (fm.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(0);
            if (isImmediate) {
                fm.popBackStackImmediate(entry.getId(), 1);
            } else {
                fm.popBackStack(entry.getId(), 1);
            }
        }
    }

    public static void remove(Fragment remove) {
        operateNoAnim(32, remove.getFragmentManager(), null, remove);
    }

    public static void removeTo(Fragment removeTo, boolean isIncludeSelf) {
        operateNoAnim(64, removeTo.getFragmentManager(), isIncludeSelf ? removeTo : null, removeTo);
    }

    public static void removeAll(FragmentManager fm) {
        List<Fragment> fragments = getFragments(fm);
        operateNoAnim(32, fm, null, (Fragment[]) fragments.toArray(new Fragment[0]));
    }

    private static void putArgs(Fragment fragment, Args args) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            fragment.setArguments(bundle);
        }
        bundle.putInt(ARGS_ID, args.id);
        bundle.putBoolean(ARGS_IS_HIDE, args.isHide);
        bundle.putBoolean(ARGS_IS_ADD_STACK, args.isAddStack);
        bundle.putString(ARGS_TAG, args.tag);
    }

    private static void putArgs(Fragment fragment, boolean isHide) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            fragment.setArguments(bundle);
        }
        bundle.putBoolean(ARGS_IS_HIDE, isHide);
    }

    private static Args getArgs(Fragment fragment) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = Bundle.EMPTY;
        }
        return new Args(bundle.getInt(ARGS_ID, fragment.getId()), bundle.getBoolean(ARGS_IS_HIDE), bundle.getBoolean(ARGS_IS_ADD_STACK));
    }

    private static void operateNoAnim(int type, FragmentManager fm, Fragment src, Fragment... dest) {
        if (fm == null) {
            return;
        }
        FragmentTransaction ft = fm.beginTransaction();
        operate(type, fm, ft, src, dest);
    }

    private static void operate(int type, FragmentManager fm, FragmentTransaction ft, Fragment src, Fragment... dest) {
        if (src != null && src.isRemoving()) {
            Log.e("FragmentUtils", src.getClass().getName() + " is isRemoving");
            return;
        }
        int i = 0;
        switch (type) {
            case 1:
                int length = dest.length;
                while (i < length) {
                    Fragment fragment = dest[i];
                    Bundle args = fragment.getArguments();
                    if (args == null) {
                        return;
                    }
                    String name = args.getString(ARGS_TAG, fragment.getClass().getName());
                    Fragment fragmentByTag = fm.findFragmentByTag(name);
                    if (fragmentByTag != null && fragmentByTag.isAdded()) {
                        ft.remove(fragmentByTag);
                    }
                    ft.add(args.getInt(ARGS_ID), fragment, name);
                    if (args.getBoolean(ARGS_IS_HIDE)) {
                        ft.hide(fragment);
                    }
                    if (args.getBoolean(ARGS_IS_ADD_STACK)) {
                        ft.addToBackStack(name);
                    }
                    i++;
                }
                break;
            case 2:
                int length2 = dest.length;
                while (i < length2) {
                    ft.show(dest[i]);
                    i++;
                }
                break;
            case 4:
                int length3 = dest.length;
                while (i < length3) {
                    ft.hide(dest[i]);
                    i++;
                }
                break;
            case 8:
                ft.show(src);
                int length4 = dest.length;
                while (i < length4) {
                    Fragment fragment2 = dest[i];
                    if (fragment2 != src) {
                        ft.hide(fragment2);
                    }
                    i++;
                }
                break;
            case 16:
                Bundle args2 = dest[0].getArguments();
                if (args2 == null) {
                    return;
                }
                String name2 = args2.getString(ARGS_TAG, dest[0].getClass().getName());
                ft.replace(args2.getInt(ARGS_ID), dest[0], name2);
                if (args2.getBoolean(ARGS_IS_ADD_STACK)) {
                    ft.addToBackStack(name2);
                }
                break;
            case 32:
                int i2 = dest.length;
                while (i < i2) {
                    Fragment fragment3 = dest[i];
                    if (fragment3 != src) {
                        ft.remove(fragment3);
                    }
                    i++;
                }
                break;
            case 64:
                int i3 = dest.length - 1;
                while (true) {
                    if (i3 >= 0) {
                        Fragment fragment4 = dest[i3];
                        if (fragment4 == dest[0]) {
                            if (src != null) {
                                ft.remove(fragment4);
                            }
                        } else {
                            ft.remove(fragment4);
                            i3--;
                        }
                    }
                    break;
                }
                break;
        }
        ft.commitAllowingStateLoss();
        fm.executePendingTransactions();
    }

    private static void addAnim(FragmentTransaction ft, int enter, int exit, int popEnter, int popExit) {
        ft.setCustomAnimations(enter, exit, popEnter, popExit);
    }

    private static void addSharedElement(FragmentTransaction ft, View... views) {
        for (View view : views) {
            ft.addSharedElement(view, view.getTransitionName());
        }
    }

    public static Fragment getTop(FragmentManager fm) {
        return getTopIsInStack(fm, null, false);
    }

    public static Fragment getTopInStack(FragmentManager fm) {
        return getTopIsInStack(fm, null, true);
    }

    private static Fragment getTopIsInStack(FragmentManager fm, Fragment parentFragment, boolean isInStack) {
        List<Fragment> fragments = getFragments(fm);
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment != null) {
                if (isInStack) {
                    Bundle args = fragment.getArguments();
                    if (args != null && args.getBoolean(ARGS_IS_ADD_STACK)) {
                        return getTopIsInStack(fragment.getChildFragmentManager(), fragment, true);
                    }
                } else {
                    return getTopIsInStack(fragment.getChildFragmentManager(), fragment, false);
                }
            }
        }
        return parentFragment;
    }

    public static Fragment getTopShow(FragmentManager fm) {
        return getTopShowIsInStack(fm, null, false);
    }

    public static Fragment getTopShowInStack(FragmentManager fm) {
        return getTopShowIsInStack(fm, null, true);
    }

    private static Fragment getTopShowIsInStack(FragmentManager fm, Fragment parentFragment, boolean isInStack) {
        List<Fragment> fragments = getFragments(fm);
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment != null && fragment.isResumed() && fragment.isVisible() && fragment.getUserVisibleHint()) {
                if (isInStack) {
                    Bundle args = fragment.getArguments();
                    if (args != null && args.getBoolean(ARGS_IS_ADD_STACK)) {
                        return getTopShowIsInStack(fragment.getChildFragmentManager(), fragment, true);
                    }
                } else {
                    return getTopShowIsInStack(fragment.getChildFragmentManager(), fragment, false);
                }
            }
        }
        return parentFragment;
    }

    public static List<Fragment> getFragments(FragmentManager fm) {
        List<Fragment> fragments = fm.getFragments();
        if (fragments == null || fragments.isEmpty()) {
            return Collections.emptyList();
        }
        return fragments;
    }

    public static List<Fragment> getFragmentsInStack(FragmentManager fm) {
        Bundle args;
        List<Fragment> fragments = getFragments(fm);
        List<Fragment> result = new ArrayList<>();
        for (Fragment fragment : fragments) {
            if (fragment != null && (args = fragment.getArguments()) != null && args.getBoolean(ARGS_IS_ADD_STACK)) {
                result.add(fragment);
            }
        }
        return result;
    }

    public static List<FragmentNode> getAllFragments(FragmentManager fm) {
        return getAllFragments(fm, new ArrayList());
    }

    private static List<FragmentNode> getAllFragments(FragmentManager fm, List<FragmentNode> result) {
        List<Fragment> fragments = getFragments(fm);
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment != null) {
                result.add(new FragmentNode(fragment, getAllFragments(fragment.getChildFragmentManager(), new ArrayList())));
            }
        }
        return result;
    }

    public static List<FragmentNode> getAllFragmentsInStack(FragmentManager fm) {
        return getAllFragmentsInStack(fm, new ArrayList());
    }

    private static List<FragmentNode> getAllFragmentsInStack(FragmentManager fm, List<FragmentNode> result) {
        Bundle args;
        List<Fragment> fragments = getFragments(fm);
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment != null && (args = fragment.getArguments()) != null && args.getBoolean(ARGS_IS_ADD_STACK)) {
                result.add(new FragmentNode(fragment, getAllFragmentsInStack(fragment.getChildFragmentManager(), new ArrayList())));
            }
        }
        return result;
    }

    public static Fragment findFragment(FragmentManager fm, Class<? extends Fragment> findClz) {
        return fm.findFragmentByTag(findClz.getName());
    }

    public static Fragment findFragment(FragmentManager fm, String tag) {
        return fm.findFragmentByTag(tag);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static boolean dispatchBackPress(Fragment fragment) {
        return fragment.isResumed() && fragment.isVisible() && fragment.getUserVisibleHint() && (fragment instanceof OnBackClickListener) && ((OnBackClickListener) fragment).onBackClick();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static boolean dispatchBackPress(FragmentManager fm) {
        List<Fragment> fragments = getFragments(fm);
        if (fragments == null || fragments.isEmpty()) {
            return false;
        }
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment != 0 && fragment.isResumed() && fragment.isVisible() && fragment.getUserVisibleHint() && (fragment instanceof OnBackClickListener) && ((OnBackClickListener) fragment).onBackClick()) {
                return true;
            }
        }
        return false;
    }

    public static void setBackgroundColor(Fragment fragment, int color) {
        View view = fragment.getView();
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }

    public static void setBackgroundResource(Fragment fragment, int resId) {
        View view = fragment.getView();
        if (view != null) {
            view.setBackgroundResource(resId);
        }
    }

    public static void setBackground(Fragment fragment, Drawable background) {
        View view = fragment.getView();
        if (view == null) {
            return;
        }
        view.setBackground(background);
    }

    public static String getSimpleName(Fragment fragment) {
        return fragment == null ? "null" : fragment.getClass().getSimpleName();
    }

    private static class Args {
        final int id;
        final boolean isAddStack;
        final boolean isHide;
        final String tag;

        Args(int id, boolean isHide, boolean isAddStack) {
            this(id, null, isHide, isAddStack);
        }

        Args(int id, String tag, boolean isHide, boolean isAddStack) {
            this.id = id;
            this.tag = tag;
            this.isHide = isHide;
            this.isAddStack = isAddStack;
        }
    }

    public static class FragmentNode {
        final Fragment fragment;
        final List<FragmentNode> next;

        public FragmentNode(Fragment fragment, List<FragmentNode> next) {
            this.fragment = fragment;
            this.next = next;
        }

        public Fragment getFragment() {
            return this.fragment;
        }

        public List<FragmentNode> getNext() {
            return this.next;
        }

        public String toString() {
            StringBuilder sbAppend = new StringBuilder().append(this.fragment.getClass().getSimpleName()).append("->");
            List<FragmentNode> list = this.next;
            return sbAppend.append((list == null || list.isEmpty()) ? "no child" : this.next.toString()).toString();
        }
    }
}

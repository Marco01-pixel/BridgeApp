package androidx.transition;

import android.R;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
class Styleable {
    static final int[] TRANSITION_TARGET = {R.attr.targetClass, R.attr.targetId, R.attr.excludeId, R.attr.excludeClass, R.attr.targetName, R.attr.excludeName};
    static final int[] TRANSITION_MANAGER = {R.attr.fromScene, R.attr.toScene, R.attr.transition};
    static final int[] TRANSITION = {R.attr.interpolator, R.attr.duration, R.attr.startDelay, R.attr.matchOrder};
    static final int[] CHANGE_BOUNDS = {R.attr.resizeClip};
    static final int[] VISIBILITY_TRANSITION = {R.attr.transitionVisibilityMode};
    static final int[] FADE = {R.attr.fadingMode};
    static final int[] CHANGE_TRANSFORM = {R.attr.reparent, R.attr.reparentWithOverlay};
    static final int[] SLIDE = {R.attr.slideEdge};
    static final int[] TRANSITION_SET = {R.attr.transitionOrdering};
    static final int[] ARC_MOTION = {R.attr.minimumHorizontalAngle, R.attr.minimumVerticalAngle, R.attr.maximumAngle};
    static final int[] PATTERN_PATH_MOTION = {R.attr.patternPathData};

    interface ArcMotion {
        public static final int MAXIMUM_ANGLE = 2;
        public static final int MINIMUM_HORIZONTAL_ANGLE = 0;
        public static final int MINIMUM_VERTICAL_ANGLE = 1;
    }

    interface ChangeBounds {
        public static final int RESIZE_CLIP = 0;
    }

    interface ChangeTransform {
        public static final int REPARENT = 0;
        public static final int REPARENT_WITH_OVERLAY = 1;
    }

    interface Fade {
        public static final int FADING_MODE = 0;
    }

    interface PatternPathMotion {
        public static final int PATTERN_PATH_DATA = 0;
    }

    interface Slide {
        public static final int SLIDE_EDGE = 0;
    }

    interface Transition {
        public static final int DURATION = 1;
        public static final int INTERPOLATOR = 0;
        public static final int MATCH_ORDER = 3;
        public static final int START_DELAY = 2;
    }

    interface TransitionManager {
        public static final int FROM_SCENE = 0;
        public static final int TO_SCENE = 1;
        public static final int TRANSITION = 2;
    }

    interface TransitionSet {
        public static final int TRANSITION_ORDERING = 0;
    }

    interface TransitionTarget {
        public static final int EXCLUDE_CLASS = 3;
        public static final int EXCLUDE_ID = 2;
        public static final int EXCLUDE_NAME = 5;
        public static final int TARGET_CLASS = 0;
        public static final int TARGET_ID = 1;
        public static final int TARGET_NAME = 4;
    }

    interface VisibilityTransition {
        public static final int TRANSITION_VISIBILITY_MODE = 0;
    }

    private Styleable() {
    }
}

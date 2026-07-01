package com.hcdev.skin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public abstract class SkinAttr {
    public static final String ATTR_NAME_BACKGROUND = "background";
    public static final String ATTR_NAME_TEXT_COLOR = "textColor";
    public static final String ATTR_NAME_SRC = "src";
    public static final String ATTR_NAME_DRAWABLE_LEFT = "drawableLeft";
    public static final String ATTR_NAME_DRAWABLE_RIGHT = "drawableRight";
    public static final String ATTR_NAME_DRAWABLE_TOP = "drawableTop";
    public static final String ATTR_NAME_DRAWABLE_BOTTOM = "drawableBottom";
    public static final String ATTR_NAME_DRAWABLE_START = "drawableStart";
    public static final String ATTR_NAME_DRAWABLE_END = "drawableEnd";
    public static final String ATTR_NAME_BUTTON = "button";
    public static final String ATTR_NAME_FILTER_COLOR = "filterColor";
    public static final String ATTR_NAME_FILTER_COLOR_COMPOUND = "filterColorCompound";

    protected final int attrResId;
    protected final boolean isAttrRef;

    protected SkinAttr(int attrResId, boolean isAttrRef) {
        this.attrResId = attrResId;
        this.isAttrRef = isAttrRef;
    }

    /** 根据 attrName 创建对应子类，attrName 不支持时返回 null */
    @Nullable
    public static SkinAttr create(@NonNull String attrName, int resId, boolean isAttrRef) {
        switch (attrName) {
            case ATTR_NAME_FILTER_COLOR:          return new FilterColorSkinAttr(resId, isAttrRef);
            case ATTR_NAME_FILTER_COLOR_COMPOUND: return new FilterColorCompoundSkinAttr(resId, isAttrRef);
            case ATTR_NAME_BUTTON:                return new ButtonSkinAttr(resId, isAttrRef);
            case ATTR_NAME_DRAWABLE_LEFT:
            case ATTR_NAME_DRAWABLE_RIGHT:
            case ATTR_NAME_DRAWABLE_TOP:
            case ATTR_NAME_DRAWABLE_BOTTOM:
            case ATTR_NAME_DRAWABLE_START:
            case ATTR_NAME_DRAWABLE_END:          return new CompoundDrawableSkinAttr(attrName, resId, isAttrRef);
            case ATTR_NAME_BACKGROUND:            return new BackgroundSkinAttr(resId, isAttrRef);
            case ATTR_NAME_TEXT_COLOR:            return new TextColorSkinAttr(resId, isAttrRef);
            case ATTR_NAME_SRC:                   return new SrcSkinAttr(resId, isAttrRef);
            default:                              return null;
        }
    }

    public final void apply(@NonNull View view) {
        int themeResId = SkinManager.getInstance().getCurrentThemeResId();
        Context themeContext = themeResId != 0
                ? new ContextThemeWrapper(view.getContext(), themeResId)
                : view.getContext();
        //noinspection ConstantConditions, 上面View.getContext返回的Context对象一定非空
        applyWithTheme(view, themeContext);
    }

    protected abstract void applyWithTheme(@NonNull View view, @NonNull Context themeContext);

    // 该方法暂时没有被使用, 都改为使用resolveColorStateList, 但保留以备后续需要只获取颜色值的场景
    protected int resolveColor(@NonNull Context themeContext) {
        if (isAttrRef) {
            TypedValue tv = resolveAttr(themeContext);
            if (tv == null) return 0;
            if (isColor(tv)) return tv.data;
            if (tv.resourceId == 0) return 0;
            //noinspection ConstantConditions, context.getResources返回的Resource对象一定非空
            ColorStateList csl = ResourcesCompat.getColorStateList(
                    themeContext.getResources(), tv.resourceId, themeContext.getTheme());
            return csl != null ? csl.getDefaultColor() : 0;
        } else {
            //noinspection ConstantConditions, context.getResources返回的Resource对象一定非空
            ColorStateList csl = ResourcesCompat.getColorStateList(
                    themeContext.getResources(), attrResId, themeContext.getTheme());
            return csl != null ? csl.getDefaultColor() : 0;
        }
    }

    private int resolveResourceId(@NonNull Context themeContext) {
        if (!isAttrRef) return attrResId;
        TypedValue tv = resolveAttr(themeContext);
        return tv != null ? tv.resourceId : 0;
    }

    @Nullable
    private TypedValue resolveAttr(@NonNull Context themeContext) {
        TypedValue tv = new TypedValue();
        //noinspection ConstantConditions, context.getTheme返回的Theme对象一定非空
        return themeContext.getTheme().resolveAttribute(attrResId, tv, true) ? tv : null;
    }

    @Nullable
    protected ColorStateList resolveColorStateList(@NonNull Context themeContext) {
        if (isAttrRef) {
            TypedValue tv = resolveAttr(themeContext);
            if (tv == null) return null;
            if (tv.resourceId != 0) {
                //noinspection ConstantConditions, context.getResources返回的Resource对象一定非空
                return ResourcesCompat.getColorStateList(themeContext.getResources(), tv.resourceId, themeContext.getTheme());
            }
            if (isColor(tv)) return ColorStateList.valueOf(tv.data);
            return null;
        }
        //noinspection ConstantConditions, context.getResources返回的Resource对象一定非空
        return ResourcesCompat.getColorStateList(themeContext.getResources(), attrResId, themeContext.getTheme());
    }

    @Nullable
    protected Drawable resolveDrawable(@NonNull Context themeContext) {
        int resId = resolveResourceId(themeContext);
        if (resId == 0) return null;
        //noinspection ConstantConditions, context.getResources返回的Resource对象一定非空
        return ResourcesCompat.getDrawable(themeContext.getResources(), resId, themeContext.getTheme());
    }

    protected static boolean isColor(@NonNull TypedValue tv) {
        return tv.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && tv.type <= TypedValue.TYPE_LAST_COLOR_INT;
    }
}

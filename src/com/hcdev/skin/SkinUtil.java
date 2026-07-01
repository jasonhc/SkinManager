package com.hcdev.skin;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.util.Arrays;
import java.util.List;

/**
 * @author hechuan1 on 2026/4/16.
 */
public class SkinUtil {

    public static int getAttrColor(@NonNull Context context, int attrResId) {
        TypedValue typedValue = new TypedValue();
        ContextThemeWrapper themeContext = new ContextThemeWrapper(context, SkinManager.getInstance().getCurrentThemeResId());
        //noinspection ConstantConditions
        boolean resolved = themeContext.getTheme().resolveAttribute(attrResId, typedValue, true);
        if (resolved) {
            if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return typedValue.data;
            } else if (typedValue.resourceId != 0) { // TODO: 是否需要处理这种情况
                try {
                    //noinspection ConstantConditions, context.getResources返回的Resource对象一定非空
                    return ResourcesCompat.getColor(themeContext.getResources(), typedValue.resourceId, themeContext.getTheme());
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return 0xFFFF0000; // 默认返回红色，调用方可根据需要设置默认值
    }

    public static int getAttrDrawableResId(@NonNull Context context, int attrResId) {
        TypedValue typedValue = new TypedValue();
        ContextThemeWrapper themeContext = new ContextThemeWrapper(context, SkinManager.getInstance().getCurrentThemeResId());
        //noinspection ConstantConditions, context.getTheme返回的Theme对象一定非空
        boolean resolved = themeContext.getTheme().resolveAttribute(attrResId, typedValue, true);

        int resId = 0; // 默认返回 0，表示未找到资源
        if (resolved) {
            resId = typedValue.resourceId;
        }
        return resId;
    }

    /**
     * 为代码创建的 View 注册换肤属性，使其在皮肤切换时自动重新应用。
     *
     * <p>是 {@link SkinLayoutInflaterFactory#register(View, List)} 的快捷入口，
     * 调用方无需自行获取 factory 实例。用法示例：
     * <pre>
     *   // 单个属性
     *   SkinUtil.register(imageView,
     *       SkinAttr.create(SkinAttr.ATTR_NAME_FILTER_COLOR, R.color.my_filter_color, false));
     *
     *   // 多个属性
     *   SkinUtil.register(textView,
     *       SkinAttr.create(SkinAttr.ATTR_NAME_TEXT_COLOR,  R.attr.colorPrimary, true),
     *       SkinAttr.create(SkinAttr.ATTR_NAME_BACKGROUND,  R.attr.colorSurface,  true));
     * </pre>
     *
     * <p>前提：当前 Activity 必须通过 {@link SkinActivityDelegate#onPreCreate} 在
     * {@code super.onCreate()} 之前注入了 {@link SkinLayoutInflaterFactory}，否则
     * {@code getFactory2()} 取不到正确实例，注册将被静默忽略。
     *
     * @param view      需要换肤的 View
     * @param skinAttrs 一个或多个皮肤属性，通过 {@link SkinAttr#create} 构造
     */
    public static void register(@NonNull View view, @NonNull SkinAttr... skinAttrs) {
        if (skinAttrs.length == 0) return;
        SkinLayoutInflaterFactory factory = getSkinFactory(view);
        if (factory != null) factory.register(view, Arrays.asList(skinAttrs));
    }

    /**
     * 对指定 View 重新应用当前皮肤，通常在代码动态修改 View 属性后调用。
     *
     * <p>典型场景：在 Activity.onCreate() 之后通过 {@code setCompoundDrawables()} 更换了
     * TextView 的 drawable，而换肤框架在 inflate 时已为该 View 注册了 filterColorCompound
     * 等皮肤属性。若不重新应用，新 drawable 将缺少皮肤着色，直到下次切换皮肤才会恢复。
     * 调用此方法可立即触发一次皮肤应用，保持视觉一致性：
     * <pre>
     *   textView.setCompoundDrawables(newDrawable, null, null, null);
     *   SkinUtil.reapply(textView);
     * </pre>
     *
     * <p>实现原理：通过 {@link LayoutInflater#getFactory2()} 取得当前 Activity 的
     * {@link SkinLayoutInflaterFactory}（需在 {@link SkinActivityDelegate#onPreCreate} 中
     * 于 super.onCreate() 之前注入，以确保 AppCompat 未抢先设置 factory，从而
     * getFactory2() 能直接返回 SkinLayoutInflaterFactory 而非 FactoryMerger），
     * 再委托 {@link SkinLayoutInflaterFactory#applySkinTo(View)} 完成皮肤重放。
     *
     * <p>注意：若当前皮肤为默认皮肤（{@link SkinManager#isDefaultSkin()} 返回 true），
     * 此方法仍会执行重放；调用方如需跳过默认皮肤场景，可自行在外层加判断。
     *
     * @param view 需要重新应用皮肤的 View
     */
    public static void reapply(@NonNull View view) {
        SkinLayoutInflaterFactory factory = getSkinFactory(view);
        if (factory != null) factory.applySkinTo(view);
    }

    @Nullable
    private static SkinLayoutInflaterFactory getSkinFactory(@NonNull View view) {
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        //noinspection ConstantConditions, LayoutInflater.from返回的对象一定非空
        LayoutInflater.Factory2 f = inflater.getFactory2();
        if (f == null) {
            f = new SkinLayoutInflaterFactory();
            inflater.setFactory2(f);
        }
        return f instanceof SkinLayoutInflaterFactory ? (SkinLayoutInflaterFactory) f : null;
    }
}

package com.hcdev.skin;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.LayoutInflaterCompat;

/**
 * 换肤委托：封装 Activity 接入换肤框架所需的全部生命周期操作。
 * 适用于无法继承 SkinBaseActivity 的场景（如已有继承链）。
 *
 * 使用方式：
 *   1. onPreCreate(activity) — 在 super.onCreate() 之前调用
 *   2. onCreate()            — 在 super.onCreate() 之后调用
 *   3. onDestroy()           — 在 onDestroy() 中调用
 *
 * 若 Activity 需要在换肤时执行额外逻辑，可单独再向 SkinManager 注册一个 SkinObserver。
 */
public class SkinActivityDelegate implements SkinManager.SkinObserver {

    @Nullable
    private SkinLayoutInflaterFactory mSkinFactory;
    @Nullable
    private Activity mActivity;

    /**
     * 必须在宿主 Activity 的 super.onCreate() 之前调用。
     */
    public void onPreCreate(@NonNull Activity activity) {
        mActivity = activity;
        applyCurrentTheme(activity);
        injectSkinFactory(activity);
    }

    private void applyCurrentTheme(@NonNull Activity activity) {
        int themeResId = SkinManager.getInstance().getCurrentThemeResId();
        if (themeResId != 0) {
            activity.setTheme(themeResId);
        }
    }

    private void injectSkinFactory(@NonNull Activity activity) {
        mSkinFactory = new SkinLayoutInflaterFactory();
        //noinspection ConstantConditions, LayoutInflater.from返回的对象一定非空
        LayoutInflaterCompat.setFactory2(LayoutInflater.from(activity), mSkinFactory);
    }

    /**
     * 在宿主 Activity 的 super.onCreate() 之后调用。
     */
    public void onCreate() {
        SkinManager.getInstance().register(this);
    }

    /**
     * 在宿主 Activity 的 onDestroy() 中调用。
     */
    public void onDestroy() {
        // 取消注册，避免内存泄漏
        SkinManager.getInstance().unregister(this);
        if (mSkinFactory != null) {
            mSkinFactory.clean();
        }
    }

    @Override
    public void onSkinChanged() {
        // 1. 刷新所有收集到的 View 的属性
        if (mSkinFactory != null) {
            mSkinFactory.applySkin();
        }
        // 2. 更新 Window 背景（android:windowBackground 不属于任何 View，需单独处理）
        applyWindowBackground();
    }

    private void applyWindowBackground() {
        if (mActivity == null) return;

        int themeResId = SkinManager.getInstance().getCurrentThemeResId();
        if (themeResId == 0) return;

        ContextThemeWrapper themeContext = new ContextThemeWrapper(mActivity, themeResId);
        TypedValue typedValue = new TypedValue();
        //noinspection ConstantConditions, context.getTheme返回的Theme对象一定非空
        boolean resolved = themeContext.getTheme()
                .resolveAttribute(android.R.attr.windowBackground, typedValue, true);
        if (resolved && typedValue.resourceId != 0) {
            //noinspection ConstantConditions, context.getResources返回的Resource对象一定非空
            Drawable bg = ResourcesCompat.getDrawable(
                    mActivity.getResources(), typedValue.resourceId, themeContext.getTheme());
            if (bg != null) {
                Window window = mActivity.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(bg);
                }
            }
        }
    }

    /**
     * 处理一种特殊情况: 使用LayoutInflater.from(getWindow().getDecorView().getContext())创建的LayoutInflater中没有注入SkinLayoutInflaterFactory
     * 需要在有这种情况的Activity.onCreate中，调用该方法.
     */
    public void injectSkinFactoryDecorView(@NonNull View decorView) {
        if (mSkinFactory != null) {
            //noinspection ConstantConditions, LayoutInflater.from返回的对象一定非空
            LayoutInflaterCompat.setFactory2(LayoutInflater.from(decorView.getContext()), mSkinFactory);
        }
    }
}

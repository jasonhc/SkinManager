package com.hcdev.skin;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 封装一个 View 及其关联的换肤属性。
 */
public class SkinView {
    @NonNull
    private final WeakReference<View> viewRef;
    @NonNull
    private final List<SkinAttr> attrs;

    public SkinView(@NonNull View view, @NonNull List<SkinAttr> attrs) {
        viewRef = new WeakReference<>(view);
        this.attrs = attrs;
    }

    /**
     * 对该 View 应用当前皮肤属性。
     */
    public void apply() {
        View view = viewRef.get();
        if (view == null || attrs.isEmpty()) return;
        for (SkinAttr attr : attrs) {
            if (attr != null) {
                attr.apply(view);
            }
        }
    }

    public boolean isDead() {
        return viewRef.get() == null;
    }

    /** 返回被包装的 View，已被 GC 时返回 null。 */
    @Nullable
    public View getView() {
        return viewRef.get();
    }
}

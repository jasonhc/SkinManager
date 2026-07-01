package com.hcdev.skin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

class BackgroundSkinAttr extends SkinAttr {
    BackgroundSkinAttr(int attrResId, boolean isAttrRef) {
        super(attrResId, isAttrRef);
    }

    @Override
    protected void applyWithTheme(@NonNull View view, @NonNull Context themeContext) {
        TypedValue tv = resolveTypedValue(themeContext);
        if (tv != null) setViewBackgroundWithTheme(view, themeContext, tv);
    }

    @Nullable
    private TypedValue resolveTypedValue(@NonNull Context themeContext) {
        TypedValue tv = new TypedValue();
        if (isAttrRef) {
            //noinspection ConstantConditions, context.getTheme返回的Theme对象一定非空
            return themeContext.getTheme().resolveAttribute(attrResId, tv, true) ? tv : null;
        }
        //noinspection ConstantConditions, context.getResources返回的Resource对象一定非空
        themeContext.getResources().getValue(attrResId, tv, true);
        return tv;
    }

    private void setViewBackgroundWithTheme(@NonNull View view, @NonNull Context themeContext, @NonNull TypedValue tv) {
        if (isColor(tv)) {
            view.setBackgroundColor(tv.data);
        } else if (tv.resourceId != 0) {
            //noinspection ConstantConditions, context.getResources返回的Resource对象一定非空
            Drawable d = ResourcesCompat.getDrawable(themeContext.getResources(), tv.resourceId, themeContext.getTheme());
            if (d != null) view.setBackground(d);
        }
    }
}

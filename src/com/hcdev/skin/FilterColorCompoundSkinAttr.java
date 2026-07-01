package com.hcdev.skin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

class FilterColorCompoundSkinAttr extends SkinAttr {
    FilterColorCompoundSkinAttr(int attrResId, boolean isAttrRef) {
        super(attrResId, isAttrRef);
    }

    @Override
    protected void applyWithTheme(@NonNull View view, @NonNull Context themeContext) {
        if (!(view instanceof TextView)) return;
        ColorStateList csl = resolveColorStateList(themeContext);
        // getCompoundDrawables方法返回的Drawable数组一定非空
        Drawable[] drawables = ((TextView) view).getCompoundDrawables();
        //noinspection ConstantConditions
        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] == null) continue;
            //noinspection ConstantConditions
            drawables[i] = drawables[i].mutate();
            //noinspection ConstantConditions
            DrawableCompat.setTintList(drawables[i], csl);
            if (csl != null) {
                //noinspection ConstantConditions
                DrawableCompat.setTintMode(drawables[i], PorterDuff.Mode.SRC_IN);
            }
        }
        ((TextView) view).setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }
}

package com.hcdev.skin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

class SrcSkinAttr extends SkinAttr {
    SrcSkinAttr(int attrResId, boolean isAttrRef) {
        super(attrResId, isAttrRef);
    }

    @Override
    protected void applyWithTheme(@NonNull View view, @NonNull Context themeContext) {
        if (!(view instanceof ImageView)) return;
        Drawable d = resolveDrawable(themeContext);
        if (d != null) ((ImageView) view).setImageDrawable(d);
    }
}

package com.hcdev.skin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

class TextColorSkinAttr extends SkinAttr {
    TextColorSkinAttr(int attrResId, boolean isAttrRef) {
        super(attrResId, isAttrRef);
    }

    @Override
    protected void applyWithTheme(@NonNull View view, @NonNull Context themeContext) {
        if (!(view instanceof TextView)) return;
        ColorStateList csl = resolveColorStateList(themeContext);
        if (csl != null) ((TextView) view).setTextColor(csl);
    }
}

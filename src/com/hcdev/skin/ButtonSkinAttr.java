package com.hcdev.skin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

class ButtonSkinAttr extends SkinAttr {
    ButtonSkinAttr(int attrResId, boolean isAttrRef) {
        super(attrResId, isAttrRef);
    }

    @Override
    protected void applyWithTheme(@NonNull View view, @NonNull Context themeContext) {
        if (!(view instanceof CompoundButton)) return;
        Drawable drawable = resolveDrawable(themeContext);
        if (drawable != null) ((CompoundButton) view).setButtonDrawable(drawable);
    }
}

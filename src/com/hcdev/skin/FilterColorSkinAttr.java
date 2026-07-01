package com.hcdev.skin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class FilterColorSkinAttr extends SkinAttr {
    FilterColorSkinAttr(int attrResId, boolean isAttrRef) {
        super(attrResId, isAttrRef);
    }

    @Override
    protected void applyWithTheme(@NonNull View view, @NonNull Context themeContext) {
        ColorStateList csl = resolveColorStateList(themeContext);
        if (view instanceof ImageView) applyToImageView((ImageView) view, csl);
        else if (view instanceof ProgressBar) applyToProgressBar((ProgressBar) view, csl);
    }

    private void applyToImageView(@NonNull ImageView view, @Nullable ColorStateList csl) {
        view.setImageTintList(csl);
        if (csl != null) view.setImageTintMode(PorterDuff.Mode.SRC_IN);
    }

    private void applyToProgressBar(@NonNull ProgressBar view, @Nullable ColorStateList csl) {
        view.setIndeterminateTintList(csl);
        if (csl != null) view.setIndeterminateTintMode(PorterDuff.Mode.SRC_IN);
    }
}

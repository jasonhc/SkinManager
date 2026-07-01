package com.hcdev.skin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

class CompoundDrawableSkinAttr extends SkinAttr {
    @NonNull
    private final String direction;

    CompoundDrawableSkinAttr(@NonNull String direction, int attrResId, boolean isAttrRef) {
        super(attrResId, isAttrRef);
        this.direction = direction;
    }

    @Override
    protected void applyWithTheme(@NonNull View view, @NonNull Context themeContext) {
        if (!(view instanceof TextView)) return;
        TextView tv = (TextView) view;
        Drawable drawable = resolveDrawable(themeContext);
        if (drawable == null) return;

        boolean isRelative = ATTR_NAME_DRAWABLE_START.equals(direction)
                || ATTR_NAME_DRAWABLE_END.equals(direction);

        // 下面getCompoundDrawablesRelative, getCompoundDrawables方法返回的Drawable数组一定非空
        if (isRelative) {
            Drawable[] d = tv.getCompoundDrawablesRelative();
            //noinspection ConstantConditions
            Drawable start  = ATTR_NAME_DRAWABLE_START.equals(direction)  ? drawable : d[0];
            //noinspection ConstantConditions
            Drawable top    = d[1];
            //noinspection ConstantConditions
            Drawable end    = ATTR_NAME_DRAWABLE_END.equals(direction)    ? drawable : d[2];
            //noinspection ConstantConditions
            Drawable bottom = d[3];
            tv.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        } else {
            Drawable[] d = tv.getCompoundDrawables();
            //noinspection ConstantConditions
            Drawable left   = ATTR_NAME_DRAWABLE_LEFT.equals(direction)   ? drawable : d[0];
            //noinspection ConstantConditions
            Drawable top    = ATTR_NAME_DRAWABLE_TOP.equals(direction)    ? drawable : d[1];
            //noinspection ConstantConditions
            Drawable right  = ATTR_NAME_DRAWABLE_RIGHT.equals(direction)  ? drawable : d[2];
            //noinspection ConstantConditions
            Drawable bottom = ATTR_NAME_DRAWABLE_BOTTOM.equals(direction) ? drawable : d[3];
            tv.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        }
    }
}

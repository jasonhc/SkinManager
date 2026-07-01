package com.hcdev.skin;

import com.letv.core.utils.ContextProvider;
import com.letv.tvdesktop.core.R;

/**
 * @author hechuan1 on 2026/4/27.
 */
public class ThemeConfiguration {

   public static int getButtonHighlightBackgroundColor() {
          return SkinUtil.getAttrColor(ContextProvider.getApplicationContext(), R.attr.focusStrokeColor);
   }

    public static int getFeedbackItemNormalImg() {
       return SkinUtil.getAttrDrawableResId(ContextProvider.getApplicationContext(), R.attr.feedbackItemNormalImg);
    }

    public static int getFeedbackItemFocusImg() {
        return SkinUtil.getAttrDrawableResId(ContextProvider.getApplicationContext(), R.attr.feedbackItemFocusImg);
    }

    public static int getFeedbackEditTextNormalColor() {
        return SkinUtil.getAttrColor(ContextProvider.getApplicationContext(), R.attr.feedbackEditTextNormalColor);
    }

    public static int getFeedbackItemFocusTextColor() {
        return SkinUtil.getAttrColor(ContextProvider.getApplicationContext(), R.attr.feedbackItemFocusTextColor);
    }

    public static int getFeedbackItemSeleteTextColor() {
        return SkinUtil.getAttrColor(ContextProvider.getApplicationContext(), R.attr.feedbackItemSelectedTextColor);
    }
}

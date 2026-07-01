package com.hcdev.skin;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.letv.tvdesktop.core.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SkinLayoutInflaterFactory implements LayoutInflater.Factory2 {
    private static final String TAG = "SkinFactory";
    private final List<SkinView> skinViews = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = createView(name, context, attrs);
        //noinspection ConstantConditions, View.getClass返回的Class对象一定非空
        Log.d(TAG, "onCreateView: name=" + name + ", view=" + (view != null ? view.getClass().getSimpleName() : "null"));
        if (view != null) {
            parseSkinAttrs(context, attrs, view);
        }
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return onCreateView(null, name, context, attrs);
    }

    @Nullable
    private View createView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = null;
        try {
            if (-1 == name.indexOf('.')) {
                //noinspection ConstantConditions, LayoutInflater.from返回的对象一定非空
                view = LayoutInflater.from(context).createView(name, "android.widget.", attrs);
                if (view == null) {
                    //noinspection ConstantConditions, LayoutInflater.from返回的对象一定非空
                    view = LayoutInflater.from(context).createView(name, "android.view.", attrs);
                }
            } else {
                //noinspection ConstantConditions, LayoutInflater.from返回的对象一定非空
                view = LayoutInflater.from(context).createView(name, null, attrs);
            }
        } catch (InflateException e) {
            Log.w(TAG, "createView exception for " + name, e);
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "createView exception for " + name, e);
        }
        return view;
    }

    private void parseSkinAttrs(@NonNull Context context, @NonNull AttributeSet attrs, @NonNull View view) {
        if (!isSkinEnabled(attrs)) return;
        List<SkinAttr> skinAttrs = collectSkinAttrs(context, attrs);
        registerSkinView(view, skinAttrs);
    }

    private boolean isSkinEnabled(@NonNull AttributeSet attrs) {
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            if ("skin_enable".equals(attrs.getAttributeName(i))) {
                return attrs.getAttributeBooleanValue(i, false);
            }
        }
        return false;
    }

    private List<SkinAttr> collectSkinAttrs(@NonNull Context context, @NonNull AttributeSet attrs) {
        List<SkinAttr> skinAttrs = new ArrayList<>();
        Set<String> capturedNames = new HashSet<>();

        collectFromXml(attrs, skinAttrs, capturedNames);
        // 兜底：对 style 中定义的 textColor/background/src，XML AttributeSet 拿不到，
        // 用 obtainStyledAttributes 捞一次。只有未从 XML 中捕获的属性才处理。
        collectFromStyle(context, attrs, skinAttrs, capturedNames);
        Log.d(TAG, "collectSkinAttrs: collected " + skinAttrs.size() + " skinAttrs");
        return skinAttrs;
    }

    private void collectFromXml(@NonNull AttributeSet attrs, @NonNull List<SkinAttr> skinAttrs,
                                @NonNull Set<String> capturedNames) {
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attrName = attrs.getAttributeName(i);
            String attrValue = attrs.getAttributeValue(i);

            if (attrName == null || attrValue == null) continue;

            int resId = -1;
            boolean isAttrRef = false;
            if (attrValue.startsWith("?")) {
                // ?attr/ 引用：'?' 后面的数字就是主题属性 ID
                try {
                    resId = Integer.parseInt(attrValue.substring(1));
                    isAttrRef = true;
                } catch (NumberFormatException ignored) {
                   // 如果属性id数字解析失败, resId保持初始值-1, 后续逻辑会忽略这个属性
                }
            } else if (attrValue.startsWith("@")) {
                // @color/@drawable/ 直接资源引用
                resId = attrs.getAttributeResourceValue(i, -1);
            }
            Log.d(TAG, "  candidate: attrName=" + attrName + ", attrValue=" + attrValue + ", resId=" + resId + ", isAttrRef=" + isAttrRef);

            if (resId != -1) {
                SkinAttr skinAttr = SkinAttr.create(attrName, resId, isAttrRef);
                if (skinAttr != null) {
                    skinAttrs.add(skinAttr);
                    capturedNames.add(attrName);
                }
            }
        }
    }

    private void registerSkinView(@NonNull View view, @NonNull List<SkinAttr> skinAttrs) {
        if (skinAttrs.isEmpty()) {
            return;
        }

        SkinView skinView = new SkinView(view, skinAttrs);
        skinViews.add(skinView);
        if (!SkinManager.getInstance().isDefaultSkin()) {
            skinView.apply();
        }
    }

    /**
     * 从 style 中捞所有支持换肤的属性，用于补充 XML AttributeSet 拿不到的 style 属性。
     * obtainStyledAttributes 会合并 XML attr + style，能拿到 style 里定义的资源 ID。
     * 只处理尚未从 XML AttributeSet 中捕获的属性名（capturedNames 控制去重）。
     */
    private void collectFromStyle(@NonNull Context context, @NonNull AttributeSet attrs,
                                  @NonNull List<SkinAttr> skinAttrs, @NonNull Set<String> capturedNames) {
        // 按索引顺序对应 STYLE_ATTR_NAMES
        int[] styleAttrIds = {
                android.R.attr.textColor,
                android.R.attr.background,
                android.R.attr.src,
                android.R.attr.drawableLeft,
                android.R.attr.drawableRight,
                android.R.attr.drawableTop,
                android.R.attr.drawableBottom,
                android.R.attr.drawableStart,
                android.R.attr.drawableEnd,
                android.R.attr.button,
                R.attr.filterColor,
                R.attr.filterColorCompound,
        };
        String[] styleAttrNames = {
                SkinAttr.ATTR_NAME_TEXT_COLOR,
                SkinAttr.ATTR_NAME_BACKGROUND,
                SkinAttr.ATTR_NAME_SRC,
                SkinAttr.ATTR_NAME_DRAWABLE_LEFT,
                SkinAttr.ATTR_NAME_DRAWABLE_RIGHT,
                SkinAttr.ATTR_NAME_DRAWABLE_TOP,
                SkinAttr.ATTR_NAME_DRAWABLE_BOTTOM,
                SkinAttr.ATTR_NAME_DRAWABLE_START,
                SkinAttr.ATTR_NAME_DRAWABLE_END,
                SkinAttr.ATTR_NAME_BUTTON,
                SkinAttr.ATTR_NAME_FILTER_COLOR,
                SkinAttr.ATTR_NAME_FILTER_COLOR_COMPOUND,
        };

        TypedArray ta = null;
        try {
            ta = context.obtainStyledAttributes(attrs, styleAttrIds);
            for (int i = 0; i < styleAttrIds.length; i++) {
                String attrName = styleAttrNames[i];
                if (capturedNames.contains(attrName)) continue; // 已从 XML 捕获，跳过

                int resId = ta.getResourceId(i, -1);
                if (resId != -1) {
                    Log.d(TAG, "  -> [from style] added SkinAttr: " + attrName + " resId=0x" + Integer.toHexString(resId));
                    SkinAttr skinAttr = SkinAttr.create(attrName, resId, false);
                    if (skinAttr != null) skinAttrs.add(skinAttr);
                }
            }
        } finally {
            if (ta != null) ta.recycle();
        }
    }

    //NOTE: 在 applySkin 之前先移除已经被回收的 View 对应的 SkinView，将来可能需要该实现
    @SuppressWarnings("ConstantConditions")
    public void applySkinRemoveDead() {
        Log.d(TAG, "applySkin: skinViews.size=" + skinViews.size());
        Iterator<SkinView> it = skinViews.iterator();
        while (it.hasNext()) {
            SkinView sv = it.next();
            if (sv.isDead()) {
                it.remove();
            } else {
                sv.apply();
            }
        }
    }

    public void applySkin() {
        Log.d(TAG, "applySkin: skinViews.size=" + skinViews.size());
        for (SkinView skinView : skinViews) {
            if (skinView != null) {
                skinView.apply();
            }
        }
    }

    public void clean() {
        skinViews.clear();
    }

    /**
     * 为代码创建的 View 手动注册换肤属性，使其在皮肤切换时自动重新应用。
     *
     * <p>适用于无法在 XML 中声明 {@code app:skin_enable="true"} 的场景，例如通过
     * {@code new} 创建的 View，或在 RecyclerView Adapter 中动态构造的 View。
     *
     * <p>注册后行为与 XML inflate 的 View 完全一致：
     * <ul>
     *   <li>若当前已是非默认皮肤，立即应用一次；</li>
     *   <li>后续每次调用 {@link #applySkin()} 时自动重新应用。</li>
     * </ul>
     *
     * <p>重复注册同一 View 不会去重，调用方需自行保证只注册一次（通常在 View 初始化时）。
     *
     * @param view      需要换肤的 View
     * @param skinAttrs 该 View 关联的皮肤属性列表，通过 {@link SkinAttr#create} 构造；
     *                  为空时直接返回
     */
    public void register(@NonNull View view, @NonNull List<SkinAttr> skinAttrs) {
        if (skinAttrs.isEmpty()) return;
        SkinView skinView = new SkinView(view, skinAttrs);
        skinViews.add(skinView);
        // 与 parseSkinAttrs 不同，这里无条件 apply：
        // parseSkinAttrs 跳过 default skin 是因为 background/textColor/src 等标准属性在
        // inflate 时已被 Android 框架应用过，重复 apply 无意义。
        // 但 filterColor/filterColorCompound 是自定义属性，Android 框架不会处理，
        // 换肤框架是唯一的应用入口，default skin 下也必须 apply 一次才能生效。
        // 此方法面向代码注册场景，调用方显式传入了 skinAttrs，期望立即生效，故无条件 apply。
        skinView.apply();
    }

    /**
     * 对指定 View 重新应用当前皮肤。
     *
     * <p>适用场景：View 在 inflate 之后通过代码（如 setCompoundDrawables）修改了某个
     * 换肤属性，此时需要重新触发一次皮肤应用，以确保当前皮肤仍然生效。
     *
     * <p>注意：此方法只对 inflate 时已被本 factory 捕获并注册过的 View 有效；
     * 若 View 由代码 {@code new} 出来而非 XML inflate，则无法命中，直接返回。
     *
     * @param view 需要重新应用皮肤的 View
     */
    public void applySkinTo(@NonNull View view) {
        for (SkinView sv : skinViews) {
            if (sv != null && sv.getView() == view) {
                sv.apply();
                return;
            }
        }
    }

}

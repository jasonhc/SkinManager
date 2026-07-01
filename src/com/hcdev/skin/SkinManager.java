package com.hcdev.skin;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 皮肤管理器：支持基于 Theme 属性的动态切换。
 */
public class SkinManager {
    private static volatile SkinManager instance;
    @NonNull
    private final List<SkinObserver> observers = new ArrayList<>();
    private int currentThemeResId = 0; // 当前应用的主题 ID

    private SkinManager() {}

    @NonNull
    public static SkinManager getInstance() {
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager();
                }
            }
        }
        //noinspection ConstantConditions, 标准的单例方法返回的对象保证非空
        return instance;
    }

    public void init() {
    }

    public interface SkinObserver {
        void onSkinChanged();
    }

    public void register(@NonNull SkinObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unregister(@NonNull SkinObserver observer) {
        observers.remove(observer);
    }

    /**
     * 切换皮肤主题
     * @param themeResId 皮肤主题的 Style 资源 ID
     */
    public void changeSkin(int themeResId) {
        if (currentThemeResId == themeResId) return;
        currentThemeResId = themeResId;
        notifyObservers();
    }

    private void notifyObservers() {
        for (SkinObserver observer : observers) {
            if (observer != null) {
                observer.onSkinChanged();
            }
        }
    }

    public int getCurrentThemeResId() {
        return currentThemeResId;
    }

    public boolean isDefaultSkin() {
        return currentThemeResId == 0;
    }
}

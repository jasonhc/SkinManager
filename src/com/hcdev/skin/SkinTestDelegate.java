package com.hcdev.skin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.letv.tvdesktop.core.R;

import letv.util.BgHandler;

/**
 * @author hechuan1 on 2026/5/6.
 */
public class SkinTestDelegate {
    private static SkinTestDelegate mInstance;

    @MainThread
    @NonNull
    public static SkinTestDelegate getInstance() {
        if (mInstance == null) {
            mInstance = new SkinTestDelegate();
        }
        return mInstance;
    }

    private SkinTestDelegate() {
    }

    private static final String ACTION_TOGGLE_SKIN = "com.launcher.ACTION_TOGGLE_SKIN";

    private final int[] mAllThemeResIds = new int[] {
            R.style.launcherAppTheme_Dark,
            R.style.launcherAppTheme_Dark_Projector,
            R.style.launcherAppTheme_Light_Blue,
            R.style.launcherAppTheme_Light_White
    };
    private final BroadcastReceiver mSkinToggleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || !ACTION_TOGGLE_SKIN.equals(intent.getAction())) return;

            changeToNextTheme();
        }
    };
    @Nullable
    private BgHandler mBgHandler;

    private void changeToNextTheme() {
        int currentTheme = SkinManager.getInstance().getCurrentThemeResId();
        int currentThemeIndex = 0;
        for (int i = 0; i < mAllThemeResIds.length; i++) {
            if (mAllThemeResIds[i] == currentTheme) {
                currentThemeIndex = i;
                break;
            }
        }
        int nextThemeIndex = (currentThemeIndex + 1) % mAllThemeResIds.length;
        SkinManager.getInstance().changeSkin(mAllThemeResIds[nextThemeIndex]);

        if (mBgHandler != null) {
            mBgHandler.checkViewBackgroundWithAnimation();
        }
    }

    @Nullable
    private Context mContext;

    public void init(@NonNull Context context) {
        IntentFilter skinFilter = new IntentFilter(ACTION_TOGGLE_SKIN);
        mContext = context;
        mContext.registerReceiver(mSkinToggleReceiver, skinFilter);
    }

    public void setBgHandler(@Nullable BgHandler bgHandler) {
        mBgHandler = bgHandler;
    }

    public void cleanUp() {
        if (mContext != null) {
            mContext.unregisterReceiver(mSkinToggleReceiver);
        }
    }
}

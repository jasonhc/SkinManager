package com.hcdev.skin;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 换肤基类 Activity：内部持有 SkinActivityDelegate，自动完成换肤框架接入。
 * 若无法继承本类（已有继承链），请直接使用 SkinActivityDelegate。
 */
public abstract class SkinBaseActivity extends Activity {

    @NonNull
    private final SkinActivityDelegate mSkinDelegate = new SkinActivityDelegate();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSkinDelegate.onPreCreate(this);
        super.onCreate(savedInstanceState);
        mSkinDelegate.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSkinDelegate.onDestroy();
    }
}

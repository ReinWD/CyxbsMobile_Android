package com.mredrck.cyxbs.base.fragment;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.mredrck.cyxbs.base.presenter.BasePresenter;

/**
 * Created by ：AceMurder
 * Created on ：2017/9/6
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public abstract class TabDataBindingFragment <P extends BasePresenter, T extends ViewDataBinding> extends BaseDataBindingFragment<P, T> {

    private boolean isShown, shouldUpgrade;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ready(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShown && shouldUpgrade) {
            shouldUpgrade = false;
            ready(null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isShown) {
            shouldUpgrade = true;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isShown = !hidden;
        if (!hidden) {
            ready(null);
        }
    }
}
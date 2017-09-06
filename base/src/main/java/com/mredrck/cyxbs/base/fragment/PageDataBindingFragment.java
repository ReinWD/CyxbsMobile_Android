package com.mredrck.cyxbs.base.fragment;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mredrck.cyxbs.base.presenter.BasePresenter;

/**
 * Created by ：AceMurder
 * Created on ：2017/9/6
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public abstract class PageDataBindingFragment <P extends BasePresenter, T extends ViewDataBinding> extends BaseDataBindingFragment<P, T> {
    private static final String TAG = "PageDataBindingFragment";

    public boolean isVisibility = false;
    private boolean isCurrentShown = false;
    private boolean isViewCreated = false;

    public boolean isViewCreated() {
        return isViewCreated;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View createContentView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        if (container != null) {
            if (!(container instanceof ViewPager)) {

            }
        }
        return super.createContentView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //如果非滑动，点击切换，如果需要重新加载view，setUserVisibleHint先执行，但是不会调用onDisplay
        if (!isViewCreated && isCurrentShown) {
            isVisibility = true;
            ready(savedInstanceState);
        }
        //presenter和inject已经执行过
        isViewCreated = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //超过缓存，清理了view
        isVisibility = false;
        isViewCreated = false;
        isCurrentShown = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //从其他页面回到mainAcivity，setUserVisibleHint不会被调用，但当前显示的isCurrentShown为true
        if (isCurrentShown) {
            startRecordRetentionTime(this.getClass().getSimpleName());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisibility = false;
        //跳到其他页面，并不会调用setUserVisibleHint，但当前显示的isCurrentShown为true
        if (isCurrentShown) {
            endRecordRetentionTime(this.getClass().getSimpleName());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isCurrentShown = true;
            startRecordRetentionTime(this.getClass().getSimpleName());
            //如果View还没有被创建，即使被调用了，也不会去执行onDisplay，presenter和InjuctView都没有被初始化
            if (isViewCreated) {
                isVisibility = true;
                ready(null);
            }
        } else if (isCurrentShown) {
            isCurrentShown = false;
            endRecordRetentionTime(this.getClass().getSimpleName());
        }
    }

    private void startRecordRetentionTime(String className) {
    }

    private void endRecordRetentionTime(String className) {

    }
}

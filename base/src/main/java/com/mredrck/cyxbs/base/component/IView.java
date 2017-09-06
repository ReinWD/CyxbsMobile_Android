package com.mredrck.cyxbs.base.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mredrck.cyxbs.base.presenter.BasePresenter;

/**
 * Created by ：AceMurder
 * Created on ：2017/9/5
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public interface IView {
    int getLayoutRes();

    void viewCreated(Bundle savedInstanceState);

    <P extends BasePresenter> P createPresenter();

    View createContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void onReady(Bundle savedInstanceState);
}

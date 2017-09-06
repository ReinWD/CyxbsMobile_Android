package com.mredrck.cyxbs.base.presenter;

import android.databinding.BaseObservable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by ：AceMurder
 * Created on ：2017/9/5
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public class BasePresenter extends BaseObservable {
    protected FragmentActivity activity;

    public BasePresenter(FragmentActivity activity) {
        this.activity = activity;
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }
}

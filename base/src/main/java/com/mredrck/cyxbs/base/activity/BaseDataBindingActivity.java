package com.mredrck.cyxbs.base.activity;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mredrck.cyxbs.base.R;
import com.mredrck.cyxbs.base.component.IView;
import com.mredrck.cyxbs.base.presenter.BasePresenter;
import com.mredrck.cyxbs.base.util.SoftInputManager;
import com.mredrck.cyxbs.base.util.statusbar.StatusBarCompat;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by ：AceMurder
 * Created on ：2017/9/5
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public abstract class BaseDataBindingActivity <P extends BasePresenter, T extends ViewDataBinding> extends AppCompatActivity implements IView {

    private static final String TAG = "BaseDataBindingActivity";
    private static final String STATUS_BAR_BG = "#0091ea";
    public BaseHandler mHandler;
    protected T dataBinding;
    protected P presenter;
    protected ViewGroup rootView;
    protected ViewGroup contentView;
    protected Activity activity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.layout_base);
        activity = this;
        rootView = (ViewGroup) findViewById(R.id.rootView);
        contentView = (ViewGroup) findViewById(R.id.content_frame);
        setContentView(createContentView(getLayoutInflater(), contentView, savedInstanceState));
        initActionBar();
        initDataBinding();
        initBasePresenter();
        initStatusBarColor();
        viewCreated(savedInstanceState);
    }


    protected void initActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        }
    }

    public void initStatusBarColor() {
        try {
       //     ViewUtils.setWindowStatusBarColor(this, R.color.napos_blue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void setStatusBarColor(@ColorInt int statusColor, int alpha) {
        StatusBarCompat.setStatusBarColor(this, statusColor, alpha);
    }

    public View createContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutRes = getLayoutRes();
        if (layoutRes != 0) {
            Log.d(TAG, "createContentView: ");
            return inflater.inflate(layoutRes, container, false);
        }
        return null;
    }

    public void onReady(Bundle savedInstanceState) {

    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        contentView.removeAllViews();
        View.inflate(this, layoutResID, contentView);
    }

    @Override
    public void setContentView(View view) {
        contentView.removeAllViews();
        contentView.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        contentView.removeAllViews();
        contentView.addView(view, params);
    }

    protected void initBasePresenter() {
        presenter = createPresenter();
        //dataBinding.setVariable(BR.presenter, presenter);
    }

    @Override
    public P createPresenter() {
        Class presentClass = getPresentClass();
        if (presentClass != null) {
            try {
                Constructor constructor = presentClass.getConstructor(FragmentActivity.class);
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                return (P) constructor.newInstance(this);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Class getPresentClass() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            if (parameterizedType.getActualTypeArguments().length == 2) {
                Type[] types = parameterizedType.getActualTypeArguments();
                return (Class<P>) types[0];
            }
        }
        return null;
    }

    /**
     * use(#onCreateOptionsMenu(Menu,MenuInflater ))
     *
     * @param menu
     * @return
     */
    @Override
    @Deprecated
    public boolean onCreateOptionsMenu(Menu menu) {
        onCreateOptionsMenu(menu, getMenuInflater());
        return super.onCreateOptionsMenu(menu);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    @Deprecated
    protected void onSaveInstanceState(Bundle outState) {
        try {
            if (presenter != null) {
                presenter.onSaveInstanceState(outState);
            }
            super.onSaveInstanceState(outState);
        } catch (IllegalStateException e) {

        }
    }

    @Override
    public void finish() {
        SoftInputManager.hideSoftInput(this);
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (presenter != null) {
            presenter.onRestoreInstanceState(savedInstanceState);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initDataBinding() {
        dataBinding = DataBindingUtil.bind(contentView.getChildAt(0));
    }

    @Override
    protected void onDestroy() {
        mHandler = null;
        super.onDestroy();
    }

    protected BaseHandler getHandler() {
        if (mHandler == null) {
            mHandler = new BaseHandler(this, getHandlerCallBack());
        }
        return mHandler;
    }

    private BaseHandler.HandlerOperate getHandlerCallBack() {
        return new BaseHandler.HandlerOperate() {
            @Override
            public void handleMessage(Message message) {
                progressHandler(message);
            }
        };
    }

    protected void progressHandler(Message message) {

    }

    protected static class BaseHandler extends Handler {

        private final WeakReference<Context> mActivity;
        private BaseHandler.HandlerOperate handlerOperate;

        BaseHandler(Context activity, BaseHandler.HandlerOperate operate) {
            mActivity = new WeakReference<>(activity);
            this.handlerOperate = operate;
        }

        @Override
        public void handleMessage(Message msg) {
            Context activity = mActivity.get();
            if (activity != null && handlerOperate != null) {
                handlerOperate.handleMessage(msg);
            }
        }

        interface HandlerOperate {
            void handleMessage(Message message);
        }
    }
}

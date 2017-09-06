package com.mredrck.cyxbs.base.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mredrck.cyxbs.base.component.IView;
import com.mredrck.cyxbs.base.presenter.BasePresenter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by ：AceMurder
 * Created on ：2017/9/6
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public abstract class BaseDataBindingFragment <P extends BasePresenter, T extends ViewDataBinding> extends Fragment implements IView {




    protected T dataBinding;
    protected P presenter;
    protected FragmentActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (FragmentActivity) context;

    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return createContentView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public View createContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutRes = getLayoutRes();
        if (layoutRes == 0) {
            return super.onCreateView(inflater, container, savedInstanceState);
        } else {
            return inflater.inflate(layoutRes, container, false);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataBinding();
        initBasePresenter();
        viewCreated(savedInstanceState);
    }

    void initBasePresenter() {
        presenter = createPresenter();
      //  dataBinding.setVariable(BR.presenter, presenter);
    }

    @Override
    public <P extends BasePresenter> P createPresenter() {
        Class presentClass = getPresentClass();
        if (presentClass != null) {
            try {
                Constructor constructor = presentClass.getConstructor(FragmentActivity.class);
                return (P) constructor.newInstance(activity);
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

    void initDataBinding() {
        dataBinding = DataBindingUtil.bind(getView());
    }

    /**
     * fragment manager tab 切换时，调用  创建时，不做调用，可以使用viewCreated回调
     * viewpager 使用BaseTabDatabindingFragment 调用
     */
    public void ready(final Bundle savedInstanceState) {
        onReady(savedInstanceState);
    }

    public void onReady(final Bundle savedInstanceState) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (presenter != null) {
            presenter.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (presenter != null) {
            presenter.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // fragment ondetach时不会主动将fragmentmanager状态重置
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    protected ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    protected void setTitle(int resId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(resId);
        }
    }

    protected void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}

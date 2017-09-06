package com.mredrck.cyxbs.base.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by ：AceMurder
 * Created on ：2017/9/5
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public class ViewUtils {
    public static void show(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    //改这段代码的时候，只需要同时按下option + space
    public static boolean isViewVisible(View view) {
        return view != null && view.getVisibility() == View.VISIBLE;
    }

    public static void hide(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setViewVisibleOrGone(View view, boolean visible) {
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public static void setVisibleOrInvisible(View view, boolean visible) {
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public static void setViewInvisible(View view) {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void inverseVisibleOrGone(View view) {
        setViewVisibleOrGone(view, !isViewVisible(view));
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void setGradientDrawable(View view, String color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        try {
            drawable.setColor(Color.parseColor(color));
            drawable.setShape(GradientDrawable.RECTANGLE);
//            drawable.setCornerRadius(radius);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    }

    public static void setCornorDrawable(View view, String color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        try {
            drawable.setColor(Color.parseColor(color));
            drawable.setShape(GradientDrawable.RECTANGLE);
            if (radius > 0) {
                drawable.setCornerRadius(radius);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    }

    public static Bitmap getBase64Bitmap(String bitmapString) {
        Bitmap bitmap = null;
        if (bitmapString != null) {
            try {
                String value = bitmapString.substring(bitmapString.indexOf(",") + 1, bitmapString.length());
                bitmap = getBitmapFromBase64(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Bitmap getBitmapFromBase64(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

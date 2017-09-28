package com.mredrock.cyxbs.network.interceptor;

import com.mredrock.cyxbs.APP;
import com.mredrock.cyxbs.util.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by ：AceMurder
 * Created on ：2017/9/26
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public class UserAgentInterceptor implements Interceptor {
    private static int versionCode  = 0;
    @Override
    public Response intercept(Chain chain) throws IOException {
        chain.request().headers().newBuilder().removeAll("User-Agent").add("User-Agent",createUserAgent());
       return chain.proceed(chain.request());
    }


    private String createUserAgent(){
        StringBuilder builder = new StringBuilder("ZhangShangChongYou_Android_");
        if (versionCode == 0 )
            versionCode = Utils.getAppVersionCode(APP.getContext());
        builder.append("version:");
        builder.append(versionCode);
        return builder.toString();
    }
}

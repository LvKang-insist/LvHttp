package com.lvhttp.test;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author 345 QQ:1831712732
 * @package : com.lvhttp.test
 * @time : 2020/10/19  20:43
 * @description :
 */
public class CustomInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Response proceed = chain.proceed(chain.request());


        throw new IOException("网络异常");
    }
}

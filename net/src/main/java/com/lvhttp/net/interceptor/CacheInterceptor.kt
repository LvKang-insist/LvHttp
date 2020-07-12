package com.lvhttp.net.interceptor

import com.lvhttp.net.LvHttp
import com.lvhttp.net.utils.isNetworkConnected
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @name CacheInterceptor
 * @package com.www.net.interceptor
 * @author 345 QQ:1831712732
 * @time 2020/6/22 23:32
 * @description 缓存
 */
class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (isNetworkConnected(LvHttp.getAppContext())) {
            //如果缓存已经存在：不超过maxAge---->不进行请求,直接返回缓存数据
            //超出了maxAge--->发起请求获取数据更新，请求失败返回旧的缓存数据
            val maxAge = 60
            return response.newBuilder()
                //清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                .removeHeader("Pragma")
                .header("Cache-Control", "max-age=$maxAge")
                .build()
        }
        return response
    }
}
package com.www.net

import android.app.Application
import com.google.gson.Gson
import com.www.net.converter.LvDefaultConverterFactory
import com.www.net.converter.LvDefaultCallAdapterFactory
import com.www.net.error.ErrorDispose
import com.www.net.error.ErrorKey
import com.www.net.interceptor.CacheInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class LvController {

    lateinit var appContext: Application
    lateinit var retrofit: Retrofit
    private val mCache = mutableMapOf<String, Any>()
    val errorDisposes: MutableMap<ErrorKey, (message: String) -> Unit> = mutableMapOf()


    fun <T> newInstance(clazz: Class<T>): T {
        if (mCache[clazz.name] == null) {
            val create = retrofit.create(clazz) as Any
            mCache[clazz.name] = create
            return create as T
        }
        return mCache[clazz.name] as T
    }


    class LvParams {
        lateinit var baseUrl: String
        lateinit var clazz: Class<*>
        lateinit var appContext: Application
        var readTimeOut: Long = 10
        var writeTimeOut: Long = 30
        var isCache = false
        var cacheSize: Long = 1024 * 1024 * 20
        var interceptors = arrayListOf<Interceptor>()
        val errorDisposes: MutableMap<ErrorKey, (message: String) -> Unit> = mutableMapOf()

        fun apply(controller: LvController): Retrofit {
            val builder = OkHttpClient.Builder()
                .readTimeout(readTimeOut, TimeUnit.SECONDS)
                .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .retryOnConnectionFailure(true)

            //设置缓存
            if (isCache) {
                builder.cache(Cache(appContext.cacheDir, cacheSize))
                builder.addNetworkInterceptor(CacheInterceptor())
            }

            //设置拦截器
            for (interceptor in interceptors) {
                builder.addInterceptor(interceptor)
            }

            val client = builder.build()
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(LvDefaultCallAdapterFactory.create())
                .addConverterFactory(LvDefaultConverterFactory.create(Gson()))
                .build()

            controller.retrofit = retrofit
            controller.appContext = appContext
            controller.mCache["Default"] = retrofit.create(clazz)
            controller.errorDisposes.clear()
            controller.errorDisposes.putAll(errorDisposes)
            return retrofit
        }
    }

}
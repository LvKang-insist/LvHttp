package com.www.net

import com.www.net.converter.ResponseAdapterFactory
import com.www.net.converter.ResponseConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * 需要初始化的类
 */
object LvCreator {

    private var BASE_URL: String = "https://github.com/LvKang-insist/"
    private var isLog: Boolean = false

    fun init(baseUrl: String): LvCreator {
        BASE_URL = baseUrl
        return this
    }

    fun log(isLog: Boolean): LvCreator {
        this.isLog = isLog
        return this
    }

    class RetrofitHolder {
        companion object {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkHolder.okHttpClient)
                .addConverterFactory(ResponseConverterFactory())
                .addCallAdapterFactory(ResponseAdapterFactory(isLog))
                .build()
        }

    }

    class OkHolder {
        companion object {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(getInterceptor()).build()

            private fun getInterceptor(): HttpLoggingInterceptor {
                //日志输出
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                return interceptor
            }
        }
    }

    fun getRetrofit(): Retrofit {
        return RetrofitHolder.retrofit
    }

}
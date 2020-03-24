package com.www.net

import com.www.net.converter.ResponseAdapterFactory
import com.www.net.converter.ResponseConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object HttpCreator {
    private var BASE_URL: String = "https://github.com/LvKang-insist/"
    private var isLog: Boolean = false

    fun init(baseUrl: String): HttpCreator {
        BASE_URL = baseUrl
        return this
    }

    fun log(isLog: Boolean): HttpCreator {
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
            val okHttpClient = OkHttpClient.Builder().build()
        }
    }

    fun getRetrofit(): Retrofit {
        return RetrofitHolder.retrofit
    }

}
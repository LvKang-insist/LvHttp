package com.www.net

import com.google.gson.Gson
import com.www.net.converter.LvConverterFactory
import com.www.net.converter.ResponseAdapterFactory
import com.www.net.converter.ResponseConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LvController {

    lateinit var retrofit: Retrofit
    private val mCache = mutableMapOf<String, Any>()


    fun <T> newInstance(clazz: Class<T>): T {
        if (mCache[clazz.name] == null) {
            val create = retrofit.create(clazz) as Any
            mCache.put(clazz.name, create)
            return create as T
        }
        return mCache[clazz.name] as T
    }


    class LvParams {
        lateinit var baseUrl: String
        lateinit var clazz: Class<*>

        fun apply(controller: LvController): Retrofit {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(LvConverterFactory.create(Gson()))
                .build()


            controller.retrofit = retrofit
            controller.mCache["Default"] = retrofit.create(clazz)
            return retrofit
        }
    }

}
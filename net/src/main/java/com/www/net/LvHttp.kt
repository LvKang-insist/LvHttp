package com.www.net

import android.app.Application
import com.www.net.error.ErrorDispose
import com.www.net.error.ErrorKey
import okhttp3.Interceptor
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import java.lang.Exception
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*


object LvHttp {

    private val mController = LvController()

    /**
     * 获取 Retrofit
     */
    fun getRetrofit(): Retrofit {
        return mController.retrofit
    }

    /**
     * 创建 API
     */
    fun <T> createApi(clazz: Class<T>): T {
        return mController.newInstance(clazz)
    }

    /**
     * 获取 Application
     */
    fun getAppContext(): Application {
        return mController.appContext
    }

    /**
     * 设置异常处理
     */
    fun setErrorDispose(errorKey: ErrorKey, error: (String) -> Unit) {
        mController.errorDisposes[errorKey] = error
    }

    class Builder {
        private var p: LvController.LvParams = LvController.LvParams()

        fun setApplication(application: Application): Builder {
            p.appContext = application
            return this
        }

        /**
         * 设置 BaseUrl
         */
        fun setBaseUrl(baseUrl: String): Builder {
            p.baseUrl = baseUrl
            return this
        }

        fun setReadTimeOut(readTime: Long): Builder {
            p.readTimeOut = readTime
            return this
        }

        fun setWirteTimeOut(writeTimeOut: Long): Builder {
            p.writeTimeOut = writeTimeOut
            return this
        }

        /**
         * 是否开启缓存，默认关闭
         */
        fun isCache(iscache: Boolean): Builder {
            p.isCache = iscache
            return this
        }

        /**
         * 设置缓存大小，默认 20mb
         */
        fun setCacheSize(cacheSize: Long): Builder {
            p.cacheSize = cacheSize
            return this
        }

        /**
         * 添加拦截器
         */
        fun addInterceptor(interceptor: Interceptor): Builder {
            p.interceptors.add(interceptor)
            return this
        }

        /**
         * 设置异常处理
         */
        fun setErrorDispose(errorKey: ErrorKey, error: (String) -> Unit): Builder {
            p.errorDisposes[errorKey] = error
            return this
        }

        /**
         * 设置 Service
         */
        fun <T> setService(clazz: Class<T>): Builder {
            p.clazz = clazz
            return this
        }

        fun build() {
            create()
        }

        private fun create(): Retrofit {
            return p.apply(mController)
        }
    }
}
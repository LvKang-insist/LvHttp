package com.www.net

import android.app.Application
import com.www.net.error.ErrorDispose
import com.www.net.error.ErrorKey
import com.www.net.error.ErrorValue
import okhttp3.Interceptor
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import java.lang.Exception
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import kotlin.math.log


object LvHttp {

    private val mController = LvController()

    /**
     * 获取 Retrofit
     */
    @JvmStatic
    fun getRetrofit(): Retrofit {
        return mController.retrofit
    }

    /**
     * 创建 API
     */
    @JvmStatic
    fun <T> createApi(clazz: Class<T>): T {
        return mController.newInstance(clazz)
    }

    /**
     * 设置异常处理
     * @param errorKey key
     * @param errorValue value
     */
    @JvmStatic
    fun setErrorDispose(errorKey: ErrorKey, errorValue: ErrorValue) {
        mController.errorDisposes[errorKey] = errorValue
    }

    /**
     * @return 获取异常处理
     */
    @JvmStatic
    fun getErrorDispose(errorKey: ErrorKey): ErrorValue? {
        return mController.errorDisposes[errorKey]
    }

    /**
     * @return 是否打印日志
     */
    fun getIsLogging(): Boolean {
        return mController.isLogging
    }

    /**
     * 获取 Application
     */
    fun getAppContext(): Application {
        return mController.appContext
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

        /**
         * 连接时间，秒为单位
         */
        fun setConnectTimeOut(connecTime: Long): Builder {
            p.connectTimeOut = connecTime
            return this
        }

        /**
         * 下载响应的时候等待时间，秒为单位
         */
        fun setReadTimeOut(readTime: Long): Builder {
            p.readTimeOut = readTime
            return this
        }

        /**
         * 写入请求的等待时间，秒为单位
         */
        fun setWirteTimeOut(writeTimeOut: Long): Builder {
            p.writeTimeOut = writeTimeOut
            return this
        }

        /**
         * 是否打印 log
         * @param logging true 表示打印
         */
        fun isLoging(logging: Boolean): Builder {
            p.isLogging = logging
            return this
        }

        /**
         * 是否开启缓存，默认关闭
         * @param iscache true 表示开启缓存
         */
        fun isCache(iscache: Boolean): Builder {
            p.isCache = iscache
            return this
        }

        /**
         * 设置缓存大小，默认 20mb
         * @param cacheSize 缓存大小
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
        fun setErrorDispose(errorKey: ErrorKey, errorValue: ErrorValue): Builder {
            p.errorDisposes[errorKey] = errorValue
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
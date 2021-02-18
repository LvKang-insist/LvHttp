package com.lvhttp.net

import android.app.Application
import com.lvhttp.net.error.ErrorKey
import com.lvhttp.net.error.ErrorValue
import okhttp3.Interceptor
import retrofit2.Retrofit


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
        return mController.isLog
    }

    /**
     * 获取 Application
     */
    fun getAppContext(): Application {
        return mController.appContext
    }

    /**
     * 获取 code
     */
    fun getCode(): Int {
        return mController.code
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
         * @param islog true 表示打印
         */
        fun isLog(islog: Boolean): Builder {
            p.isLog = islog
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
         * 设置 code 值，如果 == code，则说明请求成功，否则进行异常处理
         */
        fun setCode(code: Int): Builder {
            p.code = code
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

        fun build() {
            create()
        }

        private fun create(): Retrofit {
            return p.apply(mController)
        }
    }
}
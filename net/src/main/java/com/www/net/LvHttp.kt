package com.www.net

import android.app.Application
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


    fun getRetrofit(): Retrofit {
        return mController.retrofit
    }


    fun <T> createApi(clazz: Class<T>): T?{
//        val t = mController.newInstance(clazz)

        var t: T?= null

        val retrofit = Class.forName("retrofit2.Retrofit")

        val create = retrofit.getDeclaredMethod("create", Class::class.java)

        try {
            t = create.invoke(getRetrofit(), clazz) as T
        } catch (e: Exception) {

        }

        return t
        /*val any = t as Any
        return Proxy.newProxyInstance(any::class.java.classLoader,
            any::class.java.interfaces,
            object : InvocationHandler {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun invoke(proxy: Any?, method: Method?, args: Array<Any>?): Any? {
                    try {
                        return method?.invoke(any)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return null
                }
            }) as T*/


    }

    fun <T> Retrofit.crea(service: Class<T>): T {

        return Proxy.newProxyInstance(
            service::class.java.classLoader,
            service.interfaces,
            object : InvocationHandler {
                override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {


                    if (method!!.declaringClass == Any::class.java) {
                        return method.invoke(this, args)
                    }
                    val forName = Class.forName("retrofit2.Platform")

                    val get = forName.getDeclaredMethod("get")
                    val platform = get.invoke(null, null)

                    val bol = forName.getDeclaredMethod("isDefaultMethod", Method::class.java)
                        .invoke(platform, method) as Boolean
                    if (bol) {
                        forName.getDeclaredMethod(
                            "invokeDefaultMethod",
                            Method::class.java,
                            Class::class.java,
                            Any::class.java,
                            java.lang.reflect.Array::class.java
                        ).invoke(platform, method, service, proxy, args)
                    }
                    val clazz = Class.forName("retrofit2.Retrofit")

                    val ref =
                        clazz.getDeclaredMethod("loadServiceMethod", Method::class.java)
                    val invoke = ref.invoke(getRetrofit(), method)

                    val ser = Class.forName("retrofit2.ServiceMethod")

                    val inv =
                        ser.getDeclaredMethod("invoke", java.lang.reflect.Array::class.java)


                    return inv.invoke(invoke, if (args != null) args else arrayOf())
                }

            }
        ) as T
//
    }

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
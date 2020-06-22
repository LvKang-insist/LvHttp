package com.www.net

import androidx.lifecycle.LifecycleOwner
import com.www.net.converter.ResponseAdapterFactory
import com.www.net.converter.ResponseConverterFactory
import com.www.net.download.DownLoadLaunch
import com.www.net.download.OnStateListener
import com.www.net.get.GetRequest
import com.www.net.post.PostRequest
import com.www.net.postFile.ListFileRequest
import com.www.net.postFile.MapFileRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


object LvHttp {

    private val mController = LvController()


    fun getRetrofit(): Retrofit {
        return mController.retrofit
    }


    fun <T> getInstance(clazz: Class<T>): T {
        return mController.newInstance(clazz)
    }



    class Builder {
        private var p: LvController.LvParams = LvController.LvParams()

        /**
         * 设置 BaseUrl
         */
        fun setBaseUrl(baseUrl: String): Builder {
            p.baseUrl = baseUrl
            return this
        }

        /**
         * 设置 Service
         */
        fun<T> setService(clazz: Class<T>): Builder {
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
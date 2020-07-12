package com.lvhttp.test

import android.app.Application
import android.widget.Toast
import com.lvhttp.net.LvHttp
import com.lvhttp.net.error.ErrorKey
import com.lvhttp.net.error.ErrorValue

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        LvHttp.Builder()
            .setApplication(this)
            .setBaseUrl("https://www.wanandroid.com")
            .isCache(false)
            .isLoging(true)
            .setErrorDispose(ErrorKey.ErrorCode, ErrorValue {
                Toast.makeText(this, "Code 错误", Toast.LENGTH_SHORT).show()
            })
            .setErrorDispose(ErrorKey.AllEexeption, ErrorValue {
                it.printStackTrace()
                Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show()
            })
            .build()
    }
}
package com.www.lvhttp

import android.app.Application
import android.widget.Toast
import com.www.net.LvHttp
import com.www.net.error.ErrorKey
import com.www.net.error.ErrorValue

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        LvHttp.Builder()
            .setApplication(this)
            .setBaseUrl("https://api.github.com/")
            .isCache(false)
            .isLoging(true)
            .setService(Service::class.java)
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
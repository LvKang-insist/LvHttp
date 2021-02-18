package com.lvhttp.test

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.lvhttp.net.LvHttp
import com.lvhttp.net.error.ErrorKey
import com.lvhttp.net.error.ErrorValue

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LvHttp.Builder()
            .setApplication(this)
            .setBaseUrl("https://www.wanandroid.com/")
            .isCache(false)
            .isLog(true)
            .setCode(0)
            .setErrorDispose(ErrorKey.ErrorCode, ErrorValue {
                Log.e("345：", "Code 错误")
                Toast.makeText(this, "Code 错误", Toast.LENGTH_SHORT).show()
            })
            .setErrorDispose(ErrorKey.AllEexeption, ErrorValue {
                it.printStackTrace()
                Log.e("345：", "网络错误")
                Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show()
            })
            .build()
    }
}
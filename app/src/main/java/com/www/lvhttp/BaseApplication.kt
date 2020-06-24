package com.www.lvhttp

import android.app.Application
import com.www.net.LvHttp

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        LvHttp.Builder()
            .setApplication(this)
            .setBaseUrl("https://api.github.com/")
            .isCache(false)
            .setService(Service::class.java)
            .build()
    }
}
package com.www.lvhttp

import android.app.Application
import com.www.net.LvHttp

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        LvHttp.Builder()
            .setBaseUrl("https://api.github.com/")
            .setService(Service::class.java)
            .build()
    }
}
package com.www.net

import com.www.net.get.GetRequest

object LvHttp {

    fun get(url: String): GetRequest {
        return GetRequest(url)
    }
}
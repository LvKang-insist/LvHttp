package com.www.net

import com.google.gson.Gson
import retrofit2.Response

/**
 * 请求数据结果
 */
class Result(val value: String) {
    var response: Response<*>? = null

    fun <T> format(clazz: Class<T>): T {
        return Gson().fromJson<T>(value, clazz)
    }
}
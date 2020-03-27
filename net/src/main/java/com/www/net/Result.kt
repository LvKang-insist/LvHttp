package com.www.net

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response

/**
 * 请求数据结果
 */
class Result(val value: String) {
    var response: Response<*>? = null

    fun <T> format(clazz: Class<T>): T {
        try {
            return Gson().fromJson<T>(value, clazz)
        } catch (e: Exception) {
            throw ClassCastException("${this.javaClass.name}类型转换异常")
        }
    }

    /**
     * 注：必须使用内联，否则泛型会被擦除，导致无法转换
     */
    inline fun <reified T> format(value: String): T {
        try {
            return Gson().fromJson<T>(value, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            throw ClassCastException("${this.javaClass.name}类型转换异常")
        }
    }
}

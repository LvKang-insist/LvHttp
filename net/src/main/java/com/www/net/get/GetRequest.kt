package com.www.net.get

import android.util.Log
import com.www.net.LvCreator
import com.www.net.Request
import com.www.net.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Get 请求
 */
class GetRequest : Request {

    lateinit var url: String
    private var mPostService: GetService = LvCreator.getRetrofit().create(GetService::class.java);

    constructor()
    constructor(url: String) {
        this.url = url
    }

    fun addUrl(url: String): GetRequest {
        this.url = url
        return this
    }


    /**
     * 异步请求，返回 Result，结果是 Main 线程
     */
    override fun send(block: suspend (Result) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                request()
            }
            if (result != null) block(result) else Log.e("网络错误，", "请重试")
        }
    }

    /**
     * 请求失败回调 error
     */
    override fun send(block: suspend (Result) -> Unit, error: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                request()
            }
            if (result != null) block(result) else error()
        }
    }


    /**
     * 同步请求
     */
    override fun send(): Result? {
        return request()
    }

    /**
     * 发起请求
     */
    private fun request(): Result? {
        return when {
            params.isNotEmpty() && headers.isNotEmpty() -> mPostService.get(
                url, params, headers
            )
            params.isNotEmpty() -> mPostService.get(url, params)
            else -> mPostService.get(url)
        }
    }
}
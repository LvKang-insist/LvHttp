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

    constructor()
    constructor(url: String) {
        this.url = url
    }

    fun addUrl(url: String): GetRequest {
        this.url = url
        return this
    }

    /**
     * 多次请求
     */
    fun <X, Y> pair(block: suspend (GetRequest) -> Pair<X, Y>, pair: (Pair<X, Y>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = block(this@GetRequest)
            launch(Dispatchers.Main) {
                pair(result)
            }
        }
    }


    private var mGetService: GetService = LvCreator.getRetrofit().create(GetService::class.java);

    /**
     * 异步请求，返回 Result，结果是 Main 线程
     */
    override fun send(block: suspend (Result) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            block(
                withContext(Dispatchers.IO) {
                    request()
                }
            )
        }
    }

    /**
     * 同步请求
     */
    override fun send(): Result {
        return request()
    }

    /**
     * 发起请求
     */
    private fun request(): Result {
        return when {
            params.isNotEmpty() || headers.isNotEmpty() -> mGetService.get(
                url, params, headers
            )
            params.isNotEmpty() -> mGetService.get(url, params)
            else -> mGetService.get(url)
        }
    }
}
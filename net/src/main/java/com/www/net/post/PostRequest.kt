package com.www.net.post

import android.util.Log
import com.www.net.LvCreator
import com.www.net.Request
import com.www.net.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

class PostRequest : Request {


    private var mPostService: PostService = LvCreator.getRetrofit().create(PostService::class.java);
    private var body: RequestBody? = null

    constructor()
    constructor(url: String, body: RequestBody?) {
        this.mUrl = url
        this.body = body
    }

    fun addUrl(url: String): PostRequest {
        this.mUrl = url
        return this
    }

    override fun send(block: suspend (Result) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                request()
            }
            if (result != null) block(result) else Log.e("网络错误，", "请重试")
        }
    }

    override fun send(block: suspend (Result) -> Unit, error: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                request()
            }
            if (result != null) block(result) else error()
        }
    }

    override fun send(): Result? {
        return request()
    }

    /**
     * 发起请求
     */
    private fun request(): Result? {
        if (body != null) {
            return requestRaw()
        }
        return when {
            params.isNotEmpty() && headers.isNotEmpty() -> {
                mPostService.postHeader(
                    mUrl!!, headers, params
                )
            }
            headers.isNotEmpty() -> {
                mPostService.postHeader(mUrl!!, headers)
            }
            else -> {
                mPostService.post(mUrl!!, params)
            }
        }
    }

    /**
     * 发起请求，带Body
     */
    private fun requestRaw(): Result? {
        return when {
            headers.isNotEmpty() -> mPostService.postRaw(
                mUrl!!, headers, body!!
            )
            else -> {
                mPostService.postRaw(mUrl!!, body!!)
            }
        }
    }

}
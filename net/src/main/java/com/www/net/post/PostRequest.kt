package com.www.net.post

import com.www.net.LvCreator
import com.www.net.Request
import com.www.net.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

class PostRequest : Request {

    lateinit var url: String
    private var mPostService: PostService = LvCreator.getRetrofit().create(PostService::class.java);
    private var body: RequestBody? = null

    constructor()
    constructor(url: String, body: RequestBody?) {
        this.url = url
        this.body = body
    }

    fun addUrl(url: String): PostRequest {
        this.url = url
        return this
    }

    override fun send(block: suspend (Result) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            block(
                withContext(Dispatchers.IO) {

                    request()
                }
            )
        }
    }

    override fun send(): Result {
        return request()
    }

    /**
     * 发起请求
     */
    private fun request(): Result {
        if (body != null) {
            return requestRaw()
        }
        return when {
            params.isNotEmpty() && headers.isNotEmpty() -> {
                mPostService.post(
                    url, headers, params
                )
            }
            headers.isNotEmpty() -> {
                mPostService.postHeader(url, headers)
            }
            else -> {
                mPostService.post(url, params)
            }
        }
    }

    /**
     * 发起请求，带Body
     */
    private fun requestRaw(): Result {
        return when {
            headers.isNotEmpty() -> mPostService.postRaw(
                url, headers, body!!
            )
            else -> {
                mPostService.postRaw(url, body!!)
            }
        }
    }

}
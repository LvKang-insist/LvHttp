package com.www.net.get

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
class GetRequest(private val url: String) : Request() {

    private var mGetService: GetService = LvCreator.getRetrofit().create(GetService::class.java);

    /**
     * 发送请求，返回 Result
     */
    override fun send(block: suspend (Result) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            block(
                withContext(Dispatchers.IO) {
                    when {
                        params.isNotEmpty() || headers.isNotEmpty() -> mGetService.get(
                            url, params, headers
                        )
                        params.isNotEmpty() -> mGetService.get(url, params)
                        else -> mGetService.get(url)
                    }
                }
            )
        }
    }
}
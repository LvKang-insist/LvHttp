package com.www.net.get

import com.www.net.HttpCreator
import com.www.net.Request
import com.www.net.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetRequest(private val url: String) : Request() {

    var mGetService: GetService = HttpCreator.getRetrofit().create(GetService::class.java);

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
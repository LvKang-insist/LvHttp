package com.lvhttp.net.launch

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.lvhttp.net.LvHttp
import com.lvhttp.net.error.CodeException
import com.lvhttp.net.error.ErrorKey
import com.lvhttp.net.response.BaseResponse
import com.lvhttp.net.response.ResultState
import kotlinx.coroutines.*
import java.lang.Exception

/**
 * @name Launch
 * @package com.www.net
 * @author 345 QQ:1831712732
 * @time 2020/6/23 21:33
 * @description
 */

suspend fun <T> launchHttp(block: suspend () -> T): ResultState<T> = tryCatch(block)

fun <T> LifecycleOwner.zipLaunch(
    block: List<suspend () -> T>,
    result: (List<ResultState<T>>) -> Unit
) {
    lifecycleScope.launch {
        val list = arrayListOf<Deferred<ResultState<T>>>()
        block.forEach {
            list.add(async { tryCatch(it) })
        }
        val data = list.awaitAll()
        launch(Dispatchers.Main) {
            result.invoke(data)
        }
    }
}

private suspend fun <T> tryCatch(block: suspend () -> T): ResultState<T> {
    var t: ResultState<T>? = null
    try {
        val invoke = block.invoke()
        (invoke as? BaseResponse<*>)?.run {
            notifyData()
            if (_code != LvHttp.getCode()) {
                t = ResultState.ErrorState(error = CodeException(_code, "code 异常"))
                // Code 异常处理
                LvHttp.getErrorDispose(ErrorKey.ErrorCode)?.error?.let {
                    it(CodeException(_code, _message ?: "code 异常"))
                }
            } else {
                t = ResultState.SuccessState(invoke)
            }
        } ?: run {
            t = ResultState.SuccessState(invoke)
        }
    } catch (e: Exception) {
        t = ResultState.ErrorState(error = e)
        // 自动匹配异常
        ErrorKey.values().forEach {
            if (it.name == e::class.java.simpleName) {
                LvHttp.getErrorDispose(it)?.error?.let { it(e) }
            }
        }
        // 如果全局异常启用
        LvHttp.getErrorDispose(ErrorKey.AllEexeption)?.error?.let {
            it(e)
            return t!!
        }
    }
    return t!!
}
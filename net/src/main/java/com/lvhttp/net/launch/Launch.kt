package com.lvhttp.net.launch

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
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

/**
 * 适用于在 Activity/Fragment 中调用
 * @param result :ResultState<BaseResponse<T>>
 *                ResultState 状态管理
 *                BaseResponse<T> 数据包装类
 * 适用于有包装类的情况，默认包装类为 BaseResponse ，
 * 可参考 BaseResponse 创建自定义的包装类，参考如下写法完成 code/自定义的验证
 */
fun <T> LifecycleOwner.launchAf(
    block: suspend () -> T,
    result: (ResultState<T>) -> Unit
) {
    result(ResultState.LoadingState())
    lifecycleScope.launch(Dispatchers.IO) {
        val data = tryCatch(block)
        withMain {
            result(data)
        }
    }
}

/**
 * 适用于在 Activity/Fragment 中调用
 * @param result :ResultState<T>
 *                T 数据包装类
 * 适用于无数据包装类
 * 注意，此方式无法对 code 进行验证，code 错误时不会进行 code 异常处理
 */
fun <T> LifecycleOwner.launchAfHttp(
    block: suspend () -> T,
    result: ((ResultState<T>) -> Unit)
) {
    result.invoke(ResultState.LoadingState())
    lifecycleScope.launch(Dispatchers.IO) {
        val data = tryCatch2(block)
        result.run {
            withMain {
                result(data)
            }
        }
    }
}

fun <T> LifecycleOwner.zipAfLaunch(
    block: List<suspend () -> T>,
    result: (List<ResultState<T>>) -> Unit
) {
    lifecycleScope.launch {
        val list = arrayListOf<Deferred<ResultState<T>>>()
        block.forEach {
            list.add(
                async {
                    tryCatch(it)
                }
            )
        }
        val data = list.awaitAll()
        launch(Dispatchers.Main) {
            result.invoke(data)
        }
    }
}

/**
 * 适用于在 ViewModel 中调用
 * @param result :ResultState<BaseResponse<T>>
 *                ResultState 状态管理
 *                BaseResponse<T> 数据包装类
 */
fun <T> ViewModel.launchVm(
    block: suspend () -> T,
    result: (ResultState<T>) -> Unit
) {
    viewModelScope.launch {
        result(ResultState.LoadingState())
        val data = tryCatch(block)
        withMain { result(data) }
    }
}

/**
 * 适用于在 ViewModel 中调用
 * @param result :ResultState<BaseResponse<T>>
 *                ResultState 状态管理
 *                T 数据包装类
 * 适用于无数据包装类
 */
fun <T> ViewModel.launchVmHttp(
    block: suspend () -> T,
    result: ((ResultState<T>) -> Unit)
) {
    result.invoke(ResultState.LoadingState())
    viewModelScope.launch(Dispatchers.IO) {
        val data = tryCatch2(block)
        result.run {
            withMain {
                result(data)
            }
        }
    }
}

/**
 *  通常情况下，不推荐使用这种方式，可能会造成内存泄漏
 *  如：UI 已经被销毁了，而耗时操作没有完成。
 */
suspend fun <T> launchHttp(
    block: suspend () -> BaseResponse<T>,
    result: (ResultState<BaseResponse<T>>) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        result(ResultState.LoadingState())
        val data = tryCatch(block)
        withMain {
            result(data)
        }
    }
}

private suspend fun <T> tryCatch(block: suspend () -> T): ResultState<T> {
    var t: ResultState<T>
    try {
        val invoke = block.invoke()
        val result = (invoke as BaseResponse<*>).notifyData()
        if (result._code != LvHttp.getCode()) {
            t = ResultState.ErrorState(error = CodeException(result._code, "code 异常"))
            // Code 异常处理
            LvHttp.getErrorDispose(ErrorKey.ErrorCode)?.error?.let {
                withMain { it(CodeException(result._code, result._message ?: "code 异常")) }
            }
        } else {
            t = ResultState.SuccessState(invoke)
        }
    } catch (e: Exception) {
        t = ResultState.ErrorState(error = e)
        // 自动匹配异常
        ErrorKey.values().forEach {
            if (it.name == e::class.java.simpleName) {
                withMain { LvHttp.getErrorDispose(it)?.error?.let { it(e) } }
            }
        }
        // 如果全局异常启用
        LvHttp.getErrorDispose(ErrorKey.AllEexeption)?.error?.let {
            withMain { it(e) }
            return t
        }
    }
    return t
}

private suspend fun <T> tryCatch2(block: suspend () -> T): ResultState<T> {
    var t: ResultState<T>
    try {
        val result = block.invoke()
        t = ResultState.SuccessState(result)
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            t = ResultState.ErrorState(error = e)
            // 如果全局异常启用
            LvHttp.getErrorDispose(ErrorKey.AllEexeption)?.error?.let {
                withMain { it(e) }
                return@withContext
            }
            // 自动匹配异常
            ErrorKey.values().forEach {
                if (it.name == e::class.java.simpleName) {
                    withMain { LvHttp.getErrorDispose(it)?.error?.let { it(e) } }
                    return@withContext
                }
            }
        }
    }
    return t
}

private suspend fun withMain(block: () -> Unit) = withContext(Dispatchers.Main) {
    block.invoke()
}

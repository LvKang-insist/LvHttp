package com.lvhttp.net.launch

import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.lvhttp.net.LvHttp
import com.lvhttp.net.error.CodeException
import com.lvhttp.net.error.ErrorKey
import com.lvhttp.net.response.BaseResponse
import com.lvhttp.net.response.ResponseData
import com.lvhttp.net.response.ResultState
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.NullPointerException
import kotlin.coroutines.CoroutineContext

/**
 * @name Launch
 * @package com.www.net
 * @author 345 QQ:1831712732
 * @time 2020/6/23 21:33
 * @description
 */

/**
 * 适用于在 Activity/Fragment 中调用
 */
fun <T> LifecycleOwner.launchAf(
    block: suspend () -> ResponseData<T>,
    result: (ResultState<ResponseData<T>>) -> Unit
) {
    result(ResultState.LoadingState(ResponseData(errorCode = -1, errorMsg = "")))
    lifecycleScope.launch(Dispatchers.IO) {
        val data = tryCatch(block)
        launch(Dispatchers.Main) {
            result(data)
        }
    }
}

/**
 * 适用于返回值不确定的情况
 */
fun <T> LifecycleOwner.launchAf2(
    block: suspend () -> T,
    result: (ResultState<T>) -> Unit
) {
    result(ResultState.LoadingState(null))
    lifecycleScope.launch(Dispatchers.IO) {
        val data = tryCatch3(block)
        launch {
            result(data)
        }
    }
}

/**
 * 适用于没有返回值的情况，例如文件下载
 */
fun LifecycleOwner.launchAf3(
    block: suspend () -> Unit
) {
    lifecycleScope.launch(Dispatchers.IO) {
        tryCatch2(block)
    }
}




/**
 * 适用于在 ViewModel 中调用
 */
fun <T> ViewModel.launchVm(
    block: suspend () -> ResponseData<T>,
    result: (ResultState<ResponseData<T>>) -> Unit
) {
    viewModelScope.launch {
        result(ResultState.LoadingState(ResponseData(errorCode = -1, errorMsg = "")))
        val data = tryCatch(block)
        launch(Dispatchers.Main) {
            result(data)
        }
    }
}


/**
 * 适用于返回值不确定的情况
 */
fun <T> ViewModel.launchVm2(
    block: suspend () -> T,
    result: (ResultState<T>) -> Unit
) {
    result(ResultState.LoadingState(null))
    viewModelScope.launch(Dispatchers.IO) {
        val data = tryCatch3(block)
        launch {
            result(data)
        }
    }
}

/**
 * 适用于没有返回值的情况，例如文件下载
 */
fun ViewModel.launchVm3(
    block: suspend () -> Unit
) {
    viewModelScope.launch(Dispatchers.IO) {
        tryCatch2(block)
    }
}

/**
 *  通常情况下，不推荐使用这种方式，可能会造成内存泄漏
 *  如：UI 已经被销毁了，而耗时操作没有完成。
 */
suspend fun <T> launchHttp(
    block: suspend () -> ResponseData<T>,
    result: (ResultState<ResponseData<T>>) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        result(ResultState.LoadingState(ResponseData(errorCode = -1, errorMsg = "")))
        val data = tryCatch(block)
        launch(Dispatchers.Main) {
            result(data)
        }
    }
}

suspend fun <T> tryCatch(block: suspend () -> ResponseData<T>): ResultState<ResponseData<T>> {
    var t: ResultState<ResponseData<T>>
    try {
        val result = block.invoke()
        if (result.errorCode != LvHttp.getCode()) {
            t = ResultState.ErrorState(result)
            //Code 异常处理
            LvHttp.getErrorDispose(ErrorKey.ErrorCode)?.error?.let {
                it(CodeException(result.errorCode, result.errorMsg))
            }
        } else {
            t = ResultState.SuccessState(result)
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            t = ResultState.ErrorState(ResponseData(errorCode = -1, errorMsg = e.message ?: "网络错误"))
            //如果全局异常启用
            LvHttp.getErrorDispose(ErrorKey.AllEexeption)?.error?.let {
                it(e)
                return@withContext
            }
            //自动匹配异常
            ErrorKey.values().forEach {
                if (it.name == e::class.java.simpleName) {
                    LvHttp.getErrorDispose(it)?.error?.let { it(e) }
                    return@withContext
                }
            }
            e.printStackTrace()
        }
    }
    return t
}

suspend fun <T> tryCatch2(block: suspend () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

suspend fun <T> tryCatch3(block: suspend () -> T): ResultState<T> {
    var t: ResultState<T>
    try {
        val result = block.invoke()
        t = ResultState.SuccessState(result)
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            t = ResultState.ErrorState(null)
            //如果全局异常启用
            LvHttp.getErrorDispose(ErrorKey.AllEexeption)?.error?.let {
                it(e)
                return@withContext
            }
            //自动匹配异常
            ErrorKey.values().forEach {
                if (it.name == e::class.java.simpleName) {
                    LvHttp.getErrorDispose(it)?.error?.let { it(e) }
                    return@withContext
                }
            }
            e.printStackTrace()
        }
    }
    return t
}

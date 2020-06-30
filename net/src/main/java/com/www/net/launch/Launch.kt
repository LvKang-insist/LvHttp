package com.www.net.launch

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.www.net.LvHttp
import com.www.net.error.ErrorKey
import kotlinx.coroutines.*
import java.lang.Exception
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
fun LifecycleOwner.launchAfHttp(
    context: CoroutineContext = Dispatchers.IO,
    error: (suspend (Throwable) -> Unit)? = null,
    finally: (suspend () -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(context) {
        tryCatch(error, finally, block)
    }
}

/**
 * 适用于在 ViewModel 中调用
 */
fun ViewModel.launchVmHttp(
    context: CoroutineContext = Dispatchers.IO,
    error: (suspend (Throwable) -> Unit)? = null,
    finally: (suspend () -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch {
        tryCatch(error, finally, block)
    }
}

/**
 *  通常情况下，不推荐使用这种方式，可能会造成内存泄漏
 *  如：UI 已经被销毁了，而耗时操作没有完成。
 */
suspend fun launchHttp(
    error: (suspend (Throwable) -> Unit)? = null,
    finally: (suspend () -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) {
    tryCatch(error, finally, block)
}

private suspend fun tryCatch(
    error: (suspend (Throwable) -> Unit)? = null,
    finally: (suspend () -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) {
    try {
        coroutineScope(block)
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            error?.let {
                it(e)
                return@withContext
            }

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
    } finally {
        finally?.let {
            withContext(Dispatchers.Main) {
                it()
            }
        }
    }
}




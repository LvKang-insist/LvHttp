package com.www.net

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
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


suspend fun tryCache(
    error: (suspend (Throwable) -> Unit)? = null,
    finally: (suspend () -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) {
    try {
        coroutineScope(block)
    } catch (e: Exception) {
        error?.apply { invoke(e) }
    } finally {
        finally?.apply { invoke() }
    }
}

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
        tryCache(error, finally, block)
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
        tryCache(error, finally, block)
    }
}



package com.www.net

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * @name Launch
 * @package com.www.net
 * @author 345 QQ:1831712732
 * @time 2020/6/23 21:33
 * @description
 */


private suspend fun tryCach(
    errorBlock: (suspend (Throwable) -> Unit)?,
    block: suspend CoroutineScope.() -> Unit
) {

    try {
        coroutineScope {
            block()
        }
    } catch (t: Throwable) {
        errorBlock?.let {
            it(t)
            return
        }
        //全局异常处理
    }
}

fun LifecycleOwner.launchAfHttp(
    context: CoroutineContext = Dispatchers.IO,
    errorBlock: (suspend (Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(context) {
        tryCach(errorBlock, block)
    }
}
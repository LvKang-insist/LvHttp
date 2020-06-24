package com.www.net

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.Result
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.createCoroutine

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
    errorBlock: (suspend (Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch(context) {
        block()
    }
}

/**
 * 适用于在 ViewModel 中调用
 */
fun ViewModel.launchVmHttp(
    context: CoroutineContext = Dispatchers.IO,
    errorBlock: (suspend (Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch {
        block()
    }
}

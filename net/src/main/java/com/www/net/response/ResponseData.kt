package com.www.net.response

import android.widget.Toast
import com.www.net.LvHttp
import com.www.net.error.CodeException
import com.www.net.error.ErrorKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @name Data
 * @package com.www.net
 * @author 345 QQ:1831712732
 * @time 2020/6/27 15:41
 * @description 对 Response 的扩展，使框架的灵活性更高
 *              可仿照当前类自定义 Response 的扩展
 */

data class ResponseData<T>(val data: T, val errorCode: Int, val errorMsg: String)

/**
 * 同步扩展
 */
suspend inline fun <T> ResponseData<T>.result(
    isToast: Boolean = true,
    noinline error: ((Int) -> Unit?)? = null,
    noinline block: (T) -> Unit
) {
    block(isToast, error, block)
}


/**
 * 异步扩展，结果切回 IO 线程
 */
suspend inline fun <T> ResponseData<T>.resultIO(
    isToast: Boolean = true,
    noinline error: ((Int) -> Unit?)? = null,
    noinline block: (T) -> Unit
) {
    withContext(Dispatchers.IO) { block(isToast, error, block) }
}

/**
 * 异步扩展，结果切回 Main 线程
 */
suspend inline fun <T> ResponseData<T>.resultMain(
    isToast: Boolean = true,
    noinline error: ((Int) -> Unit?)? = null,
    noinline block: (T) -> Unit
) {
    withContext(Dispatchers.Main) { block(isToast, error, block) }
}

/**
 * 不做任何东西
 */
suspend inline fun <T> ResponseData<T>.block(
    isToast: Boolean = true,
    noinline error: ((Int) -> Unit?)? = null,
    block: (T) -> Unit
) {
    if (errorCode == 0) {
        error?.let {
            it(errorCode)
            return
        }
        //Code 异常处理
        LvHttp.getErrorDispose(ErrorKey.ErrorCode)?.error?.let {
            it(CodeException(errorCode))
            return
        }
        if (isToast) {
            withContext(Dispatchers.Main) {
                Toast.makeText(LvHttp.getAppContext(), "登录验证失败", Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        block(data)
    }
}
package com.lvhttp.net.response

import android.widget.Toast
import com.lvhttp.net.LvHttp
import com.lvhttp.net.error.CodeException
import com.lvhttp.net.error.ErrorKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.NullPointerException

/**
 * @name Data
 * @package com.www.net
 * @author 345 QQ:1831712732
 * @time 2020/6/27 15:41
 * @description 对 Response 的包装，可直接继承，参考示例中的 ResponseData
 */

open class BaseResponse<T>(var _data: T? = null, var _code: Int = 0, var _message: String? = null) {
    open fun notifyData(): BaseResponse<T> = this
}

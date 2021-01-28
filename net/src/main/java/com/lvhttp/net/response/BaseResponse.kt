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
 * @description 对 Response 的扩展，使框架的灵活性更高
 *              可仿照当前类自定义 Response 的扩展
 */

open class BaseResponse<T>(val _data: T?)

data class ResponseData<T>(val data: T? = null, val errorCode: Int, val errorMsg: String) :
    BaseResponse<T>(data)
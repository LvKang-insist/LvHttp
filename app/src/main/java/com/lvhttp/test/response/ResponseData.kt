package com.lvhttp.test.response

import android.util.Log
import com.lvhttp.net.response.BaseResponse

/**
 * @name ResponseData
 * @package com.lvhttp.test.response
 * @author 345 QQ:1831712732
 * @time 2021/06/30 22:28
 * @description
 */

data class ResponseData<T>(val data: T, val errorCode: Int, val errorMsg: String) :
    BaseResponse<T>() {
    override fun notifyData(): BaseResponse<T> {
        _data = data
        _code = errorCode
        _message = errorMsg
        return super.notifyData()
    }
}
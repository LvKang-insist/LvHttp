package com.lvhttp.net.response

import java.lang.Exception

/**
 * @name ResultState
 * @package com.lvhttp.net.response
 * @author 345 QQ:1831712732
 * @time 2021/01/05 10:19
 * @description
 */
sealed class ResultState<T> {

    class SuccessState<T>(val t: T) : ResultState<T>()
    class ErrorState<T>(val error: Exception) : ResultState<T>()

    /** 获取请求成功后的数据 */
    fun toData(data: (T) -> Unit): ResultState<T> {
        if (this is SuccessState) data.invoke(this.t)
        return this
    }

    /** 获取请求失败后的错误信息 */
    fun toError(error: (Exception) -> Unit): ResultState<T> {
        if (this is ErrorState) error.invoke(this.error)
        return this
    }
}
package com.lvhttp.net.response

import java.lang.Exception

/**
 * @name ResultState
 * @package com.lvhttp.net.response
 * @author 345 QQ:1831712732
 * @time 2021/01/05 10:19
 * @description
 */
sealed class ResultState<T>(t: T?) {
    var data: T? = null

    init {
        data = t
    }

    class SuccessState<T>(val t: T) : ResultState<T>(t)
    class ErrorState<T>(val t: T? = null, val error: Exception) : ResultState<T>(t)
    class LoadingState<T>(val t: T? = null) : ResultState<T>(t)


    fun toLoading(loading: () -> Unit): ResultState<T> {
        if (this is LoadingState) loading.invoke()
        return this
    }

    /**
     * 直接转为结果
     */
    fun toData(data: (T) -> Unit): ResultState<T> {
        if (this is SuccessState) data.invoke(this.t)
        return this
    }

    fun toError(error: (Exception) -> Unit): ResultState<T> {
        if (this is ErrorState) error.invoke(this.error)
        return this
    }
}

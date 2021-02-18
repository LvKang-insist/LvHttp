package com.lvhttp.net.response

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
    class ErrorState<T>(val t: T?) : ResultState<T>(t)
    class LoadingState<T>(val t: T?) : ResultState<T>(t)

    /**
     * 直接转为结果,可能为 null
     */
    fun toData(loading: (() -> Unit)? = null, block: ((T?) -> Unit)? = null) {
        if (this is LoadingState) {
            loading?.invoke()
        } else {
            block?.invoke(data)
        }
    }
}

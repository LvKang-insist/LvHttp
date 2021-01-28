package com.lvhttp.net.response

/**
 * @name ResultState
 * @package com.lvhttp.net.response
 * @author 345 QQ:1831712732
 * @time 2021/01/05 10:19
 * @description
 */
sealed class ResultState<T>(t: T?) {
    class SuccessState<T>( val t: T) : ResultState<T>(t)
    class ErrorState<T>( val t: T?) : ResultState<T>(t)
    class LoadingState<T>(val t: T?) : ResultState<T>(t)
}

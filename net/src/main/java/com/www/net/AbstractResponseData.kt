package com.www.net

/**
 * @name AbResponseData
 * @package com.www.net
 * @author 345 QQ:1831712732
 * @time 2020/6/23 20:56
 * @description
 */

abstract class AbstractResponseData<T>(data: T)


suspend fun <T> AbstractResponseData<T>.block() {

}

package com.lvhttp.net.error

import java.lang.Exception

/**
 * @name ErrorValue
 * @package com.www.net.error
 * @author 345 QQ:1831712732
 * @time 2020/6/27 17:55
 * @description
 */
class ErrorValue(val error: (Exception) -> Unit)
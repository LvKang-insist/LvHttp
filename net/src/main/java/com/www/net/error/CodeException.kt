package com.www.net.error

import java.lang.Exception

/**
 * @name CodeException
 * @package com.www.net.error
 * @author 345 QQ:1831712732
 * @time 2020/6/27 18:25
 * @description
 */
class CodeException(val code: Int) : Exception("Code 异常：$code")
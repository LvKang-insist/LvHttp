package com.www.net.error

import java.lang.Exception

/**
 * @name CodeException
 * @package com.www.net.error
 * @author 345 QQ:1831712732
 * @time 2020/6/27 18:25
 * @description
 */
class CodeException(private val code: Int, private val mssage: String) : Exception("Code 异常：$code")
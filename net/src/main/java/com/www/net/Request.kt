package com.www.net

abstract class Request {


    protected val headers = mutableMapOf<String, String>()
    protected val params = mutableMapOf<String, Any>()

    /**
     * 添加头文件
     */
    fun addHeader(key: String, value: String): Request {
        headers[key] = value
        return this
    }

    /**
     * value 只能为基本数据类型
     */
    fun addParam(key: String, value: Any): Request {
        params[key] = value
        return this
    }

    abstract fun send(block: suspend (Result) -> Unit)
}
package com.www.net

abstract class Request {

    /**
     * 头文件
     */
    protected val headers by lazy {
        mutableMapOf<String, String>()
    }
    /**
     * 参数
     */
    protected val params by lazy {
        mutableMapOf<String, Any>()
    }

    /**
     * 添加头文件
     */
    fun addHeader(key: String, value: String): Request {
        headers[key] = value
        return this
    }
    fun addHeader(headers: MutableMap<String, String>) {
        this.headers.putAll(headers)
    }

    /**
     * value 只能为基本数据类型
     */
    fun addParam(key: String, value: Any): Request {
        params[key] = value
        return this
    }
    fun addParam(params: MutableMap<String, Any>) {
        this.params.putAll(params)
    }

    abstract fun send(block: suspend (Result) -> Unit)

    abstract fun send():Result
}
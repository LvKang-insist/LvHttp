package com.www.net

abstract class Request {

    var mUrl: String? = null

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

    fun addHeader(headers: MutableMap<String, String>): Request {
        this.headers.putAll(headers)
        return this
    }

    /**
     * value 只能为基本数据类型
     */
    fun addParam(key: String, value: Any): Request {
        params[key] = value
        return this
    }

    fun addParam(params: MutableMap<String, Any>): Request {
        this.params.putAll(params)
        return this
    }

    /**
     * 异步请求，失败打印 log
     */
    abstract fun send(block: suspend (Result) -> Unit)


    /**
     * 异步请求，失败调用进行回调
     */
    abstract fun send(block: suspend (Result) -> Unit, error: suspend () -> Unit)

    /**
     * 同步请求
     */
    abstract fun send(): Result?
}
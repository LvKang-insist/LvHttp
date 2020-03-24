package com.www.net.exception

/**
 * 网络请求异常
 */
class LvNetWorkException : RuntimeException {
    constructor() {}
    constructor(message: String) : super(message)
}
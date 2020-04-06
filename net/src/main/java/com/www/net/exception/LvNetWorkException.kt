package com.www.net.exception

import android.util.Log

/**
 * 网络异常
 */
class LvNetWorkException : RuntimeException {
    constructor() {}
    constructor(message: String) : super(message) {
        Log.e("LvNetWorkException：", message)
    }
}
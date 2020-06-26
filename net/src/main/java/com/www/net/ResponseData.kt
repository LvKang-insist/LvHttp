package com.www.net

import android.util.Log
import org.json.JSONObject

/**
 * @name ResponseData
 * @package com.www.net
 * @author 345 QQ:1831712732
 * @time 2020/6/26 23:04
 * @description
 */

open class ResponseData<T>(val data: T, val result: String) {

    open fun parse(name: String, value: Int, error: (() -> Unit)? = null, block: () -> Unit) {
        //TODO 解决解析失败的问题
        val jsonObject = JSONObject(result)
        val int = jsonObject.getInt(name)
        if (int == value) {
            Log.e("-------", "parse: 成功")
            error?.let { it() }
            return
        }
        return block()
    }
}
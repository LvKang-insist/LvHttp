package com.www.net.param

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * @name CreateBody
 * @package com.www.net.param
 * @author 345 QQ:1831712732
 * @time 2020/6/29 19:46
 * @description
 */

/**
 * 返回一个新的请求体，该请求体传输此请求的内容。
 */
fun createFileRequestBody(file: File): RequestBody {
    return file.asRequestBody(MultipartBody.FORM)
}

/**
 * 返回传输此字符串的新请求主体
 */
fun createStrRequestBody(str: String): RequestBody {
    return str.toRequestBody(MultipartBody.FORM)
}

/**
 * 创建一个 RequestBody
 */
fun createRequestBody(values: String): RequestBody {
    return values.toRequestBody("application/json;charset=UTF-8".toMediaTypeOrNull())
}


/**
 * 创建一个 map , value 为 RequestBody
 */
fun createRequestBodyMap(params: Map<String, Any>)
        : MutableMap<String, RequestBody> {
    val par: MutableMap<String, RequestBody> = mutableMapOf()
    params.forEach {
        par[it.key] = createStrRequestBody(it.value as String)
    }
    return par
}
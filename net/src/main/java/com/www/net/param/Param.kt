package com.www.net.param

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * @name UpLoadFile
 * @package com.www.net.file
 * @author 345 QQ:1831712732
 * @time 2020/6/28 23:47
 * @description
 */

fun createRequestBody(values:String): RequestBody {

    RequestBody.create(MediaType.parse("application/"))
}

/**
 * 创建一个 value 为 RequestBody 的 map
 * 一般是用来创建请求参数的 map，其中 value 的类型为 RequestBody
 */
fun createParamsMap(params: MutableMap<String, Any>)
        : MutableMap<String, RequestBody> {
    val par: MutableMap<String, RequestBody> = mutableMapOf()
    params.forEach {
        par[it.key] = createStrRequestBody(it.value as String)
    }
    return par
}

/**
 * 创建一个 MultipartBody.part 类型的数组
 */
fun createFilesParts(filesMap: MutableMap<String, File>)
        : Array<MultipartBody.Part> {
    val list = arrayListOf<MultipartBody.Part>()

    filesMap.forEach {
        val fileBody = createFileRequestBody(it.value)
        val part = MultipartBody.Part.createFormData(it.key, it.value.name, fileBody)
        list.add(part)
    }
    return list.toTypedArray()
}

/**
 * 创建一个 MultipartBody.part 类型的数组
 * 其中 key 是 唯一的
 */
fun createFilesParts(key: String, lists: List<File>)
        : Array<MultipartBody.Part> {
    val parts = arrayListOf<MultipartBody.Part>()
    lists.forEach {
        val fileBody = createFileRequestBody(it)
        val part = MultipartBody.Part.createFormData(key, it.name, fileBody)
        parts.add(part)
    }
    return parts.toTypedArray()
}

/**
 * 创建一个  MultipartBody.part
 */
fun createFilePart(key: String, file: File): MultipartBody.Part {
    val fileBody = createFileRequestBody(file)
    return MultipartBody.Part.createFormData(key, file.name, fileBody)
}


/**
 * 返回一个新的请求体，该请求体传输此请求的内容。
 * 类型为 File
 */
fun createFileRequestBody(file: File): RequestBody {
    return file.asRequestBody(MultipartBody.FORM)
}

/**
 * 返回一个新的请求体，该请求体传输此请求的内容。
 * 类型为 String
 */
fun createStrRequestBody(str: String): RequestBody {
    return str.toRequestBody(MultipartBody.FORM)
}
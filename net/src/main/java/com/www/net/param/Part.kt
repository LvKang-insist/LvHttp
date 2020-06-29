package com.www.net.param

import okhttp3.MultipartBody
import java.io.File

/**
 * @name Parts
 * @package com.www.net.param
 * @author 345 QQ:1831712732
 * @time 2020/6/29 19:50
 * @description
 */


/**
 * 创建一个  MultipartBody.part
 */
fun createPart(key: String, file: File): MultipartBody.Part {
    val fileBody = createFileRequestBody(file)
    return MultipartBody.Part.createFormData(key, file.name, fileBody)
}


/**
 * 创建一个 MultipartBody.part 类型的数组
 */
fun createParts(filesMap: Map<String, File>)
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
fun createParts(key: String, lists: List<File>)
        : Array<MultipartBody.Part> {
    val parts = arrayListOf<MultipartBody.Part>()
    lists.forEach {
        val fileBody = createFileRequestBody(it)
        val part = MultipartBody.Part.createFormData(key, it.name, fileBody)
        parts.add(part)
    }
    return parts.toTypedArray()
}


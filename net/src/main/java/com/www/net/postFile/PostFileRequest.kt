package com.www.net.postFile

import android.util.Log
import com.www.net.LvCreator
import com.www.net.Request
import com.www.net.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * 上传文件的抽象类，封装了一下嘴基础的配置
 */
abstract class PostFileRequest(url: String) : Request() {

    protected val mPostFileService: PostFileService =
        LvCreator.getRetrofit().create(PostFileService::class.java)

    init {
        addUrl(url)
    }

    override fun send(block: suspend (Result) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) { request() }
            if (result != null) block(result) else Log.e("网络错误，", "请重试")
        }
    }

    override fun send(block: suspend (Result) -> Unit, error: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) { request() }
            if (result != null) block(result) else error()
        }
    }

    override fun send(): Result? {
        return request()
    }

    protected open fun request(): Result? {
        return null
    }


    /**
     * 创建一个 value 为 RequestBody 的 map
     * 一般是用来创建请求参数的 map，其中 value 的类型为 RequestBody
     */
    protected fun createParamsMap(params: MutableMap<String, Any>)
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
    protected fun createFilesParts(filesMap: MutableMap<String, File>)
            : Array<MultipartBody.Part> {
        val list = arrayListOf<MultipartBody.Part>()
//        filesMap.forEach { list.add(it.key) }

        /*val parts = Array<MultipartBody.Part>(filesMap.size) {
            val file = filesMap[list[it]]
            val fileBody = createFileRequestBody(file!!)
            MultipartBody.Part.createFormData(list[it], file.name, fileBody)
        }
        */

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
    protected fun createFilesParts(key: String, lists: List<File>)
            : Array<MultipartBody.Part> {
        val parts = arrayListOf<MultipartBody.Part>()
        lists.forEach {
            val fileBody = createFileRequestBody(it)
            val part = MultipartBody.Part.createFormData(key, it.name, fileBody)
            parts.add(part)
        }
        return parts.toTypedArray()
    }
}

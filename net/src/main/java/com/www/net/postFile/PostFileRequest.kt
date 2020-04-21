package com.www.net.postFile

import android.util.Log
import com.www.net.LvCreator
import com.www.net.Request
import com.www.net.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class PostFileRequest(url: String) : Request() {

    private val mPostFileService: PostFileService =
        LvCreator.getRetrofit().create(PostFileService::class.java)
    private var mFile: File? = null

    init {
        mUrl = url
    }

    fun setUrl(url: String): PostFileRequest {
        mUrl = url
        return this
    }

    fun setFile(file: File): PostFileRequest {
        this.mFile = file
        return this
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

    private fun request(): Result? {
        if (mFile == null) throw KotlinNullPointerException("文件为 null")

        val body = RequestBody.create(MultipartBody.FORM.toString().toMediaTypeOrNull(), mFile!!)

//        val body = mFile!!.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", mFile?.name, body)

//        return                 mPostFileService.postFile(mUrl!!, part)

        return when {
            params.isNotEmpty() && headers.isNotEmpty() -> {
                mPostFileService.postFileHeader(mUrl!!, params, headers, part)
            }
            headers.isNotEmpty() -> {
                mPostFileService.postFileHeader(mUrl!!, headers, part)
            }
            params.isNotEmpty() -> {
                Log.e("------------","hhhhhhhhh")
                mPostFileService.postFile(mUrl!!, params, part)
            }
            else -> {
                mPostFileService.postFile(mUrl!!, part)
            }
        }
    }
}
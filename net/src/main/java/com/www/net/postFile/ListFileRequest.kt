package com.www.net.postFile

import androidx.annotation.NonNull
import com.www.net.Result
import java.io.File

class ListFileRequest(url: String) : PostFileRequest(url) {

    private var mListFiles: MutableList<File>? = null
    private var mKey: String? = null

    /**
     * 上传单个文件
     */
    fun file(@NonNull key: String,@NonNull file: File) {
        files(key, mutableListOf(file))
    }

    /**
     * 上传多个文件，key 是唯一的
     */
    fun files(@NonNull key: String,@NonNull files: MutableList<File>): ListFileRequest {
        this.mListFiles = files
        this.mKey = key
        return this
    }


    override fun request(): Result? {
        if (mListFiles == null) throw KotlinNullPointerException("文件为 null")
        return when {
            params.isNotEmpty() && headers.isNotEmpty() -> {
                mPostFileService.postFileHeader(
                    mUrl!!,
                    createParamsMap(params),
                    headers,
                    *createFilesParts(mKey!!, mListFiles!!)
                )
            }
            headers.isNotEmpty() -> {
                mPostFileService.postFileHeader(
                    mUrl!!, headers, *createFilesParts(mKey!!, mListFiles!!)
                )
            }
            params.isNotEmpty() -> {
                mPostFileService.postFile(
                    mUrl!!, createParamsMap(params), *createFilesParts(mKey!!, mListFiles!!)
                )
            }
            else -> {
                mPostFileService.postFile(mUrl!!, *createFilesParts(mKey!!, mListFiles!!))
            }
        }
    }

}
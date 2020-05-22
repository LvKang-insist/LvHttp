package com.www.net.postFile

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.www.net.Result
import java.io.File

class MapFileRequest(url: String) : PostFileRequest(url) {

    private var mMapFiles: MutableMap<String, File>? = null


    /**
     * 上传单个文件
     */
    fun file(@NonNull key: String, @NonNull file: File): MapFileRequest {
        return files(mutableMapOf(key to file))
    }

    /**
     * 上传多个文件
     */
    fun files(files: MutableMap<String, File>): MapFileRequest {
        this.mMapFiles = files
        return this
    }

    override fun request(): Result? {
        if (mMapFiles == null) throw KotlinNullPointerException("文件为 null")
        return when {
            params.isNotEmpty() && headers.isNotEmpty() -> {
                mPostFileService.postFileHeader(
                    mUrl!!, createParamsMap(params), headers, *createFilesParts(mMapFiles!!)
                )
            }
            headers.isNotEmpty() -> {
                mPostFileService.postFileHeader(
                    mUrl!!, headers, *createFilesParts(mMapFiles!!)
                )
            }
            params.isNotEmpty() -> {
                mPostFileService.postFile(
                    mUrl!!, createParamsMap(params), *createFilesParts(mMapFiles!!)
                )
            }
            else -> {
                mPostFileService.postFile(mUrl!!, *createFilesParts(mMapFiles!!))
            }
        }
    }
}
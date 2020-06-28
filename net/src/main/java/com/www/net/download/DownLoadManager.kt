package com.www.net.download

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * @name LvHttp
 * @class name：com.www.net.download
 * @author 345 QQ:1831712732
 * @time 2020/3/24 20:33
 * @description
 */
object DownLoadManager {
    sealed class DownloadStatus {
        object None : DownloadStatus()
        class Progress(val value: Int) : DownloadStatus()
        class Error(val throwable: Throwable) : DownloadStatus()
        class Done(val file: File) : DownloadStatus()
    }


    fun download(url: String, fileName: String, savePath: String): Flow<DownloadStatus> {
        val file = File(File(savePath).also { it.mkdirs() }, fileName)
        return flow {
            val request = Request.Builder().url(url).get().build()
            val response = OkHttpClient.Builder().build()
                .newCall(request).execute()
            if (response.isSuccessful) {
                response.body!!.let { body ->
                    //总大小
                    val total = body.contentLength()
                    //当前值
                    var emittedProcess = 0L
                    file.outputStream().use { output ->
                        body.byteStream().use { input ->
                            input.copyTo1(output) { bytesCopied ->
                                //计算百分比
                                val progress = bytesCopied * 100 / total
                                //当前的值大于上一次的就进行通知
                                if (progress - emittedProcess > 1) {
                                    //发射，外部的 collect 会执行
                                    emit(DownloadStatus.Progress(progress.toInt()))
                                    emittedProcess = progress
                                }
                            }
                        }
                    }
                    //下载完成
                    emit(DownloadStatus.Done(file))
                }
            } else {
                throw Exception(response.message)
            }
        }.catch {

            file.delete()
            emit(DownloadStatus.Error(it))
            //保留最新的值
        }.conflate()

    }
}

inline fun InputStream.copyTo1(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    progress: (Long) -> Unit
): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer)

        progress(bytesCopied)
    }
    return bytesCopied
}
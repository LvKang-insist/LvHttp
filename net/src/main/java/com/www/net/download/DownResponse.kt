package com.www.net.download

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.www.net.LvHttp
import com.www.net.utils.FileQUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.lang.NullPointerException
import java.text.DecimalFormat

/**
 * 文件下载的辅助类
 * 在 Android Q 中：path 表示的路径为 根目录/dowload/path/name
 * 在 Android Q 以下，path 表示的是 根目录/path/name
 * 注意：name 后面需要加上后缀名
 */
abstract class DownResponse(val path: String, val name: String) {
    /**
     * 开始下载时调用
     * @param size 文件大小，MB 为单位（会有偏差）
     */
    open fun create(size: Float) = Unit

    /**
     * 文件下载进度
     * @param process 进度百分比
     */
    open fun process(process: Float) = Unit

    /**
     * 下载完成
     * @param file 文件
     */
    open fun done(file: File) = Unit

    /**
     * 异常处理
     * @param e 异常
     */
    open fun error(e: Exception) = Unit
}

suspend fun ResponseBody.start(downResponse: DownResponse) {
    tryCache(downResponse) {
        (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveQ(downResponse.path, downResponse.name)
        } else {
            saveFile(downResponse.path, downResponse.name)
        })?.apply {
            val output = withMain { LvHttp.getAppContext().contentResolver.openOutputStream(this) }
            val file = FileQUtils.getFileByUri(this, LvHttp.getAppContext())
            if (output != null) {
                download(byteStream(), output, downResponse, contentLength())
                withMain { downResponse.done(file) }
            } else {
                file.delete()
            }
        } ?: withMain { downResponse.error(NullPointerException("LvHttp DownLoad ：文件路径找不到")) }
    }

}

private suspend inline fun download(
    input: InputStream,
    output: OutputStream,
    downResponse: DownResponse,
    contentLength: Long
) {
    //上次下载位置
    var emittedProcess = 0f
    withMain {
        downResponse.create(format((contentLength).toFloat() / 1024 / 1024))
    }
    input.use {
        it.copyTo(output) { process ->
            //计算百分比
            val progress = format((process).toFloat() * 100 / contentLength)
            //当前的值大于上一次的就进行通知
            if (progress - emittedProcess > 0) {
                withMain { downResponse.process(progress) }
                emittedProcess = progress
            }
        }
        withMain { downResponse.process(100f) }
    }
}

/**
 * 保留 2为小数
 */
private fun format(float: Float): Float {
    return DecimalFormat("#.00").format(float).toFloat()
}

private suspend fun <T> tryCache(response: DownResponse, block: suspend () -> T) {
    try {
        block()
    } catch (e: Exception) {
        withMain { response.error(e) }
    }
}

private suspend inline fun <T> withMain(crossinline block: () -> T) =
    withContext(Dispatchers.Main) {
        block()
    }

private inline fun InputStream.copyTo(
    output: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    progress: (Long) -> Unit
): Long {
    output.use {
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = read(buffer)
        while (bytes >= 0) {
            output.write(buffer, 0, bytes)
            bytesCopied += bytes
            bytes = read(buffer)
            progress(bytesCopied)
        }
        output.flush()
        return bytesCopied
    }
}

/**
 * Q 获取下载文件 file
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun saveQ(path: String, name: String): Uri? {
    val values = ContentValues()
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
    values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/")

    val external = MediaStore.Downloads.EXTERNAL_CONTENT_URI
    val resolver = LvHttp.getAppContext().contentResolver
    val insertUri = resolver.insert(external, values)

    Log.e("------------<>", "saveQ: $insertUri")
    return insertUri
}

/**
 * Q 以下获取文件 file
 */
fun saveFile(path: String, name: String): Uri? {
    val file =
        File(Environment.getExternalStorageDirectory().path + "/$path/")
    if (!file.exists()) {
        file.mkdirs()
    }
    return Uri.fromFile(File(file.parent!! + "/" + path + "/" + name))
}
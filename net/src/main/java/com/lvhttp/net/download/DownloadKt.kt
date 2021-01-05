package com.lvhttp.net.download

import android.os.Build
import com.lvhttp.net.LvHttp
import com.lvhttp.net.utils.FileQUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.lang.NullPointerException
import java.text.DecimalFormat

/**
 * @name DownloadKt
 * @package com.lvhttp.net.download
 * @author 345 QQ:1831712732
 * @time 2020/7/12 22:33
 * @description
 */

suspend fun ResponseBody.start(downResponse: DownResponse) {
    tryCache(downResponse) {
        (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveQ(downResponse.path, downResponse.name)
        } else {
            saveFile(downResponse.path, downResponse.name)
        })?.apply {
            val output = withMain {
                LvHttp.getAppContext().contentResolver.openOutputStream(this) }
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
package com.www.net.download

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.www.net.LvHttp
import okhttp3.ResponseBody
import java.io.File
import java.io.OutputStream
import java.lang.Exception
import java.lang.NullPointerException

abstract class DownResponse(val path: String, val name: String) {
    fun create(long: Long) = Unit
    fun process(process: Int) = Unit
    fun done(file: File) = Unit
    fun error(e: Exception) = Unit
}

fun ResponseBody.start(downResponse: DownResponse) {

    (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        saveQ(downResponse.path, downResponse.name)
    } else {
        TODO()
    })?.use {
        val input = byteStream()
        var byteCopied: Long = 0
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytes = input.read(buffer)
        while (bytes >= 0) {
            it.write(buffer, 0, bytes)
            it.flush()
            byteCopied += bytes
            Log.e("---------->", "onCreate: ==========${byteCopied}")
            bytes = input.read(buffer)
        }
        Log.e("--------->", "onCreate: 下载完成")
    } ?: downResponse.error(NullPointerException())
}

@RequiresApi(Build.VERSION_CODES.Q)
fun saveQ(path: String, name: String): OutputStream? {
    val values = ContentValues()
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
    values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/$path/")
    val uri = LvHttp.getAppContext().contentResolver.insert(
        MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
    )
    if (uri != null) {
        return LvHttp.getAppContext().contentResolver.openOutputStream(uri)
    }
    return null
}
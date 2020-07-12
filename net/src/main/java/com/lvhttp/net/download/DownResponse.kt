package com.lvhttp.net.download

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.lvhttp.net.LvHttp
import java.io.File
import java.lang.Exception

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
    return resolver.insert(external, values)
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
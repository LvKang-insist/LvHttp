package com.www.net.download

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.coroutines.launch
import java.io.File

/**
 * @name LvHttp
 * @class name：com.www.net.dowload
 * @author 345 QQ:1831712732
 * @time 2020/3/24 20:21
 * @description
 */
interface OnStateListener {
    fun start()
    fun process(value: Int)
    fun error(throwable: Throwable)
    fun donal(file: File)
}

object DownLoadLaunch {

    private val mDownloadModel: DownloadModel = DownloadModel()

    fun create(
        owner: LifecycleOwner,
        url: String,
        fileName: String,
        savePath: String,
        stateListener: OnStateListener
    ) {
        //这里的 Lambda 会被多次调用，当 liveData 发送消息后 lambda会得到执行
        mDownloadModel.downloadStateLiveData.observe(owner) { status ->
            when (status) {
                DownLoadManager.DownloadStatus.None -> {
                    //启动协程
                    owner.lifecycleScope.launch {
                        stateListener.start()
                        //下载
                        mDownloadModel.download(url, fileName, savePath)
                    }
                }
                is DownLoadManager.DownloadStatus.Progress -> {
                    stateListener.process(status.value)
                }
                is DownLoadManager.DownloadStatus.Error -> {
                    stateListener.error(status.throwable)
                }
                is DownLoadManager.DownloadStatus.Done -> {
                    stateListener.donal(status.file)
                }
            }
        }
    }
}
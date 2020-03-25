package com.www.net

import androidx.lifecycle.LifecycleOwner
import com.www.net.download.DownLoadLaunch
import com.www.net.download.OnStateListener
import com.www.net.get.GetRequest

/**
 * 请求入口类
 */
object LvHttp {

    /**
     * get 请求
     */
    fun get(url: String): GetRequest {
        return GetRequest(url)
    }

    /**
     * 文件下载
     */
    fun download(
        owner: LifecycleOwner,
        url: String,
        fileName: String,
        savePath: String,
        stateListener: OnStateListener
    ) {
        DownLoadLaunch.create(owner, url, fileName, savePath, stateListener)
    }
}
package com.www.net

import androidx.lifecycle.LifecycleOwner
import com.www.net.download.DownLoadLaunch
import com.www.net.download.OnStateListener
import com.www.net.get.GetRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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

    fun get(): GetRequest {
        return GetRequest()
    }

    /**
     * 并发请求，最多可并发两次
     */
    fun <T1, T2> zip(
        pair: Pair<() -> T1, () -> T2>,
        result: (Pair<T1, T2>) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val x = GlobalScope.async { pair.first() }
            val y = GlobalScope.async { pair.second() }
            launch(Dispatchers.Main) {
                result(Pair(x.await(), y.await()))
            }
        }
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
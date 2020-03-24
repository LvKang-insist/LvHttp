package com.www.net.download

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn

/**
 * @name LvHttp
 * @class name：com.www.net.download
 * @author 345 QQ:1831712732
 * @time 2020/3/24 20:34
 * @description
 */
class DownloadModel : ViewModel() {

    val downloadStateLiveData =
        MutableLiveData<DownLoadManager.DownloadStatus>(DownLoadManager.DownloadStatus.None)


    suspend fun download(url: String, fileName: String, savePath: String) {
        DownLoadManager.download(url, fileName, savePath)
            .flowOn(Dispatchers.IO)
            .collect {
                //发送数据
                downloadStateLiveData.value = it
            }
    }
}
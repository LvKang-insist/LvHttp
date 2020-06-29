package com.www.lvhttp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.www.net.LvHttp
import com.www.net.download.DownResponse
import com.www.net.download.start
import com.www.net.param.createFilesParts
import com.www.net.launchAfHttp
import com.www.net.response.resultMain
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        test.setOnClickListener {
            launchAfHttp {
                LvHttp.createApi(Service::class.java).get().resultMain {
                    Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        downloadButton.setOnClickListener {
            launchAfHttp {
                dowload()
            }
        }

    }

    private suspend fun dowload() {
        LvHttp.createApi(Service::class.java).download()
            .start(object : DownResponse("LvHttp", "chebangyang.apk") {
                override fun create(size: Float) {
                    Log.e("-------->", "create:总大小 ${(size)} ")
                }

                @SuppressLint("SetTextI18n")
                override fun process(process: Float) {
                    downloadPath.setText("$process %")
                }

                override fun error(e: Exception) {
                    e.printStackTrace()
                    downloadPath.setText("下载错误")
                }

                override fun done(file: File) {
                    Toast.makeText(
                        this@MainActivity,
                        file.absolutePath,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private suspend fun upload() {
        val path = Environment.getExternalStorageDirectory()
        val file1 = File(path.path + "/image1.png")
        val mutableMap = mutableMapOf<String, File>()
        mutableMap["456"] = file1
        val loadBean = LvHttp.createApi(Service::class.java)
            .postFile(*createFilesParts(mutableMap))
        Log.e("-------->", "upload: ${loadBean.toString()}")
    }
}

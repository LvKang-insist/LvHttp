package com.lvhttp.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lvhttp.net.LvHttp
import com.lvhttp.net.download.DownResponse
import com.lvhttp.net.download.start
import com.lvhttp.net.launch.launchAfHttp
import com.lvhttp.net.param.createPart
import com.lvhttp.net.response.resultMain
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        test.setOnClickListener {
            launchAfHttp {
                get()
//                post()
//                upload()
            }
        }

        downloadButton.setOnClickListener {
            launchAfHttp {
                dowload()
            }
        }

    }

    private suspend fun get() {
        LvHttp.createApi(Service::class.java).get().resultMain {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun post() {
        LvHttp.createApi(Service::class.java).login("15129379467", "123456789")
            .resultMain(error = { _, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }) {
                Log.e("---------->", "post: ${it.toString()}")
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
                    //完成
                }
            })
    }

    private suspend fun upload() {
        val file1 = File(Environment.getExternalStorageDirectory().path, "/image1.png")
        val file2 = File(Environment.getExternalStorageDirectory().path, "/image2.png")

        /*val loadBean1 = LvHttp.createApi(Service::class.java)
            .postFile(
                *createParts(
                    mapOf("key1" to file1, "key2" to file2)
                )
            )*/

        val loadBean2 = LvHttp.createApi(Service::class.java)
            .postFile(createPart("key", file2))
    }
}

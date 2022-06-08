package com.lvhttp.test

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.donkingliang.imageselector.utils.ImageSelector
import com.donkingliang.imageselector.utils.UriUtils
import com.donkingliang.imageselector.utils.VersionUtils
import com.lvhttp.net.LvHttp
import com.lvhttp.net.download.DownResponse
import com.lvhttp.net.download.start
import com.lvhttp.net.launch.*
import com.lvhttp.net.param.createFileRequestBody
import com.lvhttp.net.param.createPart
import com.lvhttp.net.param.createParts
import com.lvhttp.net.param.createRequestBody
import com.lvhttp.net.response.ResultState
import com.lvhttp.test.response.ResponseData
import kotlinx.coroutines.launch
import java.io.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.test).setOnClickListener {

            lifecycleScope.launch {
                launchHttp {
                    Toast.makeText(this@MainActivity, "加载中", Toast.LENGTH_SHORT).show()
                    LvHttp.createApi(Service::class.java).get()
                }.toData {
                    Toast.makeText(this@MainActivity, "${it.data}", Toast.LENGTH_SHORT).show()
                }.toError {
                    //Error
                }

//                launchHttp {
//                    LvHttp.createApi(Service::class.java).get2()
//                }.toData {
//                    Toast.makeText(this@MainActivity, "${it.data}", Toast.LENGTH_SHORT).show()
//                }
//
//                multipleRequest()
            }
            //下载
            val downloadButton = findViewById<AppCompatButton>(R.id.downloadButton)
            downloadButton.setOnClickListener {
                lifecycleScope.launch {
                    launchHttp {
                        LvHttp.createApi(Service::class.java).download()
                            .start(object : DownResponse("LvHttp", "chebangyang.apk") {
                                override fun create(size: Float) {
                                    Log.e("-------->", "create:总大小 ${(size)} ")
                                }

                                @SuppressLint("SetTextI18n")
                                override fun process(process: Float) {
                                    downloadButton.setText("$process %")
                                }

                                override fun error(e: Exception) {
                                    e.printStackTrace()
                                    downloadButton.setText("下载错误")
                                }

                                override fun done(file: File) {
                                    //完成
                                    Toast.makeText(this@MainActivity, "成功", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                    }
                }
            }


        }
    }

    private fun multipleRequest() {
        //并发
        val list = arrayListOf<suspend () -> Bean>()
        (0..10).forEach { _ ->
            list.add {
                LvHttp.createApi(Service::class.java).get2()
            }
        }
        lifecycleScope.launch {
            zipLaunch(list) { list ->
                list.forEachIndexed { index, resultState ->
                    resultState.toData {
                        Log.e("---345--->$index", "${it}")
                    }.toError {
                        Log.e("---345--->", "${it.printStackTrace()}");
                    }
                }
            }
        }
    }

    private fun post() {
        lifecycleScope.launch {
            launchHttp {
                LvHttp.createApi(Service::class.java).login("15129379467", "147258369")
            }.toData {
                Toast.makeText(this@MainActivity, it.data.toString(), Toast.LENGTH_SHORT).show()
            }.toError {
                Log.e("---345--->", "${it.printStackTrace()}");
            }
        }
    }

    /**
     * 文件上传
     */
    private fun upload() {
        ImageSelector.builder()
//            .useCamera(true) // 设置是否使用拍照
//            .setSingle(true)  //设置是否单选
            .setCrop(true)
            .onlyTakePhoto(true)
            .start(this, 0x0001)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x0001 && data != null) {
            val array = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
            if (array != null && array.isNotEmpty()) {
                val file = File(array[0])
                Log.e("-------", array[0])

                Glide.with(this)
                    .load(file)
                    .into(findViewById(R.id.image))


//            val requestBody = createFileRequestBody(file)

                lifecycleScope.launch {
                    launchHttp {
                        LvHttp.createApi(Service::class.java)
                            .postFile(*createParts(mapOf("key" to file, "key2" to file)))
                    }.toData {
                        Toast.makeText(this@MainActivity, "成功", Toast.LENGTH_SHORT).show()
                    }.toError {
                        Toast.makeText(this@MainActivity, "失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }


    fun copyUriToExternalFilesDir(uri: Uri, fileName: String): File? {
        val inputStream = contentResolver.openInputStream(uri)
        val tempDir = getExternalFilesDir("temp")
        if (inputStream != null && tempDir != null) {
            val file = File("$tempDir/$fileName")
            val fos = FileOutputStream(file)
            val bis = BufferedInputStream(inputStream)
            val bos = BufferedOutputStream(fos)
            val byteArray = ByteArray(1024)
            var bytes = bis.read(byteArray)
            while (bytes > 0) {
                bos.write(byteArray, 0, bytes)
                bos.flush()
                bytes = bis.read(byteArray)
            }
            bos.close()
            fos.close()
            return file
        }
        return null
    }

}

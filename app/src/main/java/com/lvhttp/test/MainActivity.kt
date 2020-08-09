package com.lvhttp.test

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.donkingliang.imageselector.utils.ImageSelector
import com.donkingliang.imageselector.utils.UriUtils
import com.donkingliang.imageselector.utils.VersionUtils
import com.lvhttp.net.LvHttp
import com.lvhttp.net.download.DownResponse
import com.lvhttp.net.download.start
import com.lvhttp.net.launch.launchAfHttp
import com.lvhttp.net.param.createPart
import com.lvhttp.net.response.resultMain
import com.lvhttp.net.utils.FileQUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        test.setOnClickListener {
//            launchAfHttp {
//                get()
//                post()
            upload()
//            }
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
        launchAfHttp {
            LvHttp.createApi(Service::class.java)
                .login("15129379467", "123456789")
                .resultMain {
                    Log.e("---------->", "post: ${it.toString()}")
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
                    //完成
                }
            })
    }

    private fun upload() {
        ImageSelector.builder()
            .useCamera(true) // 设置是否使用拍照
            .setSingle(true)  //设置是否单选
            .setCrop(true)
            .start(this, 0x0001)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x0001 && data != null) {
            val array = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
            val file = File(array[0])
            Log.e("-------", array[0])

            Glide.with(this)
                .load(file)
                .into(image)


            launchAfHttp {
                val loadBean2 = LvHttp.createApi(Service::class.java)
                    .postFile(createPart("key", file!!))
                Log.e("------------->", "upload: $loadBean2")
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

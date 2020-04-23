package com.www.lvhttp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.www.net.LvCreator
import com.www.net.LvHttp
import com.www.net.download.OnStateListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        LvCreator.init("https://www.wanandroid.com/")
//            .log(false)
        LvCreator.init("http://192.168.43.80:80/")
            .log(true)

//        LvCreator.init("https://www.nuli100.com/JSCM_PD/")
//            .log(true)
//        val map = mutableMapOf<String, String>()
//        map["Cookie"] = "loginUserName=345;token_pass=5d9b90bcb70640183e09d1e755ead823"
//        LvHttp.post()
//            .addUrl("lg/collect/47864/json")
//            .addParam(mutableMapOf())
//            .addHeader(map)
//            .send {
//                Log.e("----------", it.value)
//            }

        //{"code":"ok","value":"789","a":"789","b":"789","c":"789"}


        test.setOnClickListener {

            LvHttp.postFile("test/updata.php")
                .setFile(getUri(this))
                .addParam("dir", "shops")
                .send {
                    Log.e("----------", it.value)
                }
           /* LvHttp.get()
                .addUrl("test/test.php")
                .send {
                    Log.e("00000", it.value)
                }*/


        }


//        zip()
    }

    private fun getUri(context: Context): File {
        val bitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.android
        )
        val file = File(context.externalCacheDir, "shareImage.png")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        return file
        /*  return FileProvider.getUriForFile(
              context,
              "${context.packageName}.fileprovider", file
          )*/
    }

    private fun zip() {
        LvHttp.zip(Pair({
            LvHttp.get().addUrl("index.php")
                .addParam("m", "App")
                .addParam("c", "APIUsersNewCar")
                .addParam("a", "carDetail")
                .addParam("articleType", "0")
                .addParam("p", 1)
                .send()
        }, {
            LvHttp.get().addUrl("index.php")
                .addParam("m", "App")
                .addParam("c", "APIUsersNewCar")
                .addParam("a", "carDetail")
                .addParam("articleType", "0")
                .addParam("p", 1)
                .send()
        })) {
            if (it.first != null) {
//                var format = it.first?.format(MainActivity::class.java)
//                Log.e("000000000000", format.toString())
            }
        }
    }

    //读写权限

    val PERMISSIONS_STORAGE: Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    //请求状态码
    val REQUEST_PERMISSION_CODE: Int = 1;


    fun getContext(): Context {
        return this
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE
                )
            }
        }


        fileUrl.setText("https://kotlinlang.org/docs/kotlin-docs.pdf")
        fileName.setText("Kotlin-Docs.pdf")
        downloadButton.setOnClickListener {
            LvHttp.download(this,
                fileUrl.text.toString(),
                fileName.text.toString(),
                Environment.getExternalStorageDirectory().path,
                object : OnStateListener {
                    override fun start() {
                        Toast.makeText(getContext(), "开始下载", Toast.LENGTH_LONG).show()
                    }

                    override fun process(value: Int) {
                        downloadButton.text = "Downloading $value"
                    }

                    override fun error(throwable: Throwable) {
                        Log.e("-----------", throwable.message!!)
                        Toast.makeText(getContext(), "下载出错：${throwable.message}", Toast.LENGTH_LONG)
                            .show()
                        downloadButton.text = "DownLoad"
                    }

                    override fun donal(file: File) {
                        downloadButton.text = "下载成功"
                        downloadPath.setText(file.absolutePath)
                        Toast.makeText(getContext(), "下载完成：" + file.path, Toast.LENGTH_LONG).show()
                    }


                })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            var i = 0
            permissions.forEach {
                Log.i("MainActivity", "申请的权限为：" + it + ",申请结果：" + grantResults[i++]);
            }
        }
    }

}

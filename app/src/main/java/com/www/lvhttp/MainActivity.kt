package com.www.lvhttp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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


//        val path = Environment.getExternalStorageDirectory()
//        val file1 = File(path.path + "/image1.png")
//        val file2 = File(path.path + "/image2.png")
//        val file3 = File(path.path + "/image3.jpg")
//        val file4 = File(path.path + "/image4.jpg")
//        val file5 = File(path.path + "/image5.jpg")
//        val file3 = getUri(R.drawable.image4, "3789")


        test.setOnClickListener {
//            val mutableMap = mutableMapOf<String, File>()
//            mutableMap["456"] = file1
//            mutableMap["1233"] = file2
//            mutableMap["789"] = file3
//            mutableMap["789123"] = file4
//            mutableMap["7843259"] = file5
//
//
//
//            LvHttp.uploadMapFile("test/updata.php")
//                .files(mutableMap)
//                .addParam("abs", "345")
//                .send {
//                    Log.e("-------->", "onCreate: ${it.value}")
//                }

            LvHttp.get()
                .addUrl("https://www.baidu.com")
                .send {
                    Toast.makeText(this, it.value, Toast.LENGTH_SHORT).show()
                }

            //https://www.nuli100.com/JSCM_PD/index.php?m=App&c=Base&a=uploadPicdir=users&Filedata=FILE
//            if (file.exists()) {
            /* LvHttp
                 .uploadMapFile("index.php")
                 .file("Filedata", file)
                 .addParam("m", "App")
                 .addParam("c", "Base")
                 .addParam("a", "uploadPic")
                 .addParam("dir", "users")
                 .send({
//                         https://www.nuli100.com/JSCM_PD/index.php?m=App&c=APIUsers&a=editUserPhoto&tokenId=4f9aa4a019d142dd8741ae6a0ec26d3cc0a&userPhoto=Upload/users/2020-05/5ec77db438782.jpg
                     LvHttp.get()
                         .addUrl("https://www.nuli100.com/JSCM_PD/index.php?m=App&c=APIUsers&a=editUserPhoto&tokenId=4f9aa4a019d142dd8741ae6a0ec26d3cc0a&userPhoto=Upload/users/2020-05/5ec77df7f348a.jpg")
                         .send {
//                                  {"status":1,"Filedata":{"savepath":"Upload\/users\/2020-05\/","savethumbname":"5ec77f21d07c6_thumb.jpg","savename":"5ec77f21d07c6.jpg"}}
                         }

                 }) {
                     Toast.makeText(this, "上传失败", Toast.LENGTH_LONG).show()
                 }*/
//            }
        }
//        zip()
    }

    private fun getUri(resId: Int, string: String): File {
        val bitmap = BitmapFactory.decodeResource(
            resources,
            resId
        )
        val file = File(externalCacheDir, "${string}.png")
        if (file.exists()) {
            return file
        }
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
            LvHttp.get()
                .addUrl("index.php")
                .addParam("m", "App")
                .addParam("c", "APIUsersNewCar")
                .addParam("a", "carDetail")
                .addParam("articleType", "0")
                .addParam("p", 1)
                .send()
        }, {
            LvHttp.get()
                .addUrl("index.php")
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
                Log.e("Toast------>  ", "")
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

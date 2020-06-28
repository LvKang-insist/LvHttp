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
import com.www.net.LvHttp
import com.www.net.download.DownResponse
import com.www.net.download.start
import com.www.net.file.createFilesParts
import com.www.net.launchAfHttp
import com.www.net.resultMain
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        test.setOnClickListener {

            //https://www.nuli100.com/JSCM_PD/index.php?m=App&c=APIMall&a=getGoodsList&currPage=1&goodsSrc=1
//            val client = OkHttpClient.Builder().build()
//            val request = Request.Builder()
//                .url("https://www.baidu.com")
//                .build()
//            val call = client.newCall(request)
//
//            val response = call.execute()

            launchAfHttp {
//                LvHttp.createApi(Service::class.java).get().resultMain {
//                    Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
//                }

                val path = Environment.getExternalStorageDirectory()
                val file1 = File(path.path + "/image1.png")
                val file2 = File(path.path + "/image2.png")
                val file3 = File(path.path + "/image3.jpg")
                val file4 = File(path.path + "/image4.jpg")
                val file5 = File(path.path + "/image5.jpg")
                val mutableMap = mutableMapOf<String, File>()
                mutableMap["456"] = file1
//                mutableMap["1233"] = file2
//                mutableMap["789"] = file3
//                mutableMap["789123"] = file4
//                mutableMap["7843259"] = file5
                val postFile = LvHttp.createApi(Service::class.java)
                    .postFile("http://192.168.43.253:80/test/updata.php", *createFilesParts(mutableMap))

                withContext(Dispatchers.Main) {
                    Log.e("-----上传---->", "onCreate: ${postFile.toString()}")
                }

                /* LvHttp.createApi(Service::class.java).download()
                     .start(object : DownResponse("LvHttp", "Kotlin.pdf") {
                         override fun create(size: Float) {
                             Log.e("-------->", "create:总大小 ${(size)} ")
                         }

                         override fun process(process: Int) {
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
                     })*/
            }
        }

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

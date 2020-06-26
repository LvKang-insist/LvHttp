package com.www.lvhttp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.www.net.LvHttp
import com.www.net.launchAfHttp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class MainActivity : AppCompatActivity() {


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
                val bean = LvHttp.createApi(Service::class.java).get()
                Log.e("---------->", "onCreate: ${bean.data}")
                withContext(Dispatchers.Main) {
                    bean.parse("errorCode", 0, {
                        Toast.makeText(this@MainActivity, "code 过期 ，请重新登录", Toast.LENGTH_SHORT)
                            .show()
                    }) {
                        Toast.makeText(this@MainActivity, bean.data.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }


            //https://www.nuli100.com/JSCM_PD/index.php?m=App&c=Base&a=uploadPicdir=users&Filedata=FILE
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

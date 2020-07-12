### LiveHttp

​	基于 Retrofit + 协程的网络请求组件

------

### 特性

- 适配在 Activity / Fragment / ViewMoel 的使用，防止内存泄露
- 完善异常处理机制，可自定义处理请求过程中的异常，或者统一处理全局异常等。支持数据异常处理全局处理，如 code 异常。
- 一键式设置缓存大小，是否开启缓存，打印日志等
- 对文件**上传/下载**做了丰富的支持，一键式上传下载
- 支持自定义 Response，格式参考 ResponseData
- 支持在请求接口中直接返回 Bean 类。
- 支持 Ktx 扩展

### 如何使用：

```kotlin
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```groovy
dependencies {
	  implementation 'com.github.LvKang-insist:LvHttp:1.1.4'
}
```

### 请自行导入以下组件

```groovy
//网络请求依赖
implementation 'com.squareup.okhttp3:okhttp:4.3.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.3.0'
implementation 'com.squareup.retrofit2:retrofit:2.7.2'
implementation 'com.squareup.retrofit2:converter-gson:2.7.2'
//协程基础库
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5"
//协程 Android 库，提供 UI 调度器
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.4"
```

### 关于启动方式

库中提供了三种请求的方式：

- launchAfHttp

   适用于在 Activity/Fragment 中调用。当 actvity 销毁后，该请求会被取消

- launchVmHttp

   适用于在 ViewModel 中调用。当 ViewModel 被清除时，该请求会被取消

- launchHttp

   没有做处理的请求。通常情况下，不推荐使用这种方式，可能会造成内存泄漏*  如：UI 已经被销毁了，而耗时操作没有完成

- 直接使用 LvHttp 进行请求

   这种方式是非常不推荐的，因为没有对异常进行处理，任何网络异常都有可能会导致程序崩溃

### 初始化

```kotlin
LvHttp.Builder()
    .setApplication(this)
    .setBaseUrl("https://api.github.com/")
	//是否开启缓存
    .isCache(false)
	//是否打印 log
    .isLoging(true)
	//初始化请求接口
    .setService(Service::class.java)
	//对 Code 异常的处理，可自定义,参考 ResponseData 类
    .setErrorDispose(ErrorKey.ErrorCode, ErrorValue {
        Toast.makeText(this, "Code 错误", Toast.LENGTH_SHORT).show()
    })
	//全局异常处理，参考 Launch.kt ，可自定义异常处理，参考 ErrorKey 即可
    .setErrorDispose(ErrorKey.AllEexeption, ErrorValue {
        it.printStackTrace()
        Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show()
    })
    .build()
```

------

### Get

```kotlin
/**
 * 普通请求
 */
@GET("https://wanandroid.com/wxarticle/chapters/json")
suspend fun get(): ResponseData<Bean>
```

```kotlin
launchAfHttp {
    //resultMain 将结果转到主线程
    //resultIO 将结果转到 IO线程
    //block 不做任何处理
    LvHttp.createApi(Service::class.java).get().resultMain(
        error = {
         //异常处理
        Toast.makeText(this@MainActivity, "网络异常", Toast.LENGTH_SHORT).show()
    	}
    ) {
        //拿到数据
        Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
    }
}
```

当然，异常可以交给全局去处理，如下

```kotlin
launchAfHttp {
    LvHttp.createApi(Service::class.java).get().resultMain {
        Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
    }
}
```

如上，即可完成 Get 请求

### POST

```kotlin
@FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") userName: String,
        @Field("password") passWord: String
    ): ResponseData<Bean>
```

```kotlin
LvHttp.createApi(Service::class.java).login("15129379467", "123456")
            .resultMain(error = { _, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }) {
                Log.e("---------->", "post: ${it.toString()}")
            }
```

看起来有点麻烦，是因为处理了异常，其实异常可以在初始化的时候指定需要处理的异常，或者是处理全局异常。在初始化中处理了异常之后，可以使用非常简洁的方式，如下：

```kotlin
launchAfHttp {
    LvHttp.createApi(Service::class.java).login("15129379467", "123456789")
                .resultMain {
                    Log.e("---------->", "post: ${it.toString()}")
                }
}
```

### 文件下载

```kotlin
@Streaming
@GET("https://www.nuli100.com/CBY_PD/Public/appapk/app_customer.apk")
suspend fun download(): ResponseBody
```

```kotlin
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
```

其中四个方法可根据需要进行重写
在 Android Q 中：path 表示的路径为 根目录/dowload/path/name
在 Android Q 以下，path 表示的是 根目录/path/name
注意：name 后面需要加上后缀名

### 文件上传

```kotlin
/**
 * post：文件，支持一个或者多个文件
 */
@Multipart
@POST("http://192.168.43.253:80/test/updata.php")
suspend fun postFile(@Part vararg file: MultipartBody.Part): UpLoadBean
```

```kotlin
val file1 = File(Environment.getExternalStorageDirectory().path, "/image1.png")
    val loadBean2 = LvHttp.createApi(Service::class.java)
            .postFile(createPart("key", file2))
```

上传多个文件

```kotlin
 val file1 = File(Environment.getExternalStorageDirectory().path, "/image1.png")
 val file2 = File(Environment.getExternalStorageDirectory().path, "/image2.png")
        
 val loadBean = LvHttp.createApi(Service::class.java)
     .postFile(
          *createParts(
               mapOf("key1" to file1, "key2" to file2)
           )
      )
```



### 自定义 Response

​	参考 [ResponseData](https://github.com/LvKang-insist/LvHttp/blob/master/net/src/main/java/com/www/net/response/ResponseData.kt) 更改即可

​	其中 [ResponseData](https://github.com/LvKang-insist/LvHttp/blob/master/net/src/main/java/com/www/net/response/ResponseData.kt) 对应的数据类型为 [Data](https://wanandroid.com/wxarticle/chapters/json)

​	ResponseData 中的 T 可参考 [Bean](https://github.com/LvKang-insist/LvHttp/blob/master/app/src/main/java/com/www/lvhttp/Data.kt)

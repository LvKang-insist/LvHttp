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
- 支持请求结果的状态管理

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
	  implementation 'com.github.LvKang-insist:LvHttp:1.1.5'
}
```

### 请自行导入以下组件

```groovy
//网络请求依赖
    //网络请求依赖
    implementation "com.squareup.okhttp3:okhttp:4.9.0"
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    //协程基础库
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9"
    //协程 Android 库，提供 UI 调度器
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9"

    // viewmodel / activity 的ktx扩展
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0"
```

### 关于混淆
```
    -keep class com.lvhttp.net.response.** { *; }
```
如果你自定义了 Response ，那么自定义的那个类也需要混淆，切记

### 关于启动方式

详见 [launch](https://github.com/LvKang-insist/LvHttp/blob/master/net/src/main/java/com/lvhttp/net/launch/Launch.kt)

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
@GET("wxarticle/chapters/json")
suspend fun get(): ResponseData<ArticleBean>
```

```kotlin
launchAf({
	LvHttp.createApi(Service::class.java).get()
    }) {
	when (it) {
	    is ResultState.SuccessState -> Toast.makeText(this, it.t.toString(), Toast.LENGTH_SHORT).show()
	    is ResultState.ErrorState -> Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show()
	    is ResultState.LoadingState -> Toast.makeText(this, "加载中", Toast.LENGTH_SHORT).show()
	}
    }
```

请求结果分为三个状态，成功，失败和加载中

```kotlin
@GET("wxarticle/chapters/json")
suspend fun get2(): Bean
```
如上，支持不添加数据包装类。
```kotlin
launchAfHttp({
   	 LvHttp.createApi(Service::class.java).get2()
}) {
when (it) {
   	 is ResultState.SuccessState -> Toast.makeText(this, it.t.toString(), Toast.LENGTH_SHORT).show()
   	 is ResultState.ErrorState -> Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show()
   	 is ResultState.LoadingState -> Toast.makeText(this, "加载中", Toast.LENGTH_SHORT).show()
   }
}
```

如上，即可完成 Get 请求

如果判断太麻烦，可以使用另一种方式来拿到请求结果，如下:

```
launchAf({ LvHttp.createApi(Service::class.java).get() }) { state ->
    state.toData({ showLoading() }) {
        it?.data?.run {
            Toast.makeText(this@MainActivity, this.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
```
使用上面这种方式需要手动的判断请求结果是否成功


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
launchAf({
    LvHttp.createApi(Service::class.java).login("15129379467", "147258369")
}) {
    when(it){
	is ResultState.SuccessState -> TODO()
	is ResultState.ErrorState -> TODO()
	is ResultState.LoadingState -> TODO()
    }
}
```

### 全局异常处理
在 application 中初始化的时候调用 setErrorDispose 拦截异常，可拦截的异常件 [ErrorKey](https://github.com/LvKang-insist/LvHttp/blob/master/net/src/main/java/com/lvhttp/net/error/ErrorKey.kt) 这个类，

拦截方式如下：

```kotlin
.......
  .setCode(1)
  .setErrorDispose(ErrorKey.ErrorCode, ErrorValue {
	Log.e("345：", "Code 错误")
	Toast.makeText(this, "Code 错误", Toast.LENGTH_SHORT).show()
    })
    .setErrorDispose(ErrorKey.AllEexeption, ErrorValue {
	it.printStackTrace()
	Log.e("345：", "网络错误")
	Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show()
    })

```
setCode 是设置正确的 code，如果不等于 setCode 值，就会进行 ErrorCode 异常处理
AllExeption 是所有异常都会调用到这里(不包括code异常)
更多异常详见 ErrorKey

### 文件下载

```kotlin
@Streaming
@GET("https://files.pythonhosted.org/packages/6b/34/415834bfdafca3c5f451532e8a8d9ba89a21c9743a0c59fbd0205c7f9426/six-1.15.0.tar.gz")
suspend fun download(): ResponseBody
```

```kotlin
launchAfHttp({
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
		    Toast.makeText(this@MainActivity, "成功", Toast.LENGTH_SHORT).show()
		}
	    })
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
  launchAfHttp({
	LvHttp.createApi(Service::class.java).postFile(createPart("key", file))
    }) {
	when (it) {
	    is ResultState.SuccessState -> Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show()
	    is ResultState.ErrorState -> Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show()
	    is ResultState.LoadingState -> Toast.makeText(this, "加载中", Toast.LENGTH_SHORT).show()
	}
    }
```

上传多个文件

```kotlin
 val file1 = File(Environment.getExternalStorageDirectory().path, "/image1.png")
 val file2 = File(Environment.getExternalStorageDirectory().path, "/image2.png")

  launchAfHttp({
	LvHttp.createApi(Service::class.java).postFile(*createParts(mapOf("key" to file, "key2" to file2)))
    }) {
	when (it) {
	    is ResultState.SuccessState -> Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show()
	    is ResultState.ErrorState -> Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show()
	    is ResultState.LoadingState -> Toast.makeText(this, "加载中", Toast.LENGTH_SHORT).show()
	}
    }
```



### 自定义 Response

​	参考 [ResponseData](https://github.com/LvKang-insist/LvHttp/blob/master/net/src/main/java/com/www/net/response/ResponseData.kt) 更改即可

​	其中 [ResponseData](https://github.com/LvKang-insist/LvHttp/blob/master/net/src/main/java/com/www/net/response/ResponseData.kt) 对应的数据类型为 [Data](https://wanandroid.com/wxarticle/chapters/json)

​	ResponseData 中的 T 可参考 [Bean](https://github.com/LvKang-insist/LvHttp/blob/master/app/src/main/java/com/www/lvhttp/Data.kt)

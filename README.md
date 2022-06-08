### LiveHttp

​	基于 Retrofit + 协程的网络请求组件，适用于以 `Kotlin` 开发的项目。

------

### 特性

- 适配在 Activity / Fragment / ViewMoel 的使用，防止内存泄露
- 完善异常处理机制，可自定义处理请求过程中的异常，或者统一处理全局异常等。支持数据异常处理全局处理，如 code 异常。
- 一键式设置缓存大小，是否开启缓存，打印日志等
- 对文件**上传/下载**做了丰富的支持，一键式上传下载
- 支持自定义 Response，格式参考 ResponseData
- 支持在请求接口中直接返回 Bean 类。
- 支持 Ktx 扩展
- 支持证书验证
- 完善的状态管理机制，使用非常简单

### 依赖：

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
	  implementation 'com.github.LvKang-insist:LvHttp:1.1.9'
}
```

### 请自行导入以下组件

```groovy
//网络请求依赖
    //网络请求依赖
    implementation 'com.squareup.okhttp3:okhttp:4.9.3'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    //协程基础库
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1"
    //协程 Android 库，提供 UI 调度器
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1"
    // viewmodel / activity 的ktx扩展
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.4.1"
```

### 关于混淆
```
    -keep class com.lvhttp.net.response.** { *; }
```
如果你自定义了 Response ，那么自定义的那个类也需要混淆，切记

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
	//验证证书
    .setCerResId(if (BuildConfig.DEBUG) R.raw.ca_debug else R.raw.ca_release)
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

### 使用方式

如果你是在 `Activity` 中或者 `ViewModel` 等地方使用，请在请求的外层加上他们的协程作用域，这样可以有效的解决很多副作用，例如 	`lifecycleScope.launch{}` 、`viewModelScope.launch` 等。

```kotlin
/**
 * 普通请求
 */
@GET("wxarticle/chapters/json")
suspend fun get(): ResponseData<ArticleBean>
```

```kotlin
lifecycleScope.launch {
    launchHttp {
        Toast.makeText(this@MainActivity, "加载中", Toast.LENGTH_SHORT).show()
        LvHttp.createApi(Service::class.java).get()
    }.toData {
        Toast.makeText(this@MainActivity, "${it.data}", Toast.LENGTH_SHORT).show()
    }.toError {
        Log.e("---345--->", "${it.printStackTrace()}");
    }
    //Stop Loading
    Toast.makeText(this@MainActivity, "完成", Toast.LENGTH_SHORT).show()
}
```

请求结果分为两个，分别是 `toData` 表示成功，`tpError`表示失败

> 需要注意的是，如果请求过程中失败，如果启用了全局异常，那么全局异常和 toError 都会被调用，全局异常可用于吐司提示用户或者异常上报等，toError 可以用于修改 UI 状态等

上面的请求的时候对数据进行了包装，也就是 `ResponseData` ，继承自 `BaseResponse`。

你可以参照自己项目中的统一的数据格式来定制包装类，定义的包装类都需要继承 `BaseResponse` 。这样可以有效的进行 Code 验证等其他的操作。

如果有些接口返回的数据违背了项目规定的数据格式，例如项目中调用了第三方的接口，返回的格式和项目规定的格式不同，则可以采取不添加包装类的写法。在定义请求接口方法的时候直接使用对象即可。如下所示：

```kotlin
@GET("wxarticle/chapters/json")
suspend fun get2(): Bean
```
如上，支持不添加数据包装类。
```kotlin
lifecycleScope.launch {
    launchHttp {
        LvHttp.createApi(Service::class.java).get2()
    }.toData {
        Toast.makeText(this@MainActivity, "${it.data}", Toast.LENGTH_SHORT).show()
    }
    //Stop Loading
    Toast.makeText(this@MainActivity, "完成", Toast.LENGTH_SHORT).show()
}
```

如上，即可完成 Get 请求

> 需要注意的是上面使用的是  `launchHttpPack` ，这种方式无包装类，所以无法自动验证 code，

### 全局异常处理
在 application 中初始化的时候调用 setErrorDispose 拦截异常，可拦截的异常见 [ErrorKey](https://github.com/LvKang-insist/LvHttp/blob/master/net/src/main/java/com/lvhttp/net/error/ErrorKey.kt) 这个类，

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

直接返回 `ResponseBody` 即可。库中对 `ResponseBody` 进行了扩展，具体使用方式如下：

```kotlin
@Streaming
@GET("https://files.pythonhosted.org/packages/6b/34/415834bfdafca3c5f451532e8a8d9ba89a21c9743a0c59fbd0205c7f9426/six-1.15.0.tar.gz")
suspend fun download(): ResponseBody
```

```kotlin
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
lifecycleScope.launch {
    launchHttp {
        LvHttp.createApi(Service::class.java).postFile(createPart("key", file))
    }.toData {
        Toast.makeText(this@MainActivity, "成功", Toast.LENGTH_SHORT).show()
    }.toError {
        Toast.makeText(this@MainActivity, "失败", Toast.LENGTH_SHORT).show()
    }
}
```

上传多个文件

```kotlin
 val file1 = File(Environment.getExternalStorageDirectory().path, "/image1.png")
 val file2 = File(Environment.getExternalStorageDirectory().path, "/image2.png")

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
```

### 自定义 Response

自定义 Response 需要继承 `BaseResponse`，并且实现 notifyData 方法，如下所示：

```kotlin
data class ResponseData<T>(val data: T, val code: Int, val errorMsg: String) :
    BaseResponse<T>() {
    override fun notifyData(): BaseResponse<T> {
        _data = data
        _code = code
        _message = errorMsg
        return super.notifyData()
    }
}
```

> 如果您有任何建议或者问题请联系，可直接联系我

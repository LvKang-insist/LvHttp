package com.lvhttp.test


import com.lvhttp.net.converter.Chunked
import com.lvhttp.net.response.BaseResponse
import com.lvhttp.net.response.ResponseData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface Service {
    /**
     * 普通请求
     */
    @GET("wxarticle/chapters/json")
    suspend fun get(): ResponseData<ArticleBean>

    @GET("wxarticle/chapters/json")
    suspend fun get2(): Bean

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") userName: String,
        @Field("password") passWord: String
    ): ResponseData<LoginBean>


    @Streaming
    @GET("https://files.pythonhosted.org/packages/6b/34/415834bfdafca3c5f451532e8a8d9ba89a21c9743a0c59fbd0205c7f9426/six-1.15.0.tar.gz")
    suspend fun download(): ResponseBody


    /**
     * post：文件
     */
    @Multipart
    @POST("http://192.168.23.253:80/test/updata.php")
    suspend fun postFile(@Chunked @Part vararg file: MultipartBody.Part): UpLoadBean

    @Multipart
    @POST("http://192.168.23.253:80/test/updata.php")
    suspend fun file(@Body requestBody: RequestBody): ResponseBody

    /**
     * post:请求参数+文件
     */
    @Multipart
    @POST
    suspend fun postFile(
        @Url url: String,
        @PartMap params: MutableMap<String, RequestBody>,
        @Part vararg file: MultipartBody.Part
    ): UpLoadBean

}
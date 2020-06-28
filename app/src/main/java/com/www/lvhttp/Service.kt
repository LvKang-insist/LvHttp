package com.www.lvhttp


import com.www.net.ResponseData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Service {
    /**
     * 普通请求
     */
    @GET("https://wanandroid.com/wxarticle/chapters/json")
    suspend fun get(): ResponseData<Bean>

    @GET("users/rengwuxian/repos")
    suspend fun baidu(): Response<String>

    @GET("https://kotlinlang.org/docs/kotlin-docs.pdf")
    suspend fun download(): ResponseBody

    /**
     * post：文件
     */
    @Multipart
    @POST
    suspend fun postFile(@Url url: String, @Part vararg file: MultipartBody.Part): UpLoadBean

    /**
     * post:请求参数+文件
     */
    @Multipart
    @POST
    suspend  fun postFile(
        @Url url: String,
        @PartMap params: MutableMap<String, RequestBody>,
        @Part vararg file: MultipartBody.Part
    ): UpLoadBean

}
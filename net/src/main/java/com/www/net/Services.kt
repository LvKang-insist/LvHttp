package com.www.net

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @name services
 * @package com.www.net
 * @author 345 QQ:1831712732
 * @time 2020/6/20 19:31
 * @description
 */
interface Services {

    /**
     * 普通请求
     */
    @GET
    fun get(@Url url: String): Result?

    /**
     * 带参请求
     */
    @GET
    fun get(@Url url: String, @QueryMap params: MutableMap<String, Any>): Result?

    /**
     * 携带 header
     */
    @GET
    fun getHeader(@Url url: String, @HeaderMap params: MutableMap<String, String>): Result?

    /**
     * 携带 header
     */
    @GET
    fun getHeader(@Url url: String, @QueryMap params: MutableMap<String, Any>, @HeaderMap headerMap: MutableMap<String, String>): Result?




    /**
     * 带参请求
     * @Body 即非表单请求体，被body 注解的将会被 Gson 转换为json 发送到服务器
     */
    @POST
    fun postRaw(@Url url: String, @Body body: RequestBody): Result?
    /**
     * 携带 header
     * @Body 即非表单请求体，被body 注解的将会被 Gson 转换为json 发送到服务器
     */
    @POST
    fun postRaw(@Url url: String, @HeaderMap headerMap: MutableMap<String, String>, @Body body: RequestBody): Result?


    /**
     * @FormUrlEncoded 表示请求体是一个Form 表单，
     */
    @FormUrlEncoded
    @POST
    fun post(@Url url: String, @FieldMap params: MutableMap<String, Any>): Result?

    /**
     * @FormUrlEncoded 表示请求体是一个Form 表单，
     */
    @FormUrlEncoded
    @POST
    fun postHeader(@Url url: String, @HeaderMap headerMap: MutableMap<String, String>, @FieldMap params: Map<String, Any>): Result?

    /**
     * 发送请求，带请求头，但是没有参数
     */
    @POST
    fun postHeader(@Url url: String, @HeaderMap headerMap: MutableMap<String, String>): Result?



    /**
     * post：文件
     */
    @Multipart
    @POST
    fun postFile(@Url url: String, @Part vararg file: MultipartBody.Part): Result?

    /**
     * post:请求参数+文件
     */
    @Multipart
    @POST
    fun postFile(
        @Url url: String,
        @PartMap params: MutableMap<String, RequestBody>,
        @Part vararg file: MultipartBody.Part
    ): Result?


    /**
     * post：参数+请求头+文件
     */
    @Multipart
    @POST
    fun postFileHeader(
        @Url url: String,
        @PartMap params: MutableMap<String, RequestBody>,
        @HeaderMap headerMap: MutableMap<String, String>,
        @Part vararg file: MultipartBody.Part
    ): Result?

    /**
     * post：请求头+文件
     */
    @Multipart
    @POST
    fun postFileHeader(
        @Url url: String,
        @HeaderMap headerMap: MutableMap<String, String>,
        @Part vararg file: MultipartBody.Part
    ): Result?


}
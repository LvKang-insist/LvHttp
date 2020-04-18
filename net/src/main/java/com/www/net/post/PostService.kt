package com.www.net.post

import com.www.net.Result
import okhttp3.RequestBody
import retrofit2.http.*

interface PostService {

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

}
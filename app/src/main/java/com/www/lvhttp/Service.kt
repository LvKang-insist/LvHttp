package com.www.lvhttp


import com.www.net.ResponseData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface Service {
    /**
     * 普通请求
     */
    @GET("https://wanandroid.com/wxarticle/chapters/json")
    suspend fun get(): ResponseData<Bean>

    @GET("users/rengwuxian/repos")
    suspend fun baidu(): Response<String>

    @GET("https://kotlinlang.org/docs/kotlin-docs.pdf")
    suspend fun downlog(): ResponseBody
}
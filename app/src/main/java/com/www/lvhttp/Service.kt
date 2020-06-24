package com.www.lvhttp

import com.www.net.Result
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface Service {
    /**
     * 普通请求
     */
    @GET("users/rengwuxian/repos")
    suspend fun get(): String

    @GET("users/rengwuxian/repos")
    suspend fun baidu(): Response<String>


    @GET("users/rengwuxian/repos")
    suspend fun hhhh(): Call<String>

}
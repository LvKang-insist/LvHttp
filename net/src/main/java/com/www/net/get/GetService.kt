package com.www.net.get

import com.www.net.Result
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface GetService {
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
}
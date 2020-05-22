package com.www.net.postFile

import com.www.net.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface PostFileService {

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
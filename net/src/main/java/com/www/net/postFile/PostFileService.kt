package com.www.net.postFile

import com.www.net.Result
import okhttp3.MultipartBody
import retrofit2.http.*

interface PostFileService {

    /**
     * post：文件
     */
    @Multipart
    @POST
    fun postFile(@Url url: String, @Part file: MultipartBody.Part): Result?

    /**
     * post:请求参数+文件
     */
    @Multipart
    @POST
    fun postFile(@Url url: String, @QueryMap params: MutableMap<String, Any>, @Part file: MultipartBody.Part): Result?


    /**
     * post：参数+请求头+文件
     */
    @Multipart
    @POST
    fun postFileHeader(@Url url: String, @PartMap params: MutableMap<String, Any>, @HeaderMap headerMap: MutableMap<String, String>, @Part file: MultipartBody.Part): Result?

    /**
     * post：请求头+文件
     */
    @Multipart
    @POST
    fun postFileHeader(@Url url: String, @HeaderMap headerMap: MutableMap<String, String>, @Part file: MultipartBody.Part): Result?

}
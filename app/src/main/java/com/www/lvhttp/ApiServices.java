package com.www.lvhttp;

import com.www.net.response.ResponseData;

import retrofit2.http.GET;

/**
 * @author 345 QQ:1831712732
 * @name ApiServices
 * @package com.www.lvhttp
 * @time 2020/6/30 20:47
 * @description
 */
interface ApiServices {
    /**
     * 普通请求
     */
    @GET("wxarticle/chapters/json")
    ResponseData<Bean> get();
}

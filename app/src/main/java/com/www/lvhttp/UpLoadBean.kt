package com.www.lvhttp
import com.google.gson.annotations.SerializedName


/**
 * @name UpLoadBean
 * @package com.www.lvhttp
 * @author 345 QQ:1831712732
 * @time 2020/6/28 23:43
 * @description
 */

//class UpLoadBean : ArrayList<UpLoadBean.UpLoadBeanItem>(){
    data class UpLoadBean(
        val error: String,
        val info: String, // ok
        val path: String // updata_image/file/20200628161411JSeM0.png
    )
//}
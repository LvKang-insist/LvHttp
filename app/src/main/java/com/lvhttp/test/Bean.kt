package com.lvhttp.test

/**
 * @name Bean
 * @package com.lvhttp.test
 * @author 345 QQ:1831712732
 * @time 2021/01/05 22:17
 * @description
 */
data class Bean(
    val `data`: List<Data>,
    val errorCode: Int,
    val errorMsg: String
)

data class Data(
    val children: List<Any>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
)
package com.www.lvhttp

/**
 * @name Data
 * @package com.www.lvhttp
 * @author 345 QQ:1831712732
 * @time 2020/6/26 22:47
 * @description
 */
data class Bean(
    val `data`: List<Data>,
    val errorCode: Int, // 0
    val errorMsg: String
) {
    data class Data(
        val children: List<Any>,
        val courseId: Int, // 13
        val id: Int, // 434
        val name: String, // Gityuan
        val order: Int, // 190013
        val parentChapterId: Int, // 407
        val userControlSetTop: Boolean, // false
        val visible: Int // 1
    )
}
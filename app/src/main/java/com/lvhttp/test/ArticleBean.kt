package com.lvhttp.test



/**
 * @name Data
 * @package com.www.lvhttp
 * @author 345 QQ:1831712732
 * @time 2020/6/26 22:47
 * @description
 */
class ArticleBean : ArrayList<ArticleBean.BeanItem>() {
    data class BeanItem(
        val children: List<Any>,
        val courseId: Int, // 13
        val id: Int, // 408
        val name: String, // 鸿洋
        val order: Int, // 190000
        val parentChapterId: Int, // 407
        val userControlSetTop: Boolean, // false
        val visible: Int // 1
    )
}
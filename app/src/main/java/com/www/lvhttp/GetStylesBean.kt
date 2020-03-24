package com.www.lvhttp

/**
 * Created by Administrator on 2018/7/25.
 */

data class GetStylesBean(
    val data: Data,
    val msg: String,
    val status: Int
)

data class Data(
    val adGallerys: List<AdGallery>,
    val arbImg: String,
    val arbThums: String,
    val backgroundImg: String,
    val backgroundThums: String,
    val changeBottleImg: String,
    val changeBottleThums: String,
    val changeTireImg: String,
    val changeTireThums: String,
    val changingGlassImg: String,
    val changingGlassThums: String,
    val cheapCarImg: String,
    val cheapCarThums: String,
    val cosmetologyImg: String,
    val cosmetologyThums: String,
    val decorateImg: String,
    val decorateThums: String,
    val fontColor: String,
    val id: String,
    val isDaily: String,
    val isDefault: String,
    val isUse: String,
    val maintainImg: String,
    val maintainThums: String,
    val moreImg: String,
    val moreThums: String,
    val renderingImg: String,
    val renderingThums: String,
    val rescueImg: String,
    val rescueThums: String,
    val styleEndDate: String,
    val styleFlag: String,
    val styleSort: String,
    val styleStartDate: String,
    val styleTheme: String,
    val usedCarImg: String,
    val usedCarThums: String,
    val valuationCarImg: String,
    val valuationCarThums: String,
    val violationInquiryImg: String,
    val violationInquiryThums: String,
    val washcarImg: String,
    val washcarThums: String
)

data class AdGallery(
    val adFile: String,
    val adId: String,
    val adName: String,
    val adPositionId: String,
    val adURL: String,
    val goodsId: String,
    val goodsType: String,
    val isBidding: String
)
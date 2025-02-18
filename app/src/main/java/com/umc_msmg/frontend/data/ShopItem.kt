package com.umc_msmg.frontend.data

data class  ShopItem(
    val name: String,
    val purchasingOffice: String,
    val price: Int,
    val imageResId: Int? = null
)
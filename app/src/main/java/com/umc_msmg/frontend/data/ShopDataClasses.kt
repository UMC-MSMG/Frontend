package com.umc_msmg.frontend.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShopItem(
    val id: Int,
    val name: String,
    val price: Int,
    val image: String?
) : Parcelable

data class GetProductsResponse(
    val products: List<ShopItem>,
    val message: String
)

data class GetGiftIconsResponse(
    val giftIcons: List<ShopItem>,
    val message: String
)

data class BuyProductRequest(
    val productId: Int
)

data class BuyProductResponse(
    val productId: Int,
    val updatedPoint: Int,
    val message: String
)

data class AddPointRequest(
    val points: Int
)

data class AddPointResponse(
    val userId: String,
    val updatedPoint: Int,
    val message: String
)
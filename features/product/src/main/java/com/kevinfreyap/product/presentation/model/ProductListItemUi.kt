package com.kevinfreyap.product.presentation.model

data class ProductListItemUi(
    val id: String,
    val name: String,
    val quantity: Int,
    val unit: String = "pcs",
    val stockLevel: StockLevel,
    val category: String,
    val imageUri: String?,
    val sku: String?
)

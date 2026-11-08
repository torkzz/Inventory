package com.kevinfreyap.product.presentation.state

data class ProductFormDetailState(
    val productImageUriString: String? = null,
    val productName: String = "",
    val brand: String? = null,
    val productCategoryName: String = "",
    val allCategories: List<String> = emptyList(),
    val filteredCategories: List<String> = emptyList(),
    val productDescription: String? = null,
    val unit: String = "pcs",
    val purchasePrice: String = "",
    val sellingPrice: String = "",
    val standaloneQuantity: String = "0",
    val productMinQuantity: String = "0",
    val isMinQuantityConfirmed: Boolean = false,
)

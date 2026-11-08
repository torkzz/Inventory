package com.kevinfreyap.product.presentation.navigation

sealed interface ProductListNavigation {
    object AddProduct: ProductListNavigation
    object BarcodeScanner: ProductListNavigation
    data class ProductDetail(val productId: String): ProductListNavigation
}